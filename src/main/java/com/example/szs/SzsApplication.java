package com.example.szs;

import com.example.szs.config.security.RsaKeyConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyConfigProperties.class)
@SpringBootApplication
public class SzsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SzsApplication.class, args);
    }
}
