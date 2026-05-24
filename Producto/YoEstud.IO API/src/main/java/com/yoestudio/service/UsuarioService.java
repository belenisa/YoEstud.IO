/*
 * UsuarioService.java
 * Servicio para gestionar la lógica de negocio de usuarios.
 * 22 de abril de 2026
 */
package com.yoestudio.service;

import com.yoestudio.model.Usuario;
import com.yoestudio.repository.UsuarioRepository;

import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(@NonNull Long id) {
        return usuarioRepository.findById(id);
    }

    
    public Usuario save(@NonNull Usuario usuario) {
        usuario.setPassword(
        passwordEncoder.encode(usuario.getPassword())
    );

    return usuarioRepository.save(usuario);
    }

    
    public Usuario login(String nombre, String password) {

        Usuario usuario = usuarioRepository.findByNombre(nombre)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return usuario;
    }



    public void deleteById(@NonNull Long id) {
        usuarioRepository.deleteById(id);
    }

    
}
