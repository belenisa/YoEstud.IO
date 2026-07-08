package com.yoestudio.publicaciones.controller;


import com.yoestudio.publicaciones.modelo.Publicacion;
import com.yoestudio.publicaciones.service.PublicacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "*")
public class PublicacionesController {

    @Autowired
    private PublicacionService publicacionService;

    @PostMapping
    public Publicacion crearPublicacion(@RequestBody Publicacion publicacion) {
        return publicacionService.crearPublicacion(publicacion);
    }

    @GetMapping
    public List<Publicacion> obtenerTodas() {
        return publicacionService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public Publicacion obtenerPorId(@PathVariable String id) {
        return publicacionService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Publicacion actualizarPublicacion(
            @PathVariable String id,
            @RequestBody Publicacion publicacion) {

        return publicacionService.actualizarPublicacion(id, publicacion);
    }

    @DeleteMapping("/{id}")
    public void eliminarPublicacion(@PathVariable String id) {
        publicacionService.eliminarPublicacion(id);
    }


    @GetMapping("/hora")
    public LocalDateTime obtenerHoraServidor() {
        return LocalDateTime.now();
    }

    @PutMapping("/{id}/like/{usuarioId}")
    public Publicacion darLike(
            @PathVariable String id,
            @PathVariable Long usuarioId
    ) {

        return publicacionService.darLike(
                id,
                usuarioId
        );
    }

}
