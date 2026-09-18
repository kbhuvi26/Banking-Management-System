package com.bms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Spring Boot application.
 * Run this class's main() method to start the embedded Tomcat server
 * on http://localhost:8080
 */
@SpringBootApplication
public class BankingManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingManagementSystemApplication.class, args);
    }
}
