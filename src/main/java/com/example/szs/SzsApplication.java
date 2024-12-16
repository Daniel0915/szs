package com.example.szs;

import com.example.szs.config.security.RsaKeyConfigProperties;
import com.example.szs.service.kafka.producer.ClipProducer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;

@EnableConfigurationProperties(RsaKeyConfigProperties.class)
@SpringBootApplication
public class SzsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SzsApplication.class, args);
    }

    @Bean
    public ApplicationRunner runner(ClipProducer clipProducer) {
        return args -> {
            clipProducer.async("clip3", "Hello Clips3-async");
            clipProducer.sync("clip3", "Hello Clips3-sync");
            clipProducer.routingSend("clip3", "Hello Clips3-routingSend");
            clipProducer.routingSendByte("clip3-bytes", "Hello Clips3-routingSend-byte".getBytes(StandardCharsets.UTF_8));
            clipProducer.replyingSend("clip3-request", "Ping clips");
        };

    }
}
