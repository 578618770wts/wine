package com.personal.wine;

import com.personal.wine.wine.WineServerSocket;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.personal.wine.mapper")
@EnableTransactionManagement
public class WineApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(WineApplication.class, args);
        context.getBean(WineServerSocket.class).start();
    }

}
