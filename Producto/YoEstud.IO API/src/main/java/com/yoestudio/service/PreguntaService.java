package com.yoestudio.service;

import com.yoestudio.model.Pregunta;
import com.yoestudio.repository.PreguntaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreguntaService {

    @Autowired
    private PreguntaRepository preguntaRepository;

    public List<Pregunta> findAll() {
        return preguntaRepository.findAll();
    }

    public Pregunta guardar(Pregunta pregunta) {
        return preguntaRepository.save(pregunta);
    }

    public List<Pregunta> obtenerPorUsuario(Long usuarioId) {
        return preguntaRepository.findByUsuario_Id(usuarioId);
    }

    public List<Pregunta> obtenerPorUsuarioYTipo(Long usuarioId, String asignatura) {
        return preguntaRepository.findByUsuario_IdAndAsignatura(usuarioId, asignatura);
    }

}