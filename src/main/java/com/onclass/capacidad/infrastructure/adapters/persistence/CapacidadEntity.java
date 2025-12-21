package com.onclass.capacidad.infrastructure.adapters.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("capacidades")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CapacidadEntity {

    @Id
    private Long id;
    private String nombre;
    private String descripcion;
}
