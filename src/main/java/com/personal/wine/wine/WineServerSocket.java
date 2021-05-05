package com.personal.wine.wine;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
@Component
public class WineServerSocket {

    @Value("${socket.port}")
    private Integer mPort;
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
    private ServerSocketChannel serverSocketChannel;
    public void start(Integer port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //服务端要建立两个group，一个负责接收客户端的连接，一个负责处理数据传输
                //连接处理group
                EventLoopGroup boss = new NioEventLoopGroup();
                //事件处理group
                EventLoopGroup worker = new NioEventLoopGroup();
                ServerBootstrap bootstrap = new ServerBootstrap();
                // 绑定处理group
                bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                        //保持连接数
                        .option(ChannelOption.SO_BACKLOG, 300)
                        //有数据立即发送
                        .option(ChannelOption.TCP_NODELAY, true)
                        //保持连接
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        //处理新连接
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel sc) throws Exception {
                                // 增加任务处理
                                ChannelPipeline p = sc.pipeline();
                                p.addLast(
//                                        //使用了netty自带的编码器和解码器
//                                        new StringDecoder(),
//                                        new StringEncoder(),
                                        //心跳检测，读超时，写超时，读写超时
                                        //new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS),
                                        //自定义的处理器
                                        new ServerHandler());
                            }
                        });

                //绑定端口，同步等待成功
                ChannelFuture future;
                try {
                    future = bootstrap.bind(mPort).sync();
                    if (future.isSuccess()) {
                        serverSocketChannel = (ServerSocketChannel) future.channel();
                        System.out.println("服务端启动成功，端口：" + mPort);
                    } else {
                        System.out.println("服务端启动失败！");
                    }

                    //等待服务监听端口关闭,就是由于这里会将线程阻塞，导致无法发送信息，所以我这里开了线程
                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //优雅地退出，释放线程池资源
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            }
        }).start();
      /*  try {
            ss = new ServerSocket(port == null ? this.port : port);
            started = true;
            System.out.println("端口已开启,占用4321端口号....");
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
        }*/

    }


}
