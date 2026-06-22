
package com.yoestudio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.yoestudio.usuario.repository", "com.yoestudio.preguntas.repository", "com.yoestudio.ramos.repository"})
@EnableMongoRepositories(basePackages = {"com.yoestudio.material.repository", "com.yoestudio.archivo.repository", "com.yoestudio.chat.repository"}) 
public class DatabaseConfig {
}
