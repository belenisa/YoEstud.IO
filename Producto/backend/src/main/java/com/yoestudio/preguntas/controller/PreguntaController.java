package com.yoestudio.preguntas.controller;

import com.yoestudio.preguntas.model.Pregunta;
import com.yoestudio.preguntas.service.PreguntaService;

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

    @GetMapping
    public ResponseEntity<List<Pregunta>>getAll() {
        List<Pregunta> pregunta = preguntaService.findAll();
        if (pregunta.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pregunta);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!preguntaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        preguntaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
