package com.personal.wine.wine;

import com.alibaba.fastjson.JSONObject;
import com.personal.wine.mapper.DeviceSettingMapper;
import com.personal.wine.model.DeviceSetting;
import com.personal.wine.model.DeviceSettingExample;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static DeviceSettingMapper deviceSettingMapper;


    /**
     * 客户端与服务端创建连接的时候调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端与服务端连接开始...");
        NettyConfig.group.add(ctx.channel());

        deviceSettingMapper = ManageSpringBeans.getBean(DeviceSettingMapper.class);
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
        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject == null) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("type",5);
            jsonObject1.put("message","content is null ,please check");
            pingMessage.writeBytes(jsonObject1.toJSONString().getBytes());
            channelHandlerContext.writeAndFlush(pingMessage);
            System.out.println("写出成功 =============="+ jsonObject1.toJSONString());
            return;
        }
        String deviceId = jsonObject.getString("deviceId");
        Integer type = jsonObject.getInteger("type");
        JSONObject responseJson = new JSONObject();
        if (!NettyConfig.channelHandlerContextMap.containsKey(deviceId)) {
            NettyConfig.channelHandlerContextMap.put(deviceId, channelHandlerContext);
            NettyConfig.ipDeviceMap.put(channelHandlerContext.channel().remoteAddress(), deviceId);
        }
        if (type == null) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("type",6);
            jsonObject1.put("message","heart test success");
            pingMessage.writeBytes(jsonObject1.toJSONString().getBytes());
            channelHandlerContext.writeAndFlush(pingMessage);
            System.out.println("写出成功 =============="+ jsonObject1.toJSONString());
            return;
        }
        if (1 == type) {
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
            System.out.println("写出成功 =============="+ responseJson.toJSONString());
        } else {
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

        }

        /*DeviceSettingExample example = new DeviceSettingExample();
        example.createCriteria()
                .andDeviceIdEqualTo(deviceId);
        List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
        DeviceSetting deviceSetting = deviceSettings.get(0);
        String toJSONString = JSONObject.toJSONString(deviceSetting);
        pingMessage.writeBytes(toJSONString.getBytes());
        channelHandlerContext.writeAndFlush(pingMessage);*/


        //服务端使用这个就能向 每个连接上来的客户端群发消息
        //NettyConfig.group.writeAndFlush(info);
//        Iterator<Channel> iterator = NettyConfig.group.iterator();
//        while(iterator.hasNext()){
//            //打印出所有客户端的远程地址
//            System.out.println((iterator.next()).remoteAddress());
//        }
    }

    public void setNeedSend(DeviceSetting deviceSetting) {
        ChannelHandlerContext clientSocket = NettyConfig.channelHandlerContextMap.get(deviceSetting.getDeviceId());
        System.out.println("是否有Socket clientSocket == "+ clientSocket);
        if (clientSocket != null) {
            ByteBuf pingMessage = Unpooled.buffer();
            String toJSON = JSONObject.toJSONString(deviceSetting);
            JSONObject jsonObject = JSONObject.parseObject(toJSON);
            jsonObject.put("type", 3);
            pingMessage.writeBytes(jsonObject.toJSONString().getBytes());
            clientSocket.writeAndFlush(pingMessage);

            System.out.println("写出成功 =============="+ jsonObject.toJSONString());
        }

    }
}
