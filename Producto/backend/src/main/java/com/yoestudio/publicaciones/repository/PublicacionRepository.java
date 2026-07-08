package com.yoestudio.publicaciones.repository;

import com.yoestudio.publicaciones.modelo.Publicacion;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionRepository extends MongoRepository<Publicacion, String> {
}
