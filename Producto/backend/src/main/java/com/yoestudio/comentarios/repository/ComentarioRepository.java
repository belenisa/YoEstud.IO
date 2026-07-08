package com.yoestudio.comentarios.repository;

import com.yoestudio.comentarios.modelo.Comentario;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ComentarioRepository extends MongoRepository<Comentario, String> {
    List<Comentario> findByIdPublicacion(String idPublicacion);
    List<Comentario> findByIdUsuario(Long idUsuario);
}
