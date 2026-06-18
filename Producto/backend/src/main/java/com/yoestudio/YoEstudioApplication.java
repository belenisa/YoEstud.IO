
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
        String[] paths = {"../local.properties", "local.properties", "backend/local.properties"};
        for (String path : paths) {
            if (Files.exists(Paths.get(path))) {
                Files.lines(Paths.get(path)).forEach(line -> {
                    if (line.contains("=") && !line.startsWith("#")) {
                        String[] parts = line.split("=", 2);
                        System.setProperty(parts[0].trim(), parts[1].trim());
                    }
                });
            }
        }
    }
}
