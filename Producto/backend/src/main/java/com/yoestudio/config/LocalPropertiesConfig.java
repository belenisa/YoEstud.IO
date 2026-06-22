package com.yoestudio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class LocalPropertiesConfig {

    private final ConfigurableEnvironment environment;

    public LocalPropertiesConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        String[] paths = {"../local.properties", "local.properties", "backend/local.properties"};
        boolean loaded = false;

        for (String path : paths) {
            try {
                FileSystemResource resource = new FileSystemResource(path);
                if (resource.exists()) {
                    Properties props = PropertiesLoaderUtils.loadProperties(resource);
                    environment.getPropertySources().addFirst(new MapPropertySource("localProperties-" + path, 
                        props.entrySet().stream().collect(Collectors.toMap(
                            e -> e.getKey().toString(),
                            e -> e.getValue()
                        ))
                    ));
                    System.out.println("✅ " + path + " cargado exitosamente");
                    loaded = true;
                }
            } catch (IOException e) {
                System.err.println("❌ Error al cargar " + path + ": " + e.getMessage());
            }
        }

        if (!loaded) {
            System.out.println("⚠️ No se encontró ningún archivo local.properties en las rutas conocidas");
        }
    }
}
