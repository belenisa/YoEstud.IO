package com.yoestudio.service;

import com.yoestudio.model.Educacion;
import com.yoestudio.repository.EducacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducacionService {

    @Autowired
    private EducacionRepository educacionRepository;

     public List<Educacion> findAll() {
        return educacionRepository.findAll();
    }

    public Educacion save(Educacion educacion) {
        return educacionRepository.save(educacion);
    }

   
}