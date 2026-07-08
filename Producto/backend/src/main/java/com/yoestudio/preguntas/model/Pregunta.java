package com.yoestudio.preguntas.model;

import com.yoestudio.ramos.modelo.Ramo;
import com.yoestudio.usuario.model.Usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "id_Usuarios")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_Ramos")
    private Ramo ramos;
}
