/*
 * DatabaseConfig.java
 * Configuración para separar los repositorios de JPA (PostgreSQL) y MongoDB.
 * 22 de abril de 2026
 */
package com.yoestudio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.yoestudio.usuario.repository")
@EnableMongoRepositories(basePackages = "com.yoestudio.material.repository") // Se usará para el material de estudio
public class DatabaseConfig {
}
