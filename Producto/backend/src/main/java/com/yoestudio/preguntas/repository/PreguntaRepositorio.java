package com.yoestudio.preguntas.repository;

import com.yoestudio.preguntas.model.Pregunta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreguntaRepositorio extends JpaRepository<Pregunta, Long>{
    List<Pregunta> findByUsuario_Id(Long usuarioId);
}
