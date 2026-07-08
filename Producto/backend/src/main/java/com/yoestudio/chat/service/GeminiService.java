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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${SPRING_AI_GOOGLE_AI_API_KEY}")
    private String apiKey;

    @Value("${gemini.model.name:gemini-2.5-flash}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String systemPrompt(String nombreUsuario) {
        String nombre = (nombreUsuario != null && !nombreUsuario.isBlank())
                ? nombreUsuario.split("\\s+")[0]
                : "Usuario";
        return "Eres un asistente académico de YOESTUD.IO. " +
                "El usuario se llama " + nombre + ". Dirígete a él/ella por su nombre de forma natural. " +
                "Responde preguntas académicas y de estudio en español. " +
                "Sigue estas reglas ESTRICTAMENTE:\n" +
                "---\n" +
                "REGLA A - Cuando el usuario pida crear una prueba/examen/test PERO SIN especificar la materia:\n" +
                "Responde NORMALMENTE preguntando: '¿De qué materia quieres la prueba?' NO uses JSON.\n" +
                "---\n" +
                "REGLA B - Cuando el usuario ya haya dicho la materia (en el mismo mensaje o en uno anterior):\n" +
                "Responde ÚNICAMENTE con el JSON (sin texto extra). En 'contenido' pon SOLO las preguntas reales numeradas, bien formateadas. NO pongas instrucciones ni descripciones dentro de 'contenido'. Usa simbolos × ÷ ² ³. NO uses √ ≈ ≠ π Δ Σ ni LaTeX.\n" +
                "Ejemplo del JSON:\n" +
                "{\"tipo\": \"archivo\", \"nombre_archivo\": \"matematicas_prueba.pdf\", \"contenido\": \"1. Calcula: 5 × (12 - 4) + 18 ÷ 3\\n2. Resuelve: 3x + 7 = 22\\n3. Halla el area de un rectangulo de base 8 cm y altura 5 cm\"}\n" +
                "---\n" +
                "REGLA C - Cuando el usuario pida limpiar/borrar/resetear el historial o chat:\n" +
                "Responde ÚNICAMENTE con: {\"tipo\": \"limpiar\"}\n" +
                "---\n" +
                "REGLA D - Para cualquier otra cosa, responde normalmente en español sin JSON.";
    }

    public GeminiRespuesta generarRespuesta(String mensajeUsuario,
                                            List<Map<String, String>> historial,
                                            String nombreUsuario,
                                            String nombreArchivo,
                                            String archivoBase64) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("${")) {
            return new GeminiRespuesta("Error: API Key no configurada.", false, null, null, false);
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

//        List<Map<String, Object>> contents = new ArrayList<>();
//
//        contents.add(Map.of(
//            "role", "user",
//            "parts", List.of(Map.of("text", systemPrompt(nombreUsuario)))
//        ));
//
//        if (historial != null) {
//            for (Map<String, String> msg : historial) {
//                String role = msg.get("role");
//                if ("assistant".equals(role)) {
//                    role = "model";
//                }
//                contents.add(Map.of(
//                    "role", role,
//                    "parts", List.of(Map.of("text", msg.get("content")))
//                ));
//            }
//        }
//
//        contents.add(Map.of(
//            "role", "user",
//            "parts", List.of(Map.of("text", mensajeUsuario))
//        ));
//
//        Map<String, Object> body = Map.of("contents", contents);

        // Estructura principal del body
        Map<String, Object> body = new HashMap<>();

        // Se inyecta correctamente el System Instruction según la documentación de Gemini
        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", systemPrompt(nombreUsuario)))
        );
        body.put("systemInstruction", systemInstruction);

        // Se procesa la lista conversacional respetando los turnos alternados
        List<Map<String, Object>> contents = new ArrayList<>();

        if (historial != null) {
            for (Map<String, String> msg : historial) {
                String role = msg.get("role");
                if ("assistant".equals(role)) {
                    role = "model";
                }
                contents.add(Map.of(
                        "role", role,
                        "parts", List.of(Map.of("text", msg.get("content")))
                ));
            }
        }

        // Crear las partes para el mensaje actual del usuario (Texto + Archivo si existe)
        List<Map<String, Object>> partesMensajeActual = new ArrayList<>();

        // Si se envió un archivo, se formatea en la estructura inlineData requerida por Google
        if (archivoBase64 != null && !archivoBase64.isBlank()) {
            String mimeType = "application/pdf"; // MimeType por defecto
            if (nombreArchivo != null) {
                String nombreMinuscula = nombreArchivo.toLowerCase();
                if (nombreMinuscula.endsWith(".docx")) {
                    mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                } else if (nombreMinuscula.endsWith(".doc")) {
                    mimeType = "application/msword";
                }
            }

            // Se limpian posibles espacios en blanco en la cadena Base64 generada por Android
            String base64Limpio = archivoBase64.replaceAll("\\s", "");

            Map<String, Object> inlineData = Map.of(
                    "mimeType", mimeType,
                    "data", base64Limpio
            );

            partesMensajeActual.add(Map.of("inlineData", inlineData));
        }

        // Se agrega el prompt de texto del usuario a las partes
        partesMensajeActual.add(Map.of("text", mensajeUsuario));

        // Agregar el turno completo del usuario al final de la conversación
        contents.add(Map.of(
                "role", "user",
                "parts", partesMensajeActual
        ));

        // Guardar la conversación completa en el mapa principal
        body.put("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            System.out.println("Intentando conectar con Gemini (" + modelName + ")...");
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null) {
                List candidates = (List) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map content = (Map) ((Map) candidates.get(0)).get("content");
                    List parts = (List) content.get("parts");
                    String textoRespuesta = (String) ((Map) parts.get(0)).get("text");
                    return procesarTexto(textoRespuesta);
                }
            }
            return new GeminiRespuesta("Error: Respuesta vacía.", false, null, null, false);
        } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
            System.err.println("ERROR 429: Cuota excedida de Google Gemini.");
            return new GeminiRespuesta("Lo siento, he alcanzado mi límite de mensajes gratuitos por ahora. Por favor, intenta de nuevo en un minuto.", false, null, null, false);
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            System.err.println("ERROR 404: El modelo '" + modelName + "' no existe o no está disponible. Revisa la configuración.");
            return new GeminiRespuesta("Error 404: Modelo no encontrado. El modelo '" + modelName + "' no está disponible. Revisa la configuración o actualiza el nombre del modelo.", false, null, null, false);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return new GeminiRespuesta("Error de conexión: " + e.getMessage(), false, null, null, false);
        }
    }

    private GeminiRespuesta procesarTexto(String texto) {
        try {
            String jsonLimpio = extraerJson(texto);
            if (jsonLimpio != null) {
                JsonNode root = objectMapper.readTree(jsonLimpio);
                if (root.has("tipo")) {
                    String tipo = root.get("tipo").asText();
                    if ("archivo".equals(tipo)) {
                        return new GeminiRespuesta(
                                "Archivo generado con éxito.",
                                true,
                                root.get("nombre_archivo").asText(),
                                root.get("contenido").asText(),
                                false
                        );
                    } else if ("limpiar".equals(tipo)) {
                        return new GeminiRespuesta(
                                "Historial limpiado.",
                                false, null, null, true
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parseando JSON: " + e.getMessage());
        }
        return new GeminiRespuesta(texto, false, null, null, false);
    }

    private String extraerJson(String texto) {
        int inicio = texto.indexOf("{");
        if (inicio == -1) return null;
        int profundidad = 0;
        int fin = -1;
        for (int i = inicio; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '{') profundidad++;
            else if (c == '}') {
                profundidad--;
                if (profundidad == 0) {
                    fin = i;
                    break;
                }
            }
        }
        if (fin != -1) {
            return texto.substring(inicio, fin + 1);
        }
        return null;
    }
}
