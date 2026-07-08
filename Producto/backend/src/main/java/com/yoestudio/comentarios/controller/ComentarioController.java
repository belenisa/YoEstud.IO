package com.yoestudio.comentarios.controller;


import com.yoestudio.comentarios.modelo.Comentario;
import com.yoestudio.comentarios.service.ComentarioService;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/comentarios")
@CrossOrigin(origins = "*")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @PostMapping
    public Comentario crearComentario(
            @RequestBody Comentario comentario) {

        return comentarioService.crearComentario(comentario);
    }

    @GetMapping("/publicacion/{idPublicacion}")
    public List<Comentario> obtenerComentarios(
            @PathVariable String idPublicacion) {

        return comentarioService.obtenerPorPublicacion(idPublicacion);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Comentario> obtenerPorUsuario(
            @PathVariable Long idUsuario) {

        return comentarioService.obtenerPorUsuario(idUsuario);
    }


    @DeleteMapping("/{id}")
    public void eliminarComentario(
            @PathVariable String id) {

        comentarioService.eliminarComentario(id);
    }

}
