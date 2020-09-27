//package com.personal.wine.interceptor;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//@Configuration
//public class WebAppConfig extends WebMvcConfigurerAdapter {
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry)
//    {
//        //注册自己的拦截器并设置拦截的请求路径
//        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**");
//        super.addInterceptors(registry);
//    }
//}
