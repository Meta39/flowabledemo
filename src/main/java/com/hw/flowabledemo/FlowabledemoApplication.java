package com.hw.flowabledemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class FlowabledemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowabledemoApplication.class, args);
    }
}
