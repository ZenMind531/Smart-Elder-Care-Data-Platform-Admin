package com.smarteldercare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.smarteldercare.modules.**.mapper")
public class SmartElderCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartElderCareApplication.class, args);
    }
}
