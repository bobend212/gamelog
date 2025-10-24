package com.matkon.gamelog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class GameLogAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(GameLogAppApplication.class, args);
    }
}