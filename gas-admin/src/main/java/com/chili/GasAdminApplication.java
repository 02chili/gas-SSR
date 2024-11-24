package com.chili;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.chili.mapper")
@EnableFeignClients
public class GasAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GasAdminApplication.class, args);
    }

}
