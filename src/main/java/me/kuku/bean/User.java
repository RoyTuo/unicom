package me.kuku.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "user")
public class User {
    private int max;
    private HtmlCon htmlCon;
    private List<Key> key;
    private String api;
    private Integer type;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Key> getKey() {
        return key;
    }

    public void setKey(List<Key> key) {
        this.key = key;
    }

    public HtmlCon getHtmlCon() {
        return htmlCon;
    }

    public void setHtmlCon(HtmlCon htmlCon) {
        this.htmlCon = htmlCon;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
