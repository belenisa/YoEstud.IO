package com.example.demo.Modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuariosModelo {

    @Id // Corregido: @Id debe ir en mayúscula
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true) // Recomendado: email único
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Debe estar sobre el campo, no dentro del Enum
    @Column(nullable = false)
    private Rol rol; // Campo que usa el Enum

    // Definición del Enum
    public enum Rol {
        PREMIUM, FREE, ADMIN, DEVELOPERS; // Estándar Java: Enums en MAYÚSCULAS

        @JsonCreator
        public static Rol fromString(String value) {
            if (value == null) return null;
            for (Rol rol : Rol.values()) {
                if (rol.name().equalsIgnoreCase(value)) {
                    return rol;
                }
            }
            return null;
        }

        @JsonValue
        public String toJson() {
            return name().toLowerCase(); // Opcional: devuelve "admin" en el JSON
        }
    }
}