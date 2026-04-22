/*
 * GeminiService.java
 * Servicio de integración con Google Gemini 2.0 Flash.
 * 22 de abril de 2026
 */
package com.yoestudio.material.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class GeminiService {

    @Value("${SPRING_AI_GOOGLE_AI_API_KEY}")
    private String apiKey;

    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();

    public String generarRespuesta(String mensaje) {
        if (apiKey == null || apiKey.isEmpty()) return "API Key no configurada.";

        String url = API_URL + apiKey;
        Map<String, Object> body = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", mensaje))))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            List candidates = (List) response.getBody().get("candidates");
            Map content = (Map) ((Map) candidates.get(0)).get("content");
            List parts = (List) content.get("parts");
            return (String) ((Map) parts.get(0)).get("text");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String generarCuestionario(String texto) {
        return generarRespuesta("Genera 3 preguntas de opción múltiple: " + texto);
    }
}
