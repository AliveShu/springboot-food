package com.food;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.food.mapper")
public class FoodSourceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodSourceApplication.class, args);

    }
}
