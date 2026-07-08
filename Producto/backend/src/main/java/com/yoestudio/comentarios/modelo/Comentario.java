package com.yoestudio.comentarios.modelo;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import lombok.Data;

@Data
@Document(collection = "comentarios")
public class Comentario {
    @Id
    private String id;

    private String idPublicacion;

    private Long idUsuario;
    private String nombreUsuario;

    private String descripcion;

    private LocalDateTime fechaComentario = LocalDateTime.now();

}
