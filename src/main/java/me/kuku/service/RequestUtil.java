package me.kuku.service;

import com.alibaba.fastjson.JSON;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

public class RequestUtil {

    public static <T> T request(Class<?> clazz, CloseableHttpClient httpClient, String uri, List<BasicNameValuePair> params,
                                      Class<T> tClass){
        CloseableHttpResponse execute = null;
        try {
            Constructor<?> constructor = clazz.getConstructor(String.class);
            HttpRequestBase httpRequestBase = (HttpRequestBase) constructor.newInstance(uri);
            if (HttpGet.class != clazz){
                //如果不是HttpGet
                HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = (HttpEntityEnclosingRequestBase) httpRequestBase;
                if (params != null) {
                    httpEntityEnclosingRequestBase.setEntity(new UrlEncodedFormEntity(params, "utf8"));
                }
                execute = httpClient.execute(httpEntityEnclosingRequestBase);
            }else{
                //如果是HttpGet
                execute = httpClient.execute(httpRequestBase);
            }
            if (tClass == String.class){
                return (T) EntityUtils.toString(execute.getEntity(), "utf8");
            }else if (tClass == byte[].class){
                return (T) EntityUtils.toByteArray(execute.getEntity());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (execute != null) {
                try {
                    execute.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
