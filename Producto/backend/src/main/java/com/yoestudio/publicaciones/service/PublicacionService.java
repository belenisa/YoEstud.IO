package com.yoestudio.publicaciones.service;

import com.yoestudio.publicaciones.modelo.Publicacion;
import com.yoestudio.publicaciones.repository.PublicacionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PublicacionService {

    @Autowired
    private  PublicacionRepository publicacionRepository;

    public Publicacion crearPublicacion(Publicacion publicacion) {

        publicacion.setId(null);

        if (publicacion.getFechaPublicacion() == null) {
            publicacion.setFechaPublicacion(LocalDateTime.now());
        }

        System.out.println("PUBLICACION RECIBIDA: " + publicacion);

        if (publicacion.getLikes() == null) {
            publicacion.setLikes(0);
        }

        if (publicacion.getUsuariosLikes() == null) {
            publicacion.setUsuariosLikes(new ArrayList<>());
        }

        System.out.println("ANTES DE GUARDAR: " + publicacion);

        return publicacionRepository.save(publicacion);
    }

    public List<Publicacion> obtenerTodas() {
        return publicacionRepository.findAll();
    }

    public Publicacion obtenerPorId(String id) {
        return publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));
    }

    public Publicacion actualizarPublicacion(
            String id,
            Publicacion nuevaPublicacion) {

        Publicacion actual = obtenerPorId(id);

        actual.setDescripcion(nuevaPublicacion.getDescripcion());
        actual.setDocumento(nuevaPublicacion.getDocumento());
        actual.setHashtag(nuevaPublicacion.getHashtag());

        return publicacionRepository.save(actual);
    }


    public void eliminarPublicacion(String id) {
        publicacionRepository.deleteById(id);
    }

    public Publicacion darLike(
            String id,
            Long usuarioId
    ) {

        Publicacion publicacion = obtenerPorId(id);

        if (publicacion.getUsuariosLikes() == null) {
            publicacion.setUsuariosLikes(new ArrayList<>());
        }

        if (publicacion.getUsuariosLikes().contains(usuarioId)) {

            publicacion.getUsuariosLikes().remove(usuarioId);

            if (publicacion.getLikes() > 0) {
                publicacion.setLikes(
                        publicacion.getLikes() - 1
                );
            }

        } else {

            publicacion.getUsuariosLikes().add(usuarioId);

            publicacion.setLikes(
                    publicacion.getLikes() + 1
            );
        }

        return publicacionRepository.save(publicacion);
    }

}
