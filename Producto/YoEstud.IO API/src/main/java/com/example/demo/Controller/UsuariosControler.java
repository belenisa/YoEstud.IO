package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ragemusica.model.UsuariosModelo;
import com.example.ragemusica.service.UsuariosService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con los Usuarios") //Permite agrupar los endpoints bajo un nombre y descripción 

public class UsuariosControler {

    @Autowired
    private UsuariosService usuarioService;
    
    @GetMapping
    public ResponseEntity<List<UsuariosModelo>> getAllUsuarios() {
        List<UsuariosModelo> usuarios = usuarioService.findAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuariosModelo> getUsuariosById(@PathVariable Long id) {
        UsuariosModelo usuarios = usuarioService.findById(id);
        if (usuarios == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<UsuariosModelo> createUsuarios(@RequestBody UsuariosModelo usuarios) {
        usuarios.setId(null);
        Artistas nuevoUsuario = usuarioService.save(usuarios);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artistas> updatedUsuarios(@PathVariable Long id, @RequestBody UsuariosModelo usuarios) {
        artistas.setId(id);
        Artistas updatedUsuario = usuarioService.save(usuarios);
        if (updatedUsuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUsuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuarios(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

}
