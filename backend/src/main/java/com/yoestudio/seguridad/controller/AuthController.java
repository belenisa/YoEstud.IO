package com.yoestudio.seguridad.controller;

import com.yoestudio.seguridad.dto.AuthRequest;
import com.yoestudio.seguridad.dto.AuthResponse;
import com.yoestudio.seguridad.dto.RegistroRequest;
import com.yoestudio.seguridad.util.JwtUtil;
import com.yoestudio.usuario.model.Usuario;
import com.yoestudio.usuario.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Map<String, String> codigosRecuperacion = new ConcurrentHashMap<>();

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        logger.info("Recibida petición de registro para email: {}", request.getEmail());
        try {
            if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("El email {} ya existe", request.getEmail());
                return ResponseEntity.badRequest().body(Map.of("error", "El email ya existe"));
            }
            Usuario usuario = new Usuario();
            usuario.setNombre(request.getNombre());
            usuario.setEmail(request.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setTipo(Usuario.TipoUsuario.FREE);
            usuario.setRolId(2L); 
            usuarioRepository.save(usuario);
            logger.info("Usuario {} registrado con éxito", request.getEmail());
            return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado con éxito"));
        } catch (Exception e) {
            logger.error("Error al registrar usuario: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        logger.info("Recibida petición de login para: {}", request.getUsername());
        Optional<Usuario> usuario = usuarioRepository.findByEmail(request.getUsername());
        if (usuario.isPresent() && passwordEncoder.matches(request.getPassword(), usuario.get().getPassword())) {
            String token = jwtUtil.generarToken(usuario.get().getEmail());
            logger.info("Login exitoso para: {}", request.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, usuario.get().getNombre(), usuario.get().getId()));
        }
        logger.warn("Credenciales inválidas para: {}", request.getUsername());
        return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<?> recuperarContrasena(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        logger.info("Solicitud de recuperación para: {}", email);
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isEmpty()) {
            logger.warn("Email {} no encontrado", email);
            return ResponseEntity.badRequest().body(Map.of("error", "El email no está registrado"));
        }
        String codigo = String.format("%06d", new Random().nextInt(999999));
        codigosRecuperacion.put(email, codigo);
        logger.info("Código de recuperación para {}: {}", email, codigo);
        return ResponseEntity.ok(Map.of(
            "mensaje", "Código enviado al correo",
            "codigo", codigo
        ));
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<?> verificarCodigo(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String codigo = body.get("codigo");
        String nuevaPassword = body.get("nuevaPassword");
        logger.info("Verificando código para: {}", email);
        String codigoGuardado = codigosRecuperacion.get(email);
        if (codigoGuardado == null || !codigoGuardado.equals(codigo)) {
            logger.warn("Código inválido para: {}", email);
            return ResponseEntity.badRequest().body(Map.of("error", "Código inválido"));
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
        }
        Usuario usuario = usuarioOpt.get();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        codigosRecuperacion.remove(email);
        logger.info("Contraseña actualizada para: {}", email);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada con éxito"));
    }

    @GetMapping("/debug/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }
}
