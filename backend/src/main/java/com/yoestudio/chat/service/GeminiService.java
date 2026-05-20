package com.yoestudio.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoestudio.chat.dto.GeminiRespuesta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${spring.ai.google.ai.api-key}")
    private String apiKey;

    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String SYSTEM_PROMPT = "Eres un asistente académico integrado en YOESTUD.IO, una app de estudio para estudiantes universitarios. " +
            "Tu función principal es: 1. Responder preguntas académicas de forma clara y didáctica. 2. Ayudar al usuario a comprender material de estudio que él mismo suba. " +
            "3. Cuando el usuario pida generar una \"prueba\", \"test\" o \"evaluación\" sobre un tema, DEBES responder ÚNICAMENTE con un JSON válido con esta estructura exacta: " +
            "{\"tipo\": \"archivo\", \"nombre_archivo\": \"prueba_[tema]_[fecha].txt\", \"contenido\": \"...contenido completo de la prueba en texto plano...\"}. " +
            "El servidor detectará este JSON y lo guardará como archivo en /output/. Comportamiento: - Responde siempre en español. - Sé conciso pero completo. " +
            "- Si el usuario sube una imagen o código, analízalo en contexto académico. - No respondas preguntas que no sean académicas o de uso de la app. " +
            "- Si el usuario pide una prueba, inclúye: título, instrucciones, 5-10 preguntas variadas (selección múltiple, desarrollo, verdadero/falso) con sus respuestas al final.";

    public GeminiRespuesta generarRespuesta(String mensajeUsuario, List<Map<String, String>> historial) {
        String url = API_URL + apiKey;

        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", SYSTEM_PROMPT))
        );

        Map<String, Object> body = Map.of(
                "system_instruction", systemInstruction,
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", mensajeUsuario))
                ))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            if (response.getBody() == null) return new GeminiRespuesta("Error: Respuesta vacía", false, null, null);

            List candidates = (List) response.getBody().get("candidates");
            Map content = (Map) ((Map) candidates.get(0)).get("content");
            List parts = (List) content.get("parts");
            String textoRespuesta = (String) ((Map) parts.get(PartIndex(parts))).get("text");

            return procesarTexto(textoRespuesta);
        } catch (Exception e) {
            return new GeminiRespuesta("Error de conexión con la IA: " + e.getMessage(), false, null, null);
        }
    }

    private int PartIndex(List parts) {
        return 0;
    }

    private GeminiRespuesta procesarTexto(String texto) {
        try {
            String jsonLimpio = extraerJson(texto);
            if (jsonLimpio != null) {
                JsonNode root = objectMapper.readTree(jsonLimpio);
                if (root.has("tipo") && "archivo".equals(root.get("tipo").asText())) {
                    return new GeminiRespuesta(
                            "Archivo generado con éxito.",
                            true,
                            root.get("nombre_archivo").asText(),
                            root.get("contenido").asText()
                    );
                }
            }
        } catch (Exception e) {
            
        }
        return new GeminiRespuesta(texto, false, null, null);
    }

    private String extraerJson(String texto) {
        int inicio = texto.indexOf("{");
        int fin = texto.lastIndexOf("}");
        if (inicio != -1 && fin != -1 && fin > inicio) {
            return texto.substring(inicio, fin + 1);
        }
        return null;
    }
}
