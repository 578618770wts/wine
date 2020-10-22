package com.personal.wine.wine;

import com.alibaba.fastjson.JSONObject;
import com.personal.wine.model.DeviceSetting;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
@Component
public class WineServerSocket {

    @Value("${socket.port}")
    private Integer port;
    private boolean started;
    private ServerSocket ss;
    public static ConcurrentHashMap<String, ClientSocket> clientsMap = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        new WineServerSocket().start(10086);
//        DeviceSetting deviceSetting = new DeviceSetting();
//        deviceSetting.setId(1);
//        deviceSetting.setDeviceId("1234");
//        deviceSetting.setAlertSwitch(1);
//        deviceSetting.setDeviceName("test");
//        deviceSetting.setCurrentHumidity("80");
//        deviceSetting.setCurrentTemperature(26);
//        deviceSetting.setDeicingDeviceTime(1);
//        deviceSetting.setDeicingTime(1);
//        deviceSetting.setFanSetting(1);
//        deviceSetting.setGlassSwitch(1);
//        deviceSetting.setHighTemperature(38);
//        deviceSetting.setHighTemperatureAlert(39);
//        deviceSetting.setLedBrightness(1);
//        deviceSetting.setLedColor("#055525");
//        deviceSetting.setLedSwitch(1);
//        deviceSetting.setLockDelay(1);
//        deviceSetting.setLowTemperature(10);
//        deviceSetting.setLowTemperatureAlert(8);
//        deviceSetting.setReboundPower(10);
//        deviceSetting.setStopPower(1);
//        deviceSetting.setTemperature(10);
//        System.out.println(JSONObject.toJSONString(deviceSetting));

    }

    public void start() {
        start(null);
    }

    public void start(Integer port) {
        try {
            ss = new ServerSocket(port == null ? this.port : port);
            started = true;
            System.out.println("端口已开启,占用10086端口号....");
        } catch (Exception e) {
            System.out.println("端口使用中....");
            System.out.println("请关掉相关程序并重新运行服务器！");
            e.printStackTrace();
            System.exit(0);
        }

        try {
            while (started) {
                Socket socket = ss.accept();
                socket.setKeepAlive(true);
                System.out.println("上线通知： " + socket.getInetAddress() + ":" + socket.getPort());
                ClientSocket register = ClientSocket.register(socket);
                if (register != null) {
                    executorService.execute(register);
                    System.out.println("a client connected!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
