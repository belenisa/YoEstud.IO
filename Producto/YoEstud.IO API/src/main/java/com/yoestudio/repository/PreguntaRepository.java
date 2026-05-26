package com.yoestudio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yoestudio.model.Pregunta;
import java.util.List;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByUsuario_Id(Long usuarioId);

    List<Pregunta> findByUsuario_IdAndAsignatura(Long usuarioId, String asignatura);

}