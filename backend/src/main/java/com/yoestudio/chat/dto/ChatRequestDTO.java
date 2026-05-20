package com.yoestudio.chat.dto;

import lombok.Data;

@Data
public class ChatRequestDTO {
    private Long usuarioId;
    private String sesionId;
    private String mensaje;
}
