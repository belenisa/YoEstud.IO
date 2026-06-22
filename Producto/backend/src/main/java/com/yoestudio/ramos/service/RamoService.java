package com.yoestudio.ramos.service;

import com.yoestudio.preguntas.model.Pregunta;
import com.yoestudio.ramos.modelo.Ramo;
import com.yoestudio.ramos.repository.RamoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;

@Service
public class RamoService {
    @Autowired
    private RamoRepository ramoRepository;


    public List<Ramo> findAll() {
        return ramoRepository.findAll();
    }

    public Optional<Ramo> findById(@NonNull Long id) {
        return ramoRepository.findById(id);
    }

    public List<Pregunta> obtenerPorPregunta(Long preguntaId) {
        return ramoRepository.findByPregunta_Id(preguntaId);
    }

    public Ramo guardar(Ramo ramo) {
        return ramoRepository.save(ramo);
    }

    public void deleteById(@NonNull Long id) {
        ramoRepository.deleteById(id);
    }
}
