package com.yoestudio.archivo.repository;

import com.yoestudio.archivo.model.ArchivoGenerado;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivoRepository extends MongoRepository<ArchivoGenerado, String> {
    List<ArchivoGenerado> findByUsuarioId(Long usuarioId);
}
