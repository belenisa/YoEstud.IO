package com.yoestudio.chat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "mensajes_chat")
public class MensajesChat {
    @Id
    private String id;
    private Long usuarioId;
    private String sesionId;
    private List<Mensaje> mensajes;
    private Date fechaInicio;

    @Data
    public static class Mensaje {
        private String rol;
        private String contenido;
        private Date fecha;
    }
}
