package com.yoestudio.archivo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "archivos_generados")
public class ArchivoGenerado {
    @Id
    private String id;
    private Long usuarioId;
    private String nombreArchivo;
    private String rutaServidor;
    private String tipo;
    private Date fechaCreacion;
    private String estado;
}
