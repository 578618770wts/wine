package com.personal.wine.service;

import com.personal.wine.base.Response;
import com.personal.wine.dto.SystemUserDTO;
import com.personal.wine.in.PasswordLogin;
import com.personal.wine.in.RegisterIn;
import com.personal.wine.in.SMSLoginIn;
import com.personal.wine.model.SystemUser;

public interface UserService {

    /**
     * 用户登录
     *
     * @param phone
     * @return
     */
    Response<SystemUserDTO> passwordLogin(PasswordLogin phone);


    /**
     * 用户注册
     *
     * @param req
     * @return
     */
    Response<SystemUser> register(RegisterIn req);


    /**
     * 发送短信
     *
     * @param phone
     * @return
     */
    Response sendSMS(String phone);

    /**
     * 短信验证码登录
     *
     * @param smsLoginIn
     * @return
     */
    Response<SystemUserDTO> loginSMS(SMSLoginIn smsLoginIn);
}
