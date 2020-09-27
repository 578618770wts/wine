package com.personal.wine.service.impl;

import com.personal.wine.base.Response;
import com.personal.wine.constants.ErrorCode;
import com.personal.wine.dto.SystemUserDTO;
import com.personal.wine.in.PasswordLogin;
import com.personal.wine.in.RegisterIn;
import com.personal.wine.mapper.SystemUserMapper;
import com.personal.wine.model.SystemUser;
import com.personal.wine.model.SystemUserExample;
import com.personal.wine.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    SystemUserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户登录
     *
     * @param req
     * @return
     */
    @Override
    public Response<SystemUserDTO> passwordLogin(PasswordLogin req) {
        Response<SystemUserDTO> response = new Response<>(ErrorCode.SUCCESS);
        SystemUserDTO dto = new SystemUserDTO();
        if (req.getPhone() == null || req.getPassword() == null) {
            response.setErrorCode(ErrorCode.MISSING_PARAMETER);
            return response;
        }
        //先判断用户是否存在，如果存在直接登录，返回user信息
        SystemUserExample example = new SystemUserExample();
        example.createCriteria()
                .andPasswordEqualTo(req.getPassword())
                .andPhoneEqualTo(req.getPhone());
        List<SystemUser> systemUsers = userMapper.selectByExample(example);

        if (systemUsers.isEmpty()) {
            response.setErrorCode(ErrorCode.PASSWORD_ERROR);
            return response;
        }
        BeanUtils.copyProperties(systemUsers.get(0), dto);
        //生成token
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set(uuid, req.getPhone(), 1, TimeUnit.DAYS);
        dto.setToken(uuid);
        response.setData(dto);
        return response;
    }

    /**
     * 用户注册
     *
     * @param req
     * @return
     */
    @Override
    public Response<SystemUser> register(RegisterIn req) {
        Response<SystemUser> response = new Response<>(ErrorCode.SUCCESS);
        if (req.getPhone() == null || req.getPassword() == null ||
                req.getPhone().isEmpty() || req.getPassword().isEmpty()) {
            response.setErrorCode(ErrorCode.MISSING_PARAMETER);
            return response;
        }
        String phone = req.getPhone();
        SystemUserExample userExample = new SystemUserExample();
        userExample.createCriteria()
                .andPhoneEqualTo(phone);
        List<SystemUser> systemUsers = userMapper.selectByExample(userExample);
        if (!systemUsers.isEmpty()) {
            response.setErrorCode(ErrorCode.USER_EXIST);
            return response;
        }
        SystemUser systemUser = new SystemUser();
        systemUser.setCreateTime(System.currentTimeMillis() + "");
        systemUser.setUpdateTime(System.currentTimeMillis() + "");
        systemUser.setUserName(req.getName() == null ? req.getPassword() : req.getName());
        systemUser.setPassword(req.getPassword());
        systemUser.setPhone(req.getPhone());
        userMapper.insertSelective(systemUser);
        response.setData(systemUser);
        return response;
    }


}
