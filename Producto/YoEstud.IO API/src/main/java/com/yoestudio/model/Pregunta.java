package com.yoestudio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pregunta;
    private String respuesta;
    private String asignatura;

    @ManyToOne
    @JoinColumn(name = "idUsuarios")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idEducacion")
    private Educacion educacion;

}
