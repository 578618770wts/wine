package com.personal.wine.service.impl;

import com.personal.wine.base.Response;
import com.personal.wine.constants.ErrorCode;
import com.personal.wine.in.BindDeviceIn;
import com.personal.wine.in.GetDeviceListIn;
import com.personal.wine.in.GetDeviceSettingIn;
import com.personal.wine.mapper.DeviceSettingMapper;
import com.personal.wine.mapper.SystemUserMapper;
import com.personal.wine.model.DeviceSetting;
import com.personal.wine.model.DeviceSettingExample;
import com.personal.wine.model.SystemUser;
import com.personal.wine.service.DeviceService;
import com.personal.wine.vo.Device;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    DeviceSettingMapper deviceSettingMapper;
    @Autowired
    SystemUserMapper userMapper;

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
        DeviceSettingExample example = new DeviceSettingExample();
        example.createCriteria()
                .andUserIdEqualTo(req.getUserId())
                .andDeviceIdEqualTo(req.getDeviceId());
        List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
        if (!deviceSettings.isEmpty()) {
            response.setErrorCode(ErrorCode.DEVICE_BIND);
            return response;

        }
        if (deviceName == null || deviceName.isEmpty()) {
            deviceName = deviceId;
        }

        DeviceSetting deviceSetting = new DeviceSetting();
        deviceSetting.setCreateTime(System.currentTimeMillis() + "");
        deviceSetting.setUserId(userId);
        deviceSetting.setDeviceId(deviceId);
        deviceSetting.setDeviceName(deviceName);
        deviceSettingMapper.insertSelective(deviceSetting);
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
        deviceSetting.setLockDelay(3);
        deviceSetting.setStopPower(3);
        deviceSetting.setReboundPower(3);
        deviceSetting.setDeicingTime(8);
        deviceSetting.setDeicingDeviceTime(10);
        deviceSettingMapper.updateByPrimaryKeySelective(deviceSetting);
        response.setData(deviceSetting);
        return response;
    }
}
