package com.example.demo.Service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder builder) {
        // Configuramos el cliente específicamente para Gemini 3 Flash
        this.chatClient = builder
                .defaultOptions(GoogleAiChatOptions.builder()
                        .withModel("gemini-3-flash") // Especificamos el modelo de 3ra generación
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