/*
 * Pago.java
 * Entidad que representa los pagos realizados por los usuarios.
 * 22 de abril de 2026
 */
package com.yoestudio.pago.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private OffsetDateTime fecha;

    @Column(nullable = false, length = 50)
    private String estado;

    @Column(name = "transaccion_externa_id")
    private String transaccionExternaId;
}
