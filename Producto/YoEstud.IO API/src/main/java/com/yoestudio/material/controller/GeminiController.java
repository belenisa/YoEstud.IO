/*
 * GeminiController.java
 * Controlador REST para interactuar con el asistente de IA de YOESTUD.IO.
 * 22 de abril de 2026
 */
package com.yoestudio.material.controller;

import com.yoestudio.material.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
@Tag(name = "IA Asistente", description = "Endpoints para la asistencia de estudio con Gemini")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/chat")
    @Operation(summary = "Chat con el tutor académico", description = "Envía un mensaje y recibe una respuesta educativa del asistente.")
    public ResponseEntity<String> chat(@RequestBody String mensaje) {
        try {
            String respuesta = geminiService.generarRespuesta(mensaje);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al conectar con Gemini: " + e.getMessage());
        }
    }

    @PostMapping("/estudiar")
    @Operation(summary = "Generar cuestionario de estudio", description = "Envía apuntes en texto y recibe 3 preguntas de opción múltiple.")
    public ResponseEntity<String> generarCuestionario(@RequestBody String apuntes) {
        try {
            String cuestionario = geminiService.generarCuestionario(apuntes);
            return ResponseEntity.ok(cuestionario);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al generar cuestionario: " + e.getMessage());
        }
    }
}
