package com.onclass.capacidad.infrastructure.adapters.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Table("capacidades_tecnologias")
@Getter
@Setter
@AllArgsConstructor
public class CapacidadTecnologiaEntity {
    private Long capacidadId;
    private Long tecnologiaId;
}
