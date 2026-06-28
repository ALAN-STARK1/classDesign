package com.example.indras;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.indras.**.mapper")
public class IndrasApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndrasApplication.class, args);
    }
}
