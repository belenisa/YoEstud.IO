package com.yoestudio.publicaciones.modelo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
@Document(collection = "publicaciones")
public class Publicacion {
    @Id
    private String id;

    private Long idUsuario;

    private String nombreUsuario;

    private String carrera;

    private String descripcion;

    private String documento;

    private String hashtag;

    private LocalDateTime fechaPublicacion;

    private Integer likes;

    private List<Long> usuariosLikes;
}