package com.yoestudio.ramos.controller;


import com.yoestudio.preguntas.model.Pregunta;
import com.yoestudio.preguntas.service.PreguntaService;
import com.yoestudio.ramos.modelo.Ramo;
import com.yoestudio.ramos.service.RamoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ramos")
@CrossOrigin(origins = "*")
public class RamoController {

    @Autowired
    private RamoService ramoService;

    @GetMapping
    public ResponseEntity<List<Ramo>> getAll() {
        List<Ramo> ramo = ramoService.findAll();
        if (ramo.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ramo);
    }

    @PostMapping
    public ResponseEntity<Ramo> crear(@RequestBody Ramo ramo) {

        Ramo nueva = ramoService.guardar(ramo);

        return ResponseEntity.ok(nueva);
    }
    @GetMapping("/pregunta/{preguntaId}")
    public ResponseEntity<List<Ramo>> obtenerPorPregunta(
            @PathVariable Long preguntaId) {

        List<Ramo> lista = ramoService.obtenerPorPregunta(preguntaId);

        return ResponseEntity.ok(lista);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!ramoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ramoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
