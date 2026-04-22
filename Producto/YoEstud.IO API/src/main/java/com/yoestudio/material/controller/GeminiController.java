/*
 * GeminiController.java
 * API para el asistente de estudio.
 * 22 de abril de 2026
 */
package com.yoestudio.material.controller;

import com.yoestudio.material.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String mensaje) {
        return ResponseEntity.ok(geminiService.generarRespuesta(mensaje));
    }

    @PostMapping("/estudiar")
    public ResponseEntity<String> estudiar(@RequestBody String texto) {
        return ResponseEntity.ok(geminiService.generarCuestionario(texto));
    }
}
