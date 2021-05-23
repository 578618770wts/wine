package com.personal.wine.in;

import lombok.Data;

@Data
public class BindDeviceIn {
    private String deviceId; //设备id
    private int userId;//用户id
    private int defaultDevice;// 是否是默认设备 0：不是  1：是
    private String deviceName;//设备名称

}
