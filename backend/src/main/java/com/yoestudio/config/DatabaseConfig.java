
package com.yoestudio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.yoestudio.usuario.repository")
@EnableMongoRepositories(basePackages = "com.yoestudio.material.repository") 
public class DatabaseConfig {
}
