/*
 * GeminiService.java
 * Servicio para la integración con la API de Google Gemini (modelo gemini-3-flash).
 * 22 de abril de 2026
 */
package com.yoestudio.material.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder builder) {
        // Configuramos el cliente específicamente para Gemini 3 Flash
        this.chatClient = builder
                .defaultOptions(GoogleAiChatOptions.builder()
                        .withModel("gemini-1.5-flash") // Ajustado a la versión disponible común, cambiar a 3-flash si está disponible
                        .withTemperature(0.7)
                        .build())
                .build();
    }

    public String generarRespuesta(String mensaje) {
        return chatClient.prompt()
                .user(mensaje)
                .call()
                .content();
    }
}
