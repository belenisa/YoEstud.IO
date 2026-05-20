package com.yoestudio.chat.service;

import com.yoestudio.archivo.model.ArchivoGenerado;
import com.yoestudio.archivo.service.ArchivoService;
import com.yoestudio.chat.dto.GeminiRespuesta;
import com.yoestudio.chat.model.MensajesChat;
import com.yoestudio.chat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private ArchivoService archivoService;

    public Map<String, Object> procesarMensaje(Long usuarioId, String sesionId, String mensajeUsuario) {
        MensajesChat chat = chatRepository.findBySesionId(sesionId)
                .orElseGet(() -> {
                    MensajesChat nuevoChat = new MensajesChat();
                    nuevoChat.setUsuarioId(usuarioId);
                    nuevoChat.setSesionId(sesionId);
                    nuevoChat.setMensajes(new ArrayList<>());
                    nuevoChat.setFechaInicio(new Date());
                    return nuevoChat;
                });

        List<Map<String, String>> historial = chat.getMensajes().stream()
                .map(m -> Map.of("role", m.getRol(), "content", m.getContenido()))
                .collect(Collectors.toList());

        GeminiRespuesta geminiRespuesta = geminiService.generarRespuesta(mensajeUsuario, historial);

        MensajesChat.Mensaje mUser = new MensajesChat.Mensaje();
        mUser.setRol("user");
        mUser.setContenido(mensajeUsuario);
        mUser.setFecha(new Date());
        chat.getMensajes().add(mUser);

        MensajesChat.Mensaje mBot = new MensajesChat.Mensaje();
        mBot.setRol("assistant");
        mBot.setContenido(geminiRespuesta.getContenido());
        mBot.setFecha(new Date());
        chat.getMensajes().add(mBot);

        chatRepository.save(chat);

        Map<String, Object> response = new HashMap<>();
        response.put("respuesta", geminiRespuesta.getContenido());
        response.put("es_archivo", geminiRespuesta.isEsArchivo());

        if (geminiRespuesta.isEsArchivo()) {
            ArchivoGenerado archivo = archivoService.guardarArchivo(
                    usuarioId,
                    geminiRespuesta.getNombreArchivo(),
                    geminiRespuesta.getContenidoArchivo()
            );
            response.put("archivo_id", archivo.getId());
            response.put("nombre_archivo", archivo.getNombreArchivo());
        }

        return response;
    }

    public List<MensajesChat> obtenerHistorial(Long usuarioId) {
        return chatRepository.findByUsuarioId(usuarioId);
    }

    public void limpiarSesion(String sesionId) {
        chatRepository.deleteBySesionId(sesionId);
    }
}
