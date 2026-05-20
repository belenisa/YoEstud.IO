package com.yoestudio.seguridad.controller;

import com.yoestudio.seguridad.dto.AuthRequest;
import com.yoestudio.seguridad.dto.AuthResponse;
import com.yoestudio.seguridad.dto.RegistroRequest;
import com.yoestudio.seguridad.util.JwtUtil;
import com.yoestudio.usuario.model.Usuario;
import com.yoestudio.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El email ya existe");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario registrado con éxito");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(request.getUsername());
        if (usuario.isPresent() && usuario.get().getPassword().equals(request.getPassword())) {
            String token = jwtUtil.generarToken(usuario.get().getEmail());
            return ResponseEntity.ok(new AuthResponse(token, usuario.get().getNombre()));
        }
        return ResponseEntity.status(401).body("Credenciales inválidas");
    }
}
