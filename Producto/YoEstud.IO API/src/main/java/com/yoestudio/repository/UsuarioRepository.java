/*
 * UsuarioRepository.java
 * Repositorio JPA para la entidad Usuario.
 * 22 de abril de 2026
 */
package com.yoestudio.usuario.repository;

import com.yoestudio.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
