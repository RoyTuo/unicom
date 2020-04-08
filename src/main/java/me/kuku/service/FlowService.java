package me.kuku.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import me.kuku.utils.OkHttpClientUtil;
import okhttp3.FormBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@SessionScope
public class FlowService {
    private OkHttpClientUtil okHttpClientUtil;

    public FlowService(){
        okHttpClientUtil = OkHttpClientUtil.getInstance();
    }

    // 0 不是联通号码  1  ok  -1  鬼知道什么原因
    public Integer getCaptcha(String phoneNumber) throws IOException {
        if (!checkUnicom(phoneNumber)) {
            return 0;
        }
        Response response = okHttpClientUtil.post("https://m.10010.com/god/AirCheckMessage/sendCaptcha", new FormBody.Builder()
                .add("phoneVal", phoneNumber).add("type", "21").build());
        String result = Objects.requireNonNull(response.body()).string();
        JSONObject jsonObject = JSON.parseObject(result);
        if ("10001".equals(jsonObject.getString("RespCode"))){
            return -1;
        }else{
            return 1;
        }
    }

    public boolean receiveFlow(String number, String captcha) throws IOException {
        Response response = okHttpClientUtil.get("https://m.10010.com/god/qingPiCard/flowExchange?number=" + number + "&type=21&captcha=" + captcha);
        String str = Objects.requireNonNull(response.body()).string();
        boolean status = false;
        //{"respDesc":"验证码错误","respCode":"1001"}
        JSONObject jsonObject = JSON.parseObject(str);
        if (!"1001".equals(jsonObject.getString("respCode"))){
            status = true;
        }
        return status;
    }

    public boolean checkUnicom(String number){
        String regex = "^1[35678]\\d{9}$";
        boolean b = Pattern.matches(regex, number);
        return b;
    }

}
