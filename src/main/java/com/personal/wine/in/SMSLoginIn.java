package com.personal.wine.in;

import lombok.Data;

@Data
public class SMSLoginIn {
    private String phone; //手机号
    private String verifyCode; //密码
}
