package com.personal.wine.wine;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.net.SocketAddress;
import java.util.Map;

public class NettyConfig {

    /**
     * 存储每一个客户端接入进来时的channel对象
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static Map<String, ChannelHandlerContext> channelHandlerContextMap = new HashMap();
    public static Map<SocketAddress, String> ipDeviceMap = new HashMap();

}