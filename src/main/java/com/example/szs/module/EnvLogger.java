package com.example.szs.module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EnvLogger implements CommandLineRunner {
    private final Environment environment;

    public EnvLogger(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        log.info("DATASOURCE_URL = {}", environment.getProperty("DATASOURCE_URL"));
        log.info("DATASOURCE_USER = {}", environment.getProperty("DATASOURCE_USER"));
        log.info("FRONTEND_URL = {}", environment.getProperty("FRONTEND_URL"));
        log.info("SERVER_URL = {}", environment.getProperty("SERVER_URL"));
        log.info("JASYPT_ALGORITHM = {}", environment.getProperty("JASYPT_ALGORITHM"));
    }
}
