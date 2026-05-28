package com.yoestudio.chat.dto;

import lombok.Data;

@Data
public class ChatRequestDTO {
    private Long usuarioId;
    private String sesionId;
    private String mensaje;
    private String nombreArchivo;
    private String archivoBase64;
    private String nombreUsuario;
}
