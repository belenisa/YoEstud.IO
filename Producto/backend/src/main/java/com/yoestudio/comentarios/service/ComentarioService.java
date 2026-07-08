package com.yoestudio.comentarios.service;

import com.yoestudio.comentarios.modelo.Comentario;
import com.yoestudio.comentarios.repository.ComentarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;


    public Comentario crearComentario(Comentario comentario) {

        if (comentario.getFechaComentario() == null) {
            comentario.setFechaComentario(LocalDateTime.now());
        }

        return comentarioRepository.save(comentario);
    }

    public List<Comentario> obtenerPorPublicacion(String idPublicacion) {
        return comentarioRepository.findByIdPublicacion(idPublicacion);
    }

    public List<Comentario> obtenerPorUsuario(Long idUsuario) {
        return comentarioRepository.findByIdUsuario(idUsuario);
    }

    public void eliminarComentario(String id) {
        comentarioRepository.deleteById(id);
    }

}
