package com.personal.wine.wine;


import com.alibaba.fastjson.JSONObject;
import com.personal.wine.mapper.DeviceSettingMapper;
import com.personal.wine.model.DeviceSetting;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Data
@Slf4j
public class ClientSocket implements Runnable {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String key;
    private String message;

    private static Socket mSocket;
    private static ClientSocket client;

    private static DeviceSettingMapper deviceSettingMapper;
    private boolean needSend = false;
    private ServerSocketChannel serverSocketChannel;

    /**
     * 注册socket到map里
     *
     * @param socket
     * @return
     */
    public static ClientSocket register(Socket socket) {

        mSocket = socket;
        client = new ClientSocket();

        deviceSettingMapper = ManageSpringBeans.getBean(DeviceSettingMapper.class);

        return client;

    }

    /**
     * 发送数据
     *
     * @param str
     */
    public void send(String str) {
        try {
            outputStream.write(str.getBytes());
        } catch (IOException e) {
            logout();
        }
    }

    /**
     * 接收数据
     *
     * @return
     * @throws IOException
     */
    public String receive() {
        try {
            byte[] bytes = new byte[1024];
            inputStream.read(bytes);
            String info = new String(bytes, "utf-8");
            System.out.println(info);
            return info;
        } catch (IOException e) {
            logout();
        }
        return null;
    }

    /**
     * 登出操作, 关闭各种流
     */
    public void logout() {
        if (WineServerSocket.clientsMap.containsKey(key)) {
            WineServerSocket.clientsMap.remove(key);
        }

        System.out.println(WineServerSocket.clientsMap);
        try {
            socket.shutdownOutput();
            socket.shutdownInput();
        } catch (IOException e) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 发送数据包, 判断数据连接状态
     *
     * @return
     */
    public boolean isSocketClosed() {
        try {
            socket.sendUrgentData(1);
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    public void run() {

        // 每过5秒连接一次客户端
       /* while (true) {
            try {
                client.setSocket(mSocket);
                client.setInputStream(new DataInputStream(mSocket.getInputStream()));
                client.setOutputStream(new DataOutputStream(mSocket.getOutputStream()));
                byte[] bytes = new byte[2048];
                client.getInputStream().read(bytes);
                client.setKey(new String(bytes, "utf-8"));
                System.out.println("时间为 ==" + System.currentTimeMillis() + " 》》》》》接收到的数据 == : " + client.getKey());
                JSONObject jsonObject = JSONObject.parseObject(client.getKey());
                Integer type = jsonObject.getInteger("type");
                String deviceId = jsonObject.getString("deviceId");
                if (deviceId == null)
                    return;
                WineServerSocket.clientsMap.put(deviceId, client);
                key = deviceId;
                JSONObject responseJson = new JSONObject();
                if (type == null) {
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
//                    client.getOutputStream().write(responseJson.toJSONString().getBytes());
                } else {
                    DeviceSetting deviceSetting = JSONObject.parseObject(client.getKey(), DeviceSetting.class);
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
                System.out.println("回复的port == " + socket.getPort());
                client.getOutputStream().write(responseJson.toString().getBytes());


            } catch (IOException e) {
                client.logout();
            } catch (JSONException e) {
                try {
                    System.out.println("json错误 == " + client.getKey());
                    client.getOutputStream().write(("JSON格式错误 +" + client.getKey()).getBytes());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            if (isSocketClosed()) {
                System.out.println("关闭");
                logout();
                break;
            }
        }*/


    }


    public void setNeedSend(DeviceSetting deviceSetting) {
        try {
            ClientSocket clientSocket = WineServerSocket.clientsMap.get(deviceSetting.getDeviceId());
            if (clientSocket != null) {
                clientSocket.getOutputStream().write(JSONObject.toJSON(deviceSetting).toString().getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "socket=" + socket +
                ", inputStream=" + inputStream +
                ", outputStream=" + outputStream +
                ", key='" + key + '\'' +
                '}';
    }
}
