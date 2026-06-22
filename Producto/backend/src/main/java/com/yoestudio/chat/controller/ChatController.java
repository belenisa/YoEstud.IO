package com.yoestudio.chat.controller;

import com.yoestudio.chat.dto.ChatRequestDTO;
import com.yoestudio.chat.model.MensajesChat;
import com.yoestudio.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/mensaje")
    public Map<String, Object> enviarMensaje(@RequestBody ChatRequestDTO request) {
        System.out.println("📩 Recibida petición en /api/chat/mensaje para usuario: " + request.getUsuarioId());
        return chatService.procesarMensaje(
            request.getUsuarioId(), 
            request.getSesionId(), 
            request.getMensaje(),
            request.getNombreArchivo(),
            request.getArchivoBase64(),
            request.getNombreUsuario()
        );
    }

    @GetMapping("/historial/{usuarioId}")
    public List<MensajesChat> obtenerHistorial(@PathVariable Long usuarioId) {
        return chatService.obtenerHistorial(usuarioId);
    }

    @DeleteMapping("/sesion/{sesionId}")
    public void limpiarSesion(@PathVariable String sesionId) {
        chatService.limpiarSesion(sesionId);
    }
}
