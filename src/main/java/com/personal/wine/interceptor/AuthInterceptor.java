package com.personal.wine.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate redisTemplate;

    private String blankList = "passwordLogin";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截处理代码
        System.out.println("拦截到了");
        if (request.getRequestURI().contains(blankList)) {
            return true;
        }
        //返回true通过，返回false拦截
        String headerToken = request.getHeader("token");
        if (redisTemplate.hasKey(headerToken)) {
            return true;
        } else {
            return false;
        }
    }
}
