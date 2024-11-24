package com.chili;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chili.mapper")
public class GasPipelinesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GasPipelinesApplication.class, args);
    }

}
