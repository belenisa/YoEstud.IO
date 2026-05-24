package com.yoestudio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.yoestudio.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
}
