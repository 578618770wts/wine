package com.personal.wine.controller;

import com.alibaba.fastjson.JSONObject;
import com.personal.wine.base.Response;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LogAspect {

    private JSONObject jsonObject = new JSONObject();

    //用来记录请求进入的时间，防止多线程时出错，这里用了ThreadLocal
    ThreadLocal<Long> startTime = new ThreadLocal<>();
    /**
     * 定义切入点，controller下面的所有类的所有公有方法，这里需要更改成自己项目的
     */
    @Pointcut("execution(public * com.personal.wine.controller..*.*(..))")
    public void requestLog(){};

    /**
     * 方法之前执行，日志打印请求信息
     * @param joinPoint joinPoint
     */
    @Before("requestLog()")
    public void doBefore(JoinPoint joinPoint) throws IOException {
        startTime.set(System.currentTimeMillis());
        //定时任务不走  aop
        if (!joinPoint.getSignature().getDeclaringType().getSimpleName().contains("UserVipTimedController")){
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            //打印当前的请求路径
            //String bodyData = getBodyData(request);
            //System.out.println(bodyData);
            log.info("============================请求内容============================");
            // 打印请求内容
            log.info("请求地址===============:" + request.getRequestURL().toString());
            log.info("请求方式==:" + request.getMethod());
            log.info("请求类方法==:" + joinPoint.getSignature());

            log.info("请求参数===============RequestParam:{}", Arrays.toString(joinPoint.getArgs()));
        }


    }

    /**
     * 方法返回之前执行，打印才返回值以及方法消耗时间
     * @param response 返回值
     */
    @AfterReturning(returning = "response",pointcut = "requestLog()")
    public void doAfterRunning(Object response) {
        Response response2= new Response();
        if (response!=null){
            if (response.toString().contains("Response")){
                response2 = (Response)response;
            }

            //打印返回值信息
            log.info("返回参数===============Response:[{}]",jsonObject.toJSONString(response2));
            //打印请求耗时
            log.info("请求耗时===Request spend times : [{}ms]",System.currentTimeMillis()-startTime.get());
        }

    }
}