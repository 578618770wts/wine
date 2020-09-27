package com.personal.wine.wine;


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

    /**
     * 注册socket到map里
     *
     * @param socket
     * @return
     */
    public static ClientSocket register(Socket socket) {

        mSocket = socket;
        client = new ClientSocket();

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
        while (true) {
            try {
                client.setSocket(mSocket);
                client.setInputStream(new DataInputStream(mSocket.getInputStream()));
                client.setOutputStream(new DataOutputStream(mSocket.getOutputStream()));
                byte[] bytes = new byte[1024];
                client.getInputStream().read(bytes);
                client.setKey(new String(bytes, "utf-8"));
                WineServerSocket.clientsMap.put(client.getKey(), client);
                System.out.println("时间为 ==" + System.currentTimeMillis() + " 》》》》》接收到的数据 == : " + client.getKey());
            } catch (IOException e) {
                client.logout();
            }
            if (isSocketClosed()) {
                System.out.println("关闭");
                logout();
                break;
            }
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
