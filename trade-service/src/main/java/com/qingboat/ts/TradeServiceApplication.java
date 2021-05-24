package com.qingboat.ts;

import com.qingboat.base.api.FeishuService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.qingboat")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.qingboat")
@MapperScan("com.qingboat.ts.dao")
@EnableAsync
public class TradeServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(TradeServiceApplication.class, args);
    }

}
