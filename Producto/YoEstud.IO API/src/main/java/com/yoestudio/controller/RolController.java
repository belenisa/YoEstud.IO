package com.yoestudio.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.yoestudio.model.Rol;
import com.yoestudio.service.RolService;



@RestController
@RequestMapping("/api/rol")
@CrossOrigin(origins = "*")
@Tag(name = "Rol", description = "Operaciones relacionadas con los roles de usuarios de YoEstud.IO")
public class RolController {
    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<List<Rol>> getAll() {
        List<Rol> rol = rolService.findAll();
        if (rol.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rol);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> getById(@PathVariable Long id) {
        return rolService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Rol>create(@Valid @RequestBody Rol rol) {
        rol.setId(null);
        Rol nuevorol = rolService.save(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevorol);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rol> update(@PathVariable Long id, @Valid @RequestBody Rol rol) {
        if (!rolService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        rol.setId(id);
        Rol rolActualizado = rolService.save(rol);
        return ResponseEntity.ok(rolActualizado);
    }



}
