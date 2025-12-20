package com.onclass.capacidad.infrastructure.entrypoints.dto;

import lombok.Data;

import java.util.List;

@Data
public class CapacidadDTO {
    private String nombre;
    private String descripcion;
    private List<Long> tecnologiaIds;
}
