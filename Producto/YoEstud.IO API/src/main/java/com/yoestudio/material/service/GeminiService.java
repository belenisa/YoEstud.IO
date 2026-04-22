/*
 * GeminiService.java
 * Servicio para la integración con Google Gemini 1.5 Flash.
 * Actúa como el tutor inteligente de YOESTUD.IO para los estudiantes.
 * 22 de abril de 2026
 */
package com.yoestudio.material.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder builder) {
        // Configuramos el asistente con un "System Prompt" para que actúe como tutor
        this.chatClient = builder
                .defaultSystem("Eres el asistente de estudio de YOESTUD.IO. " +
                               "Tu objetivo es ayudar a estudiantes universitarios a entender conceptos complejos, " +
                               "hacer resúmenes de sus apuntes y generar preguntas de práctica. " +
                               "Responde siempre de forma motivadora, clara y estructurada en español.")
                .defaultOptions(GoogleAiChatOptions.builder()
                        .withModel("gemini-1.5-flash") // El mejor modelo para apps móviles por su velocidad
                        .withTemperature(0.7) // Balance entre creatividad y precisión
                        .build())
                .build();
    }

    /**
     * Genera una respuesta general del tutor de IA.
     */
    public String generarRespuesta(String mensaje) {
        return chatClient.prompt()
                .user(mensaje)
                .call()
                .content();
    }

    /**
     * Genera un cuestionario de estudio a partir de un texto de apuntes.
     */
    public String generarCuestionario(String textoApuntes) {
        String prompt = "A partir de los siguientes apuntes, genera 3 preguntas de opción múltiple " +
                        "con sus respuestas correctas para ayudarme a estudiar:\n\n" + textoApuntes;
        
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
