package com.vampon.vojbackendjudgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication//(exclude = {RedisAutoConfiguration.class})
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.vampon")  //有些类定义在不同的模块下，需要加该注解使得这些包能够被扫描到
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.vampon.vojbackendserviceclient.service"})
public class VojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VojBackendJudgeServiceApplication.class, args);
    }

}
