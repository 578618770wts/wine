package com.personal.wine.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate redisTemplate;

    private String blankList = "passwordLogin";
    private String blankList1 = "register";
    private String blankList2 = "sendSMS";
    private String blankList3 = "loginSMS";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截处理代码


        if (request.getRequestURI().contains(blankList)) {
            return true;
        }
        if (request.getRequestURI().contains(blankList1)) {
            return true;
        }
        if (request.getRequestURI().contains(blankList2)) {
            return true;
        }
        if (request.getRequestURI().contains(blankList3)) {
            return true;
        }

        //返回true通过，返回false拦截
        String headerToken = request.getHeader("token");
        System.out.println("拦截到了 == headerToken ==" + headerToken);
        if (headerToken != null && redisTemplate.hasKey(headerToken)) {
            return true;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("token验证失败，返回404，请求token：{}", headerToken);
            }
            response.setStatus(404);
            return false;
        }
    }
}
