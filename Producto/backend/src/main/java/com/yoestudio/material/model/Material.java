package com.yoestudio.material.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "materiales")
public class Material {
    @Id
    private String id;
    private String titulo;
    private String descripcion;
    private String asignatura;
    private String areaConocimiento;
    private String tipoArchivo;
    private String urlArchivo;
    private Long usuarioId;
    private Date fechaSubida;
    private Double calificacionPromedio;
    private List<Calificacion> calificaciones;

    @Data
    public static class Calificacion {
        private Long usuarioId;
        private Integer valor;
        private String comentario;
        private Date fecha;
    }
}
