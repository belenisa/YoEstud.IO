/*
 * Usuario.java
 * Entidad que representa a los usuarios del sistema YoEstud.IO.
 * 22 de mayo de 2026
 */
package com.yoestudio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Rol {

    public enum Roles {
        ADMIN,ESTUDIANTE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", nullable = false)
    private Roles tiporol;
}

