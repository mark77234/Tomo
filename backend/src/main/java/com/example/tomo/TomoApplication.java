package com.example.tomo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TomoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TomoApplication.class, args);
    }

}
