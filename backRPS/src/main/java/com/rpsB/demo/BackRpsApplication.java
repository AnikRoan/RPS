package com.rpsB.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackRpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackRpsApplication.class, args);
    }

}
