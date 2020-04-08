package me.kuku.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OkHttpClientUtil {

    private final int TIME_OUT = 10000;
    private OkHttpClient okHttpClient;
    private static OkHttpClientUtil okHttpClientUtil = null;
//    private Map<String, List<Cookie>> cookieStore = new HashMap<>();

    private OkHttpClientUtil(){
        okHttpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
//                .cookieJar(new CookieJar() {
//                    @Override
//                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
//                        cookieStore.put(httpUrl.host(), list);
//                    }
//
//                    @NotNull
//                    @Override
//                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
//                        List<Cookie> cookies = new ArrayList<>();
//                        return cookies != null ? cookies : new ArrayList<Cookie>();
//                    }
//                })
                .build();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public static OkHttpClientUtil getInstance(){
        if (okHttpClientUtil == null){
            synchronized (OkHttpClientUtil.class){
                if (okHttpClientUtil == null){
                    okHttpClientUtil = new OkHttpClientUtil();
                }
            }
        }
        return okHttpClientUtil;
    }

    public Response get(String url, Headers headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();
        Response execute = okHttpClient.newCall(request).execute();
        return execute;
    }

    public Response get(String url) throws IOException {
        return this.get(url, new Headers.Builder().build());
    }

    public Response post(String url, RequestBody requestBody, Headers headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .headers(headers)
                .build();
        Response execute = okHttpClient.newCall(request).execute();
        return execute;
    }

    public Response post (String url, RequestBody requestBody) throws IOException{
        return this.post(url, requestBody, new Headers.Builder().build());
    }

    public Response post (String url, Headers headers) throws IOException{
        return this.post(url, new FormBody.Builder().build(), headers);
    }

    public Response put(String url, RequestBody requestBody, Headers headers) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .headers(headers)
                .build();
        Response execute = okHttpClient.newCall(request).execute();
        return execute;
    }

    public Response put (String url, RequestBody requestBody) throws IOException{
        return this.put(url, requestBody, new Headers.Builder().build());
    }

    public Response put (String url, Headers headers) throws IOException{
        return this.put(url, new FormBody.Builder().build(), headers);
    }

    public Headers addCookie(String cookie){
        return this.addHeader("cookie", cookie);
    }

    public Headers addReferer(String url){
        return this.addHeader("Referer", url);
    }

    public Headers addHeader(String name, String value){
        return new Headers.Builder().add(name, value).build();
    }

    public Map<String, String> getCookie(Response response, String...name){
        Map<String, String> map = new HashMap<>();
        List<String> headers = response.headers("Set-Cookie");
        for (String cookie : headers){
            cookie = cookie.substring(0, cookie.indexOf(';'));
            String[] split = cookie.split("=");
            if (split.length == 1) continue;
            for (String str : name){
                if (str.equals(split[0]))
                    map.put(str, split[1]);
            }
        }
        return map;
    }

    public String getCookie(Response response){
        StringBuilder sb = new StringBuilder();
        List<String> headers = response.headers("Set-Cookie");
        for (String str : headers){
            str = str.substring(0, str.indexOf(';'));
            sb.append(str).append("; ");
        }
        return sb.toString();
    }
}
