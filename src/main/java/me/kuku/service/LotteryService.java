package me.kuku.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.ocr.AipOcr;
import me.kuku.bean.Key;
import me.kuku.bean.User;
import me.kuku.utils.OkHttpClientUtil;
import okhttp3.FormBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class LotteryService {

    private OkHttpClientUtil okHttpClientUtil;
    @Autowired
    User user;

    public LotteryService(){
        okHttpClientUtil = OkHttpClientUtil.getInstance();
    }

    //获取userId
    private String getUserId() throws IOException {
        Response response = okHttpClientUtil.get("http://m.client.10010.com/sma-lottery/qpactivity/qingpiindex");
        response.close();
        Map<String, String> map = okHttpClientUtil.getCookie(response, "JSESSIONID");
        return map.get("JSESSIONID");
    }

    //获取图片地址
    private String getCaptchaUrl(String userId){
        return "http://m.client.10010.com/sma-lottery/qpactivity/getSysManageLoginCode.htm?userid=" + userId + "&code=" + new Date().getTime();
    }

    //获取图片流
    private byte[] getCaptchaImg(String url) throws IOException {
        Response response = okHttpClientUtil.get(url);
        return Objects.requireNonNull(response.body()).bytes();
    }

    //通过ocr的api调用
    public String getCaptcha(String captchaUrl) throws IOException {
        Response response = okHttpClientUtil.post(user.getApi(), new FormBody.Builder().add("url", captchaUrl).build());
        return Objects.requireNonNull(response.body()).string();
    }

    //加密手机号
    //返回值为null  为验证码错误
    public String getMobile(String phone, String captcha, String userId) throws IOException {
        Response response = okHttpClientUtil.post("http://m.client.10010.com/sma-lottery/validation/qpImgValidation.htm", new FormBody.Builder()
                .add("mobile", phone)
                .add("image", captcha)
                .add("userid", userId).build());
        String str = Objects.requireNonNull(response.body()).string();
        String mobile = null;
        JSONObject jsonObject = JSON.parseObject(str);
        if (jsonObject.getString("code").equals("YES")){
            mobile = jsonObject.getString("mobile");
        }
        return mobile;
    }

    //抽奖
    public String lottery(String encryptPhone, String captcha, String userId) throws IOException {
        Response response = okHttpClientUtil.post("http://m.client.10010.com/sma-lottery/qpactivity/qpLuckdraw.htm", new FormBody.Builder()
                .add("mobile", encryptPhone)
                .add("image", captcha)
                .add("userid", userId).build());
        String result = Objects.requireNonNull(response.body()).string();
        String gift = null;
        JSONObject jsonObject = JSON.parseObject(result);
        Integer status = jsonObject.getInteger("status");
        if (status == 500){
            return "没有抽奖次数了";
        }else if (status == 400 || status == 700){
            return "抽奖人数过多";
        }else if (jsonObject.getBoolean("isunicom").equals(false)){
            return "不是联通号码";
        }else if (status == 200 || status == 0){
            String level = jsonObject.getJSONObject("data").getString("level");
            switch (level){
                case "1":
                    gift = "50mb流量";
                    break;
                case "2":
                    gift = "100mb流量";
                    break;
                case "3":
                    gift = "幸运奖";
                    break;
                case "4":
                    gift = "1000mb流量";
                    break;
                case "5":
                    gift = "20砖石";
                    break;
                case "6":
                    gift = "15元开卡礼包";
                    break;
                case "7":
                    gift = "50元开卡礼包";
                    break;
                default:
                    gift = "未知奖品";
            }
        }else{
            gift = "50元开卡礼包";
        }
        return gift;
    }

    public String run(AipOcr aipOcr, String phone){
        try {
            //首先获取用户id
            String userId = getUserId();
            String mobile, code;
            //取验证码url
            for (; ; ) {
                String captchaUrl = getCaptchaUrl(userId);
                if (user.getType() == 0) {
                    //ocr
                    code = getCaptcha(captchaUrl);
                } else {
                    //百度
                    org.json.JSONObject jsonObject = aipOcr.numbers(getCaptchaImg(captchaUrl), null);
                    if (jsonObject.has("error_msg")) return "百度ai次数超限";
                    JSONArray jsonArray = jsonObject.getJSONArray("words_result");
                    if (jsonArray.length() == 0) continue;
                    code = jsonArray.getJSONObject(0).getString("words");
                    if (code.length() != 4) continue;
                }
                mobile = getMobile(phone, code, userId);
                if (mobile == null)
                    continue;
                else break;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                String lottery = lottery(mobile, code, userId);
                sb.append(lottery).append("；");
            }
            System.out.println(sb.toString());
            return sb.toString();
        }catch (Exception e){
            return "抽奖失败";
        }
    }

}
