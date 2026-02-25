package com.travelmateai.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication does THREE things in one:
// 1. @Configuration      → marks this as a config class
// 2. @EnableAutoConfig   → tells Spring to auto-setup beans based on dependencies
// 3. @ComponentScan      → tells Spring to scan this package for components/controllers/services
@SpringBootApplication
public class TravelMateAiApplication {

    
    public static void main(String[] args) {


        SpringApplication.run(TravelMateAiApplication.class, args);
    }
}
