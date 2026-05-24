package com.yoestudio.controller;

import com.yoestudio.model.Pregunta;
import com.yoestudio.service.PreguntaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preguntas")
@CrossOrigin(origins = "*")
public class PreguntaController {

    @Autowired
    private PreguntaService preguntaService;

    @PostMapping
    public ResponseEntity<Pregunta> crear(@RequestBody Pregunta pregunta) {

        Pregunta nueva = preguntaService.guardar(pregunta);

        return ResponseEntity.ok(nueva);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pregunta>> obtenerPorUsuario(@PathVariable Long usuarioId) {

        List<Pregunta> lista = preguntaService.obtenerPorUsuario(usuarioId);

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/usuario/{usuarioId}/{tipo}")
    public ResponseEntity<List<Pregunta>> obtenerPorTipo(
            @PathVariable Long usuarioId,
            @PathVariable String tipo) {

        List<Pregunta> lista = preguntaService.obtenerPorUsuarioYTipo(usuarioId, tipo);

        return ResponseEntity.ok(lista);
    }
}