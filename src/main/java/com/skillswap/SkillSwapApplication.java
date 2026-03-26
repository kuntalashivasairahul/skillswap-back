package com.skillswap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the SkillSwap Spring Boot application.
 * Bootstraps the entire Spring context including JPA, Security, and Web MVC.
 */
@SpringBootApplication
public class SkillSwapApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillSwapApplication.class, args);
    }
}
