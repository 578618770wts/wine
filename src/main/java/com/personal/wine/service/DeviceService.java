package com.personal.wine.service;

import com.personal.wine.base.Response;
import com.personal.wine.in.BindDeviceIn;
import com.personal.wine.in.GetDeviceListIn;
import com.personal.wine.in.GetDeviceSettingIn;
import com.personal.wine.model.DeviceSetting;
import com.personal.wine.model.Warning;
import com.personal.wine.vo.Device;

import java.util.List;

public interface DeviceService {

    /**
     * 绑定设备与用户关系
     *
     * @param req
     */
    Response bindService(BindDeviceIn req);


    /**
     * 解绑设备
     *
     * @param req
     * @return
     */
    Response unBindDevice(BindDeviceIn req);

    /**
     * 获取绑定设备的一些配置
     *
     * @param req
     * @return
     */
    Response<DeviceSetting> getDeviceSetting(GetDeviceSettingIn req);


    /**
     * 获取设备列表
     *
     * @param req
     * @return
     */
    Response<List<Device>> getDeviceList(GetDeviceListIn req);


    /**
     * 上传配置信息
     *
     * @param req
     * @return
     */
    Response uploadDeviceSetting(DeviceSetting req);


    /**
     * 恢复出厂设置
     *
     * @return
     */
    Response<DeviceSetting> resetDevice(String deviceId);


    /**
     * 设置默认的设备
     *
     * @param deviceId
     * @return
     */
    Response<DeviceSetting> setDefaultDevice(int userId, String deviceId);

    /**
     * 获取警报列表
     *
     * @param deviceId
     * @return
     */
    Response<List<Warning>> getWarningList(String deviceId);

    /**
     * 将此报警标志位已读
     *
     * @param deviceId
     * @param warningType
     * @return
     */
    Response<List<Warning>> readCurrentWarning(String deviceId, int warningType);


}
