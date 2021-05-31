package com.personal.wine.vo;

import lombok.Data;

@Data
public class Device {
    private int id;
    private String deviceId;
    private int userId;
    private String deviceName;
    private int defaultDevice;
}
