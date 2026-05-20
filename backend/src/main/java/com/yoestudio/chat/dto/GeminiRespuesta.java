package com.yoestudio.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiRespuesta {
    private String contenido;
    private boolean esArchivo;
    private String nombreArchivo;
    private String contenidoArchivo;
}
