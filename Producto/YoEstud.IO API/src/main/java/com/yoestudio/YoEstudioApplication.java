/*
 * YoEstudioApplication.java
 * Clase principal de YoEstud.IO.
 * 22 de abril de 2026
 */
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
            // Fallback a variables de entorno
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
