package me.kuku.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import me.kuku.bean.User;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class LotteryService {

    private CloseableHttpClient httpClient = null;
    private BasicCookieStore basicCookieStore = null;
    private RequestConfig requestConfig = null;
    @Autowired
    User user;

    public LotteryService(){
        basicCookieStore = new BasicCookieStore();
        requestConfig = RequestConfig.custom().setConnectionRequestTimeout(10000).setSocketTimeout(10000)
                .setConnectionRequestTimeout(10000).build();
        httpClient = HttpClients.custom().setDefaultCookieStore(basicCookieStore).setDefaultRequestConfig(requestConfig).build();
    }

    public String getUserId(){
        RequestUtil.request(HttpGet.class, httpClient, "http://m.client.10010.com/sma-lottery/qpactivity/qingpiindex",
                null, String.class);
        String id = null;
        List<Cookie> cookies = basicCookieStore.getCookies();
        for (Cookie cookie : cookies){
            if (cookie.getName().equals("JSESSIONID")){
                id = cookie.getValue();
                break;
            }
        }
        return id;
    }

    public String getCaptchaUrl(String userId){
        return "http://m.client.10010.com/sma-lottery/qpactivity/getSysManageLoginCode.htm?userid=" + userId + "&code=" + new Date().getTime();
    }

    public byte[] getCaptchaImg(String userId){
        byte[] bytes = RequestUtil.request(HttpGet.class, httpClient, getCaptchaUrl(userId), null, byte[].class);
        return bytes;
    }

    //通过ocr的api调用
    public String getCaptcha(String captchaUrl){
        String str = RequestUtil.request(HttpPost.class, httpClient, user.getApi(),
                Arrays.asList(new BasicNameValuePair("url", captchaUrl)), String.class);
        return str;
    }

    //返回值为null  为验证码错误
    public String getMobile(String phone, String captcha, String userId){
        String str = RequestUtil.request(HttpPost.class, httpClient, "http://m.client.10010.com/sma-lottery/validation/qpImgValidation.htm",
                Arrays.asList(new BasicNameValuePair("mobile", phone),
                        new BasicNameValuePair("image", captcha),
                        new BasicNameValuePair("userid", userId)), String.class);
        String mobile = null;
        //这里经常会链接超时，，可能会出现空指针
        try {
            JSONObject jsonObject = JSON.parseObject(str);
            if (jsonObject.getString("code").equals("YES")){
                mobile = jsonObject.getString("mobile");
            }
        } catch (Exception e){
            return null;
        }
        return mobile;
    }

    public String lottery(String encryptPhone, String captcha, String userId){
        String result = RequestUtil.request(HttpPost.class, httpClient, "http://m.client.10010.com/sma-lottery/qpactivity/qpLuckdraw.htm",
                Arrays.asList(new BasicNameValuePair("mobile", encryptPhone),
                        new BasicNameValuePair("image", captcha),
                        new BasicNameValuePair("userid", userId)), String.class);
        String gift = null;
        //{"data":{"assetCategory":"0","assetName":"100MB","everyDayNum":"2000","id":"qp100","level":"6","price":"100","prizeCount":"17694885424"},"isunicom":true,"msg":"2","status":200}
        //{"data":{"assetName":"幸运奖","level":"3","prizeCount":"15529772351"},"isunicom":true,"msg":"1","status":0}
        //{"data":{"assetName":"幸运奖","level":"3","prizeCount":"15529772351"},"isunicom":true,"msg":"0","status":0}
        //看level的值
        //1、50mb流量   2、100mb流量  3、200mb流量  4、1000mb流量  5、20砖石  6、15元开卡红包  7、50元开卡红包
        //1、100mb流量  2、50元开卡礼包  3、幸运奖  4、50mb流量  5、10000mb流量  6、15元开卡礼包  7、20钻石
        //1、50mb流量  2、100mb流量  3、幸运奖  4、10000mb流量  5、20钻石  6、15元开卡礼包  7、50元开卡礼包
        //isunicom=false  错误 然后读msg
        //status = 200 or status = 0 正常
        //status = 500 没有抽奖次数了
        //status = 400 or status = 700 抽奖人数过多
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

    public String run(String phone, BaiDuAIService baiDuAIService) throws Exception{
        String code = null, mobile = null;
        String userId = getUserId();
        String captchaUrl = getCaptchaUrl(userId);
        while (true) {
            if (user.getType() == 0) code = getCaptcha(captchaUrl);
            else {
                byte[] captchaImg = getCaptchaImg(userId);
                code = baiDuAIService.Literacy(captchaImg, user);
            }
            mobile = getMobile(phone, code, userId);
            if (mobile == null) continue;
            else break;
        }
        String gifts = "";
        for (int i = 0; i < 3; i++) gifts += lottery(mobile, code, userId) + "；";
        return gifts;
    }

}
