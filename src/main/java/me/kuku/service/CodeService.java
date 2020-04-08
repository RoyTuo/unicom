package me.kuku.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CodeService {
    void creatVerifyCode(HttpServletRequest request, HttpServletResponse response);
    String getRandomCode(int len);
}
