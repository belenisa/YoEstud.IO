
package com.yoestudio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class YoEstudioApplication {

    public static void main(String[] args) {
        try {
            loadApiKeys();
        } catch (IOException e) {
            
        }
        SpringApplication.run(YoEstudioApplication.class, args);
    }

    private static void loadApiKeys() throws IOException {
        String path = "../local.properties";
        if (Files.exists(Paths.get(path))) {
            Files.lines(Paths.get(path)).forEach(line -> {
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (key.equals("GOOGLE_AI_API_KEY")) {
                        System.setProperty("spring.ai.google.ai.api-key", value);
                    } else if (key.equals("API_KEY")) {
                        
                        System.setProperty("spring.ai.google.ai.api-key", value);
                    }
                }
            });
        }
    }
}
