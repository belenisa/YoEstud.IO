package com.yoestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.NonNull;
import com.yoestudio.model.Rol;
import com.yoestudio.repository.RolRepository;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    public Optional<Rol> findById(@NonNull Long id) {
        return rolRepository.findById(id);
    }

    public Rol save(@NonNull Rol rol) {
        return rolRepository.save(rol);
    }
}
