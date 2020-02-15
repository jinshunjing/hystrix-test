package org.jim.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableHystrix
@EnableScheduling
public class HystrixTestApp {
    public static void main(String[] args) {
        SpringApplication.run(HystrixTestApp.class, args);
    }
}
