package com.yoestudio.preguntas.service;

import com.yoestudio.preguntas.model.Pregunta;
import com.yoestudio.preguntas.repository.PreguntaRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;
@Service
public class PreguntaService {
    @Autowired
    private PreguntaRepositorio preguntaRepository;

    public List<Pregunta> findAll() {
        return preguntaRepository.findAll();
    }

    public Optional<Pregunta> findById(@NonNull Long id) {
        return preguntaRepository.findById(id);
    }

    public Pregunta guardar(Pregunta pregunta) {
        return preguntaRepository.save(pregunta);
    }

    public List<Pregunta> obtenerPorUsuario(Long usuarioId) {
        return preguntaRepository.findByUsuario_Id(usuarioId);
    }

    public void deleteById(@NonNull Long id) {
        preguntaRepository.deleteById(id);
    }

}
