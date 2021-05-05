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
        NettyConfig.group.remove(ctx.channel());
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
        String deviceId = jsonObject.getString("deviceId");
        DeviceSettingExample example = new DeviceSettingExample();
        example.createCriteria()
                .andDeviceIdEqualTo(deviceId);
        List<DeviceSetting> deviceSettings = deviceSettingMapper.selectByExample(example);
        DeviceSetting deviceSetting = deviceSettings.get(0);
        String toJSONString = JSONObject.toJSONString(deviceSetting);
        pingMessage.writeBytes(toJSONString.getBytes());
        channelHandlerContext.writeAndFlush(pingMessage);
        NettyConfig.channelHandlerContextMap.put(deviceId, channelHandlerContext);

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
        if (clientSocket != null) {
            ByteBuf pingMessage = Unpooled.buffer();
            String toJSON = JSONObject.toJSONString(deviceSetting);
            pingMessage.writeBytes(toJSON.getBytes());
            clientSocket.writeAndFlush(pingMessage);
        }

    }
}
