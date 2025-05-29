package com.example.CryptoPortfolioTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CryptoPortfolioTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoPortfolioTrackerApplication.class, args);
    }

}
