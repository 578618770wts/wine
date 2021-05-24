package com.personal.wine.controller;

import com.personal.wine.base.Response;
import com.personal.wine.constants.ErrorCode;
import com.personal.wine.dto.SystemUserDTO;
import com.personal.wine.in.GetSMSDateIn;
import com.personal.wine.in.PasswordLogin;
import com.personal.wine.in.RegisterIn;
import com.personal.wine.in.SMSLoginIn;
import com.personal.wine.model.SystemUser;
import com.personal.wine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ResourceBundle;

@RestController
@RequestMapping("/v1/")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    static ResourceBundle jiGuang = ResourceBundle.getBundle("JiGuang");

    private int LOGIN_TEMPLE_ID = Integer.parseInt(jiGuang.getString("register"));


    /**
     * 用户注册
     *
     * @param req
     * @return
     */
    @RequestMapping("register")
    public Response<SystemUser> register(@RequestBody RegisterIn req) {
        return userService.register(req);
    }

    /**
     * 密码登录
     *
     * @param req
     * @return
     */
    @RequestMapping("/passwordLogin")
    public Response<SystemUserDTO> passwordLogin(@RequestBody PasswordLogin req) {

        return userService.passwordLogin(req);

    }


    /**
     * 验证码登录
     *
     * @param req
     * @return
     */
    @RequestMapping("/loginSMS")
    public Response<SystemUserDTO> loginSMS(@RequestBody SMSLoginIn req) {

        return userService.loginSMS(req);

    }

    @RequestMapping("/sendSMS")
    public Response sendSMS(@RequestBody GetSMSDateIn req) {
        Response response = new Response<>(ErrorCode.SUCCESS);
        if (StringUtils.isEmpty(req.getPhone())) {
            return new Response<>(ErrorCode.MISSING_PARAMETER);
        }
        Response response1 = userService.sendSMS(req.getPhone());
//        response.setData(smsCode);
//        redisTemplate.opsForValue().set("sendSMS" + req.getPhone(), smsCode, 5, TimeUnit.MINUTES);
        return response1;
    }


}
