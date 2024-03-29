package com.personal.wine.wine;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.personal.wine.constants.MCUType;
import com.personal.wine.mapper.DeviceSettingMapper;
import com.personal.wine.mapper.SystemUserMapper;
import com.personal.wine.mapper.WarningMapper;
import com.personal.wine.model.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.springframework.util.StringUtils;

import java.util.List;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static DeviceSettingMapper deviceSettingMapper;
    private static WarningMapper warningMapper;
    private static SystemUserMapper userMapper;

    private static DeviceSetting mDeviceSetting;


    /**
     * 客户端与服务端创建连接的时候调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端与服务端连接开始...");
        NettyConfig.group.add(ctx.channel());

        deviceSettingMapper = ManageSpringBeans.getBean(DeviceSettingMapper.class);
        warningMapper = ManageSpringBeans.getBean(WarningMapper.class);
        userMapper = ManageSpringBeans.getBean(SystemUserMapper.class);
    }

    /**
     * 客户端与服务端断开连接时调用
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端与服务端连接关闭...");
        String deviceId = NettyConfig.ipDeviceMap.get(ctx.channel().remoteAddress());
        NettyConfig.group.remove(ctx.channel());
        NettyConfig.channelHandlerContextMap.remove(deviceId);
        NettyConfig.ipDeviceMap.remove(ctx.channel().remoteAddress());

    }

    /**
     * 服务端接收客户端发送过来的数据结束之后调用
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        System.out.println("信息接收完毕...");
    }

    /**
     * 工程出现异常的时候调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 服务端处理客户端websocket请求的核心方法，这里接收了客户端发来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object info) throws Exception {
        System.out.println("接收到了：" + info);
        ByteBuf buf = (ByteBuf) info;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("本地端口为：==" + channelHandlerContext.name());

        System.out.println("接收客户端数据:" + body);
        ByteBuf pingMessage = Unpooled.buffer();
        try {
            JSONObject jsonObject = JSONObject.parseObject(body);
            if (jsonObject == null) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("type", MCUType.NULL_CONTENT);
                jsonObject1.put("message", "content is null ,please check");
                pingMessage.writeBytes(jsonObject1.toJSONString().getBytes());
                channelHandlerContext.writeAndFlush(pingMessage);
                System.out.println("写出成功 ==============" + jsonObject1.toJSONString());
                return;
            }
            String deviceId = jsonObject.getString("deviceId");
            Integer type = jsonObject.getInteger("type");
            JSONObject responseJson = new JSONObject();
            if (!NettyConfig.channelHandlerContextMap.containsKey(deviceId)) {
                //加入之前先移除原有的,确保一下
                NettyConfig.channelHandlerContextMap.remove(deviceId);
                NettyConfig.ipDeviceMap.remove(channelHandlerContext.channel().remoteAddress());

                NettyConfig.channelHandlerContextMap.put(deviceId, channelHandlerContext);
                NettyConfig.ipDeviceMap.put(channelHandlerContext.channel().remoteAddress(), deviceId);
            } else {
                //先找出原来有的与新来的做对比
                ChannelHandlerContext mapChannel = NettyConfig.channelHandlerContextMap.get(deviceId);
                if (!mapChannel.channel().remoteAddress().equals(channelHandlerContext.channel().remoteAddress())) {
                    NettyConfig.channelHandlerContextMap.remove(deviceId);
                    NettyConfig.ipDeviceMap.remove(channelHandlerContext.channel().remoteAddress());

                    NettyConfig.channelHandlerContextMap.put(deviceId, channelHandlerContext);
                    NettyConfig.ipDeviceMap.put(channelHandlerContext.channel().remoteAddress(), deviceId);
                }
            }
            if (type == null || MCUType.HEART_TEST == type) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("type", MCUType.HEART_TEST);
                jsonObject1.put("message", "heart test success");
                pingMessage.writeBytes(jsonObject1.toJSONString().getBytes());
                channelHandlerContext.writeAndFlush(pingMessage);
                System.out.println("心跳包ip：================》》》》" + channelHandlerContext.channel().remoteAddress());
                System.out.println("写出成功 ==============" + jsonObject1.toJSONString());
                return;
            }
            if (MCUType.GET == type) {
                DeviceSettingExample example = new DeviceSettingExample();
                example.createCriteria()
                        .andDeviceIdEqualTo(deviceId);
                List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
                if (deviceSettings.isEmpty()) {
                    responseJson.put("code", -1);
                    responseJson.put("data", "");
                    responseJson.put("message", "device not exist");
                } else {
                    DeviceSetting deviceSetting = deviceSettings.get(0);
                    responseJson.put("code", 0);
                    responseJson.put("data", JSONObject.toJSON(deviceSetting));
                    responseJson.put("message", "success");
                }
                pingMessage.writeBytes(responseJson.toJSONString().getBytes());
                channelHandlerContext.writeAndFlush(pingMessage);
                System.out.println("写出成功 ==============" + responseJson.toJSONString());
            } else if (MCUType.UPLOAD == type) {
                DeviceSetting deviceSetting = JSONObject.parseObject(body, DeviceSetting.class);
                DeviceSettingExample example = new DeviceSettingExample();
                example.createCriteria()
                        .andDeviceIdEqualTo(deviceId);
                List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
                if (deviceSettings.isEmpty()) {
                    responseJson.put("code", -1);
                    responseJson.put("data", "");
                    responseJson.put("message", "device not exist");
                } else {
                    DeviceSetting deviceSetting1 = deviceSettings.get(0);
                    deviceSetting.setId(deviceSetting1.getId());
                    deviceSettingMapper.updateByPrimaryKeySelective(deviceSetting);
                    responseJson.put("code", 0);
                    responseJson.put("data", JSONObject.toJSON(deviceSetting));
                    responseJson.put("message", "success");
//                        client.getOutputStream().write((client.getKey() + "update success").getBytes());
                }

            } else if (type == MCUType.RESET_ALL_DEVICE) {
                if (StringUtils.isEmpty(deviceId)) {
                    System.out.println("设备id为空空空 ============== 》》》》》》》》》》》》》》》》》》》》》》》》》》"
                    );
                    return;
                }
                //把这个设备与所有人解绑
                DeviceSettingExample example = new DeviceSettingExample();
                example.createCriteria()
                        .andDeviceIdEqualTo(deviceId);
                List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
                for (int i = 0; i < deviceSettings.size(); i++) {
                    DeviceSetting deviceSetting = deviceSettings.get(0);
                    deviceSettingMapper.deleteByPrimaryKey(deviceSetting.getId());
                    System.out.println("删除成功 ============== 》》》》》》》》》》》》》》》》》》》》》》》》》》"
                            + deviceSetting.toString());
                }
            } else if (type == MCUType.WARNING_UPLOAD) {
                //上报报警类型
                if (StringUtils.isEmpty(deviceId)) {
                    System.out.println("设备id为空空空 ============== 》》》》》》》》》》》》》》》》》》》》》》》》》》"
                    );
                    return;
                }
                int warningType = (int) jsonObject.get("warningType");
                Warning warning = new Warning();
                warning.setDeviceId(deviceId);
                warning.setWarningType(warningType);
                warning.setStatus(1);
                warning.setUpdateTime(System.currentTimeMillis() + "");
                warningMapper.insert(warning);
                sendSMSWarning(deviceId, warningType);
            } else if (MCUType.OPERATION_DEVICE_SUCCESS == type) {
                deviceSettingMapper.updateByPrimaryKeySelective(mDeviceSetting);
            }
        } catch (JSONException jsonException) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", MCUType.JSON_EXCEPTION);
            jsonObject.put("message", "JSON Exception");
            pingMessage.writeBytes(jsonObject.toJSONString().getBytes());
            channelHandlerContext.writeAndFlush(pingMessage);
            System.out.println("写出成功 ==============" + jsonObject.toJSONString());
        } finally {
            // 用于释放缓存。避免内存溢出
            ReferenceCountUtil.release(info);
        }

    }

    /**
     * 发送报警短信
     * 报警类型：1.温度上限 2.温度下限，3.开门警报 4.展台警报
     *
     * @param deviceId
     */
    private void sendSMSWarning(String deviceId, int alertType) {
        DeviceSettingExample deviceSettingExample = new DeviceSettingExample();
        deviceSettingExample.createCriteria()
                .andDeviceIdEqualTo(deviceId);
        List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(deviceSettingExample);
        if (!deviceSettings.isEmpty()) {
            Integer userId = deviceSettings.get(0).getUserId();
            SystemUserExample example = new SystemUserExample();
            example.createCriteria()
                    .andIdEqualTo(userId);
            List<SystemUser> systemUsers = userMapper.selectByExample(example);


            Config config = new Config()
                    // 您的AccessKey ID
                    .setAccessKeyId("LTAI5tBAzpj8MzHR4QMAGjys")
                    // 您的AccessKey Secret
                    .setAccessKeySecret("IPf2an7xfe8QzUXE3L2985zRnlTqov");
            JSONObject jsonObject = new JSONObject();
            String verifyCode = ((int) ((Math.random() * 9 + 1) * 100000)) + "";
            jsonObject.put("name", deviceId);
            jsonObject.put("code", "2021/06/11");
            Client client = null;
            try {
                client = new Client(config);
                // 访问的域名
                config.endpoint = "dysmsapi.aliyuncs.com";
                SendSmsRequest sendSmsRequest = new SendSmsRequest();
                sendSmsRequest.setPhoneNumbers(systemUsers.get(0).getPhone());
                sendSmsRequest.setSignName("高恒美");

                switch (alertType) {
                    case 1:
                        sendSmsRequest.setTemplateCode("SMS_216844841");
                        break;
                    case 2:
                        sendSmsRequest.setTemplateCode("SMS_216844933");
                        break;
                    case 3:
                        sendSmsRequest.setTemplateCode("SMS_217435315");
                        break;
                    case 4:
                        sendSmsRequest.setTemplateCode("SMS_216844841");
                        break;
                }
                sendSmsRequest.setTemplateParam(jsonObject.toString());
                // 复制代码运行请自行打印 API 的返回值
                SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void setNeedSend(DeviceSetting deviceSetting) {
        mDeviceSetting = deviceSetting;
        ChannelHandlerContext clientSocket = NettyConfig.channelHandlerContextMap.get(deviceSetting.getDeviceId());
        System.out.println("是否有Socket clientSocket == " + clientSocket);

        if (clientSocket != null && NettyConfig.group.contains(clientSocket.channel())) {
            if (clientSocket.channel().isActive()) {
                System.out.println("写出的ip为==============》》》》》》》》》》" + clientSocket.channel().remoteAddress());
                ByteBuf pingMessage = Unpooled.buffer();
//                String toJSON = JSONObject.toJSONString(deviceSetting);
//                JSONObject jsonObject = JSONObject.parseObject(toJSON);
                JSONObject sourceObject = (JSONObject) JSONObject.toJSON(deviceSetting);
                JSONObject jsonObject = formatData(sourceObject);
                jsonObject.put("type", MCUType.SEND_TO_DEVICE);
                pingMessage.writeBytes(jsonObject.toJSONString().getBytes());
                clientSocket.writeAndFlush(pingMessage);

                System.out.println("写出成功 ==============" + jsonObject.toJSONString());
            } else {
                System.out.println("---------》》》》》》通讯++++++++已死" + clientSocket.channel().remoteAddress());
                NettyConfig.group.remove(clientSocket.channel());
                NettyConfig.channelHandlerContextMap.remove(deviceSetting.getDeviceId());
                NettyConfig.ipDeviceMap.remove(clientSocket.channel().remoteAddress());
            }
        } else {
            NettyConfig.channelHandlerContextMap.remove(deviceSetting.getDeviceId());
        }

    }

    private JSONObject formatData(JSONObject jsonObject) {
        JSONObject newJson = new JSONObject();
        for (String k : jsonObject.keySet()) {
            Object value = jsonObject.get(k);
            if (value != null) {
                newJson.put(k, value);
            }
        }
        return newJson;
    }
}
