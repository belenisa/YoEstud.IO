package com.yoestudio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.yoestudio.repository")
@EntityScan(basePackages = "com.yoestudio.model")
public class YoEstudioApplication {

    public static void main(String[] args) {
        try {
            loadApiKeys();
        } catch (IOException e) {
            // fallback silencioso
        }
        SpringApplication.run(YoEstudioApplication.class, args);
    }

    private static void loadApiKeys() throws IOException {
        String path = "apis.txt";
        if (Files.exists(Paths.get(path))) {
            Files.lines(Paths.get(path)).forEach(line -> {
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts[0].trim().equals("API_KEY")) {
                        System.setProperty("SPRING_AI_GOOGLE_AI_API_KEY", parts[1].trim());
                    }
                }
            });
        }
    }
}