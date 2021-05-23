package com.personal.wine.controller;

import com.personal.wine.base.Response;
import com.personal.wine.in.BindDeviceIn;
import com.personal.wine.in.GetDeviceListIn;
import com.personal.wine.in.GetDeviceSettingIn;
import com.personal.wine.in.ResetDeviceIn;
import com.personal.wine.model.DeviceSetting;
import com.personal.wine.service.DeviceService;
import com.personal.wine.vo.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/")
public class DeviceController {

    @Autowired
    DeviceService deviceService;


    /**
     * 获取自己设备的配置
     *
     * @return
     */
    @RequestMapping("getSettingConfig")
    public Response<DeviceSetting> getSettingConfig(@RequestBody GetDeviceSettingIn req) {
        return deviceService.getDeviceSetting(req);
    }


    /**
     * 绑定设备
     *
     * @param req
     * @return
     */
    @RequestMapping("bindDevice")
    public Response bindDevice(@RequestBody BindDeviceIn req) {
        return deviceService.bindService(req);
    }


    /**
     * 获取设备列表
     */
    @RequestMapping("getDeviceList")
    public Response<List<Device>> getDeviceList(@RequestBody GetDeviceListIn req) {
        return deviceService.getDeviceList(req);
    }

    /**
     * 上传配置信息
     *
     * @param req
     * @return
     */
    @RequestMapping("uploadSettingConfig")
    public Response uploadSettingConfig(@RequestBody DeviceSetting req) {
        return deviceService.uploadDeviceSetting(req);
    }

    /**
     * 重置配置信息
     *
     * @param req
     * @return
     */
    @RequestMapping("resetDevice")
    public Response resetDevice(@RequestBody ResetDeviceIn req) {
        return deviceService.resetDevice(req.getDeviceId());
    }


    /**
     * 解绑设备
     *
     * @param req
     * @return
     */
    @RequestMapping("unBindDevice")
    public Response resetDevice(@RequestBody BindDeviceIn req) {
        return deviceService.unBindDevice(req);
    }

}
