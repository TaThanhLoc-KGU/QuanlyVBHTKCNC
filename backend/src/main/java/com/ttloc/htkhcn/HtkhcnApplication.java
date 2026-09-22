package com.ttloc.htkhcn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HtkhcnApplication {

    public static void main(String[] args) {
        SpringApplication.run(HtkhcnApplication.class, args);
    }
}
