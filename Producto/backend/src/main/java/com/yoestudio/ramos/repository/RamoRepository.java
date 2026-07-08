package com.yoestudio.ramos.repository;

import com.yoestudio.preguntas.model.Pregunta;
import com.yoestudio.ramos.modelo.Ramo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RamoRepository extends JpaRepository<Ramo, Long> {

    List<Ramo> findByPreguntas_Id(Long preguntaId);

}
