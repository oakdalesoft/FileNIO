package com.oakdalesoft.example.FileNIO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by Alex on 28/09/2016.
 */

@SpringBootApplication
public class FileNIOConfig {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(FileNIOConfig.class, args);
    }

    @Bean
    Application application() {
        return new Application();
    }

}
