package com.personal.wine.service.impl;

import com.personal.wine.base.Response;
import com.personal.wine.constants.ErrorCode;
import com.personal.wine.in.BindDeviceIn;
import com.personal.wine.in.GetDeviceListIn;
import com.personal.wine.in.GetDeviceSettingIn;
import com.personal.wine.mapper.DeviceSettingMapper;
import com.personal.wine.mapper.SystemUserMapper;
import com.personal.wine.mapper.WarningMapper;
import com.personal.wine.model.*;
import com.personal.wine.service.DeviceService;
import com.personal.wine.vo.Device;
import com.personal.wine.wine.ServerHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    DeviceSettingMapper deviceSettingMapper;
    @Autowired
    SystemUserMapper userMapper;
    @Autowired
    WarningMapper warningMapper;

    private ServerHandler clientSocket = new ServerHandler();

    /**
     * 绑定设备
     *
     * @param req
     */
    @Override
    public Response bindService(BindDeviceIn req) {

        Response response = new Response(ErrorCode.SUCCESS);
        String deviceId = req.getDeviceId();
        int userId = req.getUserId();
        String deviceName = req.getDeviceName();
        if (deviceId.isEmpty() || userId <= 0) {
            response.setErrorCode(ErrorCode.MISSING_PARAMETER);
            return response;
        }
        SystemUser systemUser = userMapper.selectByPrimaryKey(userId);
        if (systemUser == null) {
            response.setErrorCode(ErrorCode.USER_NOT_EXIST);
            return response;
        }
        if (req.getDefaultDevice() == 1) {
            //如果是绑定默认设备，需要看他是否已经有默认设备了
            DeviceSettingExample example = new DeviceSettingExample();
            example.createCriteria()
                    .andUserIdEqualTo(req.getUserId())
                    .andDefaultDeviceEqualTo(1);
            List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
            if (!deviceSettings.isEmpty()) {
                //说明已经有默认设备了 ,把当前的默认设备设为非默认 0
                deviceSettings.get(0).setDefaultDevice(0);
                deviceSettingMapper.updateByPrimaryKeySelective(deviceSettings.get(0));
            }
        }

        if (deviceName == null || deviceName.isEmpty()) {
            deviceName = deviceId;
        }

        DeviceSetting deviceSetting = new DeviceSetting();
        deviceSetting.setCreateTime(System.currentTimeMillis() + "");
        deviceSetting.setUserId(userId);
        deviceSetting.setDeviceId(deviceId);
        deviceSetting.setDeviceName(deviceName);
        deviceSetting.setDefaultDevice(req.getDefaultDevice());
        deviceSettingMapper.insertSelective(deviceSetting);
        return response;
    }

    @Override
    public Response unBindDevice(BindDeviceIn req) {

        Response response = new Response(ErrorCode.SUCCESS);
        int userId = req.getUserId();
        if (userId <= 0) {
            response.setErrorCode(ErrorCode.MISSING_PARAMETER);
            return response;
        }
        SystemUser systemUser = userMapper.selectByPrimaryKey(userId);
        if (systemUser == null) {
            response.setErrorCode(ErrorCode.USER_NOT_EXIST);
            return response;
        }

        DeviceSettingExample example = new DeviceSettingExample();
        example.createCriteria()
                .andUserIdEqualTo(req.getUserId());
        List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
        if (deviceSettings.isEmpty()) {
            response.setErrorCode(ErrorCode.DEVICE_NOT_BIND);
            return response;
        }
        for (int i = 0; i < deviceSettings.size(); i++) {
            DeviceSetting deviceSetting = deviceSettings.get(i);
            deviceSettingMapper.deleteByPrimaryKey(deviceSetting.getId());
        }

        return response;
    }

    /**
     * 获取设备配置信息
     *
     * @param req
     * @return
     */
    @Override
    public Response<DeviceSetting> getDeviceSetting(GetDeviceSettingIn req) {
        Response<DeviceSetting> response = new Response<>(ErrorCode.SUCCESS);
        String deviceId = req.getDeviceId();
        int userId = req.getUserId();
        if (deviceId.isEmpty() || userId <= 0) {
            response.setErrorCode(ErrorCode.MISSING_PARAMETER);
            return response;
        }
        DeviceSettingExample example = new DeviceSettingExample();
        example.createCriteria()
                .andDeviceIdEqualTo(deviceId)
                .andUserIdEqualTo(userId);

        List<DeviceSetting> deviceSettings =
                deviceSettingMapper.selectByExample(example);
        if (deviceSettings.isEmpty()) {
            response.setErrorCode(ErrorCode.DEVICE_BIND_ERROR);
            return response;
        }
        response.setData(deviceSettings.get(0));
        return response;
    }

    /**
     * 获取设备列表
     *
     * @param req
     * @return
     */
    @Override
    public Response<List<Device>> getDeviceList(GetDeviceListIn req) {
        Response<List<Device>> response = new Response<>(ErrorCode.SUCCESS);
        List<Device> deviceList = new ArrayList<>();
        int userId = req.getUserId();
        if (userId <= 0) {
            response.setErrorCode(ErrorCode.MISSING_PARAMETER);
            return response;
        }
        SystemUser systemUser = userMapper.selectByPrimaryKey(userId);
        if (systemUser == null) {
            response.setErrorCode(ErrorCode.USER_NOT_EXIST);
            return response;
        }
        DeviceSettingExample example = new DeviceSettingExample();
        example.createCriteria()
                .andUserIdEqualTo(userId);
        List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
        if (!deviceSettings.isEmpty()) {
            for (int i = 0; i < deviceSettings.size(); i++) {
                Device device = new Device();
                BeanUtils.copyProperties(deviceSettings.get(i), device);
                deviceList.add(device);
            }
        }
        response.setData(deviceList);
        return response;
    }

    /**
     * 上传配置信息
     *
     * @param req
     * @return
     */
    @Override
    public Response uploadDeviceSetting(DeviceSetting req) {
        Response response = new Response(ErrorCode.SUCCESS);
        String deviceId = req.getDeviceId();
        DeviceSettingExample example = new DeviceSettingExample();
        example.createCriteria()
                .andDeviceIdEqualTo(deviceId);
        List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
        if (deviceSettings.isEmpty()) {
            response.setErrorCode(ErrorCode.DEVICE_NOT_EXIST);
            return response;
        }
        //查找出来，再根据数据库的id去更新设备
        DeviceSetting deviceSetting = deviceSettings.get(0);
        req.setId(deviceSetting.getId());
        deviceSettingMapper.updateByPrimaryKeySelective(req);
        List<DeviceSetting> deviceSettings1 = deviceSettingMapper.selectByExample(example);

        clientSocket.setNeedSend(req);
        return response;
    }

    @Override
    public Response<DeviceSetting> resetDevice(String deviceId) {
        Response<DeviceSetting> response = new Response<>(ErrorCode.SUCCESS);
        DeviceSettingExample example = new DeviceSettingExample();
        example.createCriteria()
                .andDeviceIdEqualTo(deviceId);
        List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
        if (deviceSettings.isEmpty()) {
            response.setErrorCode(ErrorCode.DEVICE_NOT_EXIST);
            return response;
        }
        DeviceSetting deviceSetting = deviceSettings.get(0);
        deviceSetting.setLedBrightness(1);
        deviceSetting.setLedSwitch(1);
        deviceSetting.setGlassSwitch(0);
        deviceSetting.setAlertSwitch(1);
        deviceSetting.setDoorSwitch(0);
        deviceSetting.setLowTemperatureAlert(5);
        deviceSetting.setHighTemperatureAlert(25);
        deviceSetting.setLockDelay(5);
        deviceSetting.setStopPower(5);
        deviceSetting.setReboundPower(5);
        deviceSetting.setHighTemperature(2);
        deviceSetting.setDeicingTime(0);
        deviceSetting.setDeicingDeviceTime(24);
        deviceSettingMapper.updateByPrimaryKeySelective(deviceSetting);
        response.setData(deviceSetting);
        clientSocket.setNeedSend(deviceSetting);
        return response;
    }

    @Override
    public Response<DeviceSetting> setDefaultDevice(int userId, String deviceId) {

        if (StringUtils.isEmpty(deviceId)) {
            return new Response<>(ErrorCode.MISSING_PARAMETER);
        }

        Response response = new Response(ErrorCode.SUCCESS);
        //先查出这个人的默认设备
        DeviceSettingExample example = new DeviceSettingExample();
        example.createCriteria()
                .andUserIdEqualTo(userId)
                .andDefaultDeviceEqualTo(1);
        List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
        if (!deviceSettings.isEmpty()) {
            deviceSettings.get(0).setDefaultDevice(0);
            deviceSettingMapper.updateByPrimaryKeySelective(deviceSettings.get(0));
        }
        DeviceSettingExample deviceSettingExample = new DeviceSettingExample();
        deviceSettingExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andDeviceIdEqualTo(deviceId);
        List<DeviceSetting> deviceSettings1 = deviceSettingMapper.selectByExample(deviceSettingExample);
        if (deviceSettings1.isEmpty()) {
            response.setErrorCode(ErrorCode.DEVICE_BIND_ERROR);
            return response;
        }
        deviceSettings1.get(0).setDefaultDevice(1);
        deviceSettingMapper.updateByPrimaryKeySelective(deviceSettings1.get(0));
        response.setData(deviceSettings1.get(0));
        return response;
    }

    @Override
    public Response<List<Warning>> getWarningList(String deviceId) {
        Response<List<Warning>> response = new Response<>(ErrorCode.SUCCESS);
        WarningExample warningExample = new WarningExample();
        warningExample.createCriteria()
                .andStatusEqualTo(1)  //是否需要报警 1 ，是要报警
                .andDeviceIdEqualTo(deviceId);
        List<Warning> warnings = warningMapper.selectByExample(warningExample);
        //查出是否需要报警的警报
        response.setData(warnings);
        return response;
    }

    @Override
    public Response<List<Warning>> readCurrentWarning(String deviceId, int warningType) {
        Response<List<Warning>> response = new Response(ErrorCode.SUCCESS);
        WarningExample warningExample = new WarningExample();
        warningExample.createCriteria()
                .andDeviceIdEqualTo(deviceId)
                .andStatusEqualTo(1)
                .andWarningTypeEqualTo(warningType);
        List<Warning> warnings = warningMapper.selectByExample(warningExample);
        if (!warnings.isEmpty()) {
            warnings.get(0).setStatus(0);
            warningMapper.updateByPrimaryKeySelective(warnings.get(0));
        }
        warningExample.clear();
        warningExample.createCriteria()
                .andDeviceIdEqualTo(deviceId)
                .andStatusEqualTo(1);
        List<Warning> newWarningList = warningMapper.selectByExample(warningExample);
        response.setData(newWarningList);
        return response;
    }
}
