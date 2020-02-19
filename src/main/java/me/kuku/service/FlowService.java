package me.kuku.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class FlowService {
    private CloseableHttpClient httpClient = null;
    private BasicCookieStore basicCookieStore = null;

    public FlowService(){
        basicCookieStore = new BasicCookieStore();
        httpClient = HttpClients.custom().setDefaultCookieStore(basicCookieStore).build();
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // 0 不是联通号码  1  ok  -1  鬼知道什么原因
    public Integer getCaptcha(String phoneNumber){
        Integer code = 0;
        if (!checkUnicom(phoneNumber)) {
            return code;
        }
        HttpPost httpPost = new HttpPost("https://m.10010.com/god/AirCheckMessage/sendCaptcha");
        List<NameValuePair> param = new ArrayList<>();
        param.add(new BasicNameValuePair("phoneVal", phoneNumber));
        param.add(new BasicNameValuePair("type", "21"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(param, "utf8"));
            CloseableHttpResponse execute = httpClient.execute(httpPost);
            if (execute.getStatusLine().getStatusCode() == 200){
                String result = EntityUtils.toString(execute.getEntity(), "utf8");
                //{"RespCode":"10001","RespMsg":"号码:17673373494一天最多只能发送三次青啤流量兑换验证码短信。"}
                //{}
                //{"RespCode":"0000"}
//                System.out.println(result);
                JSONObject jsonObject = JSON.parseObject(result);
                if ("10001".equals(jsonObject.getString("RespCode"))){
                    return -1;
                }else{
                    return 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    public boolean receiveFlow(String number, String captcha){
        HttpGet httpGet = new HttpGet("https://m.10010.com/god/qingPiCard/flowExchange?number=" + number + "&type=21&captcha=" + captcha);
        boolean status = false;
        try {
            CloseableHttpResponse execute = httpClient.execute(httpGet);
            if (execute.getStatusLine().getStatusCode() == 200){
                String str = EntityUtils.toString(execute.getEntity(), "utf8");
                //{"respDesc":"验证码错误","respCode":"1001"}
                JSONObject jsonObject = JSON.parseObject(str);
                if (!"1001".equals(jsonObject.getString("respCode"))){
                    status = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    public boolean checkUnicom(String number){
        String regex = "^1[35678]\\d{9}$";
        boolean b = Pattern.matches(regex, number);
        return b;
    }

}
