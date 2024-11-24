package com.chili;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chili.mapper")
public class GasReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(GasReportApplication.class, args);
    }

}
