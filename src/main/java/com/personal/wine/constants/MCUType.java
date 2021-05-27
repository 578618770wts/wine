package com.personal.wine.constants;

public interface MCUType {
    /**
     * 请求类型（0，上传数据，1：获取数据）
     * 3 APP更新，下发内容到MCU
     * 5 内容为null
     * 6 心跳
     * 7 json格式错误
     * 8 设备端解绑所有设备
     * 9 更新报警类型
     */

    int UPLOAD = 0;
    int GET = 1;
    int SEND_TO_DEVICE = 3;
    int NULL_CONTENT = 5;
    int HEART_TEST = 6;
    int JSON_EXCEPTION = 7;
    int RESET_ALL_DEVICE = 8;
    int WARNING_UPLOAD = 9;

}
