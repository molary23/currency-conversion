package com.xe.ratealerts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@EnableConfigurationProperties
@SpringBootApplication
public class RateAlertsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateAlertsApplication.class, args);
    }

}
