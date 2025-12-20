package com.onclass.capacidad.domain.model;

import java.util.List;

public record Capacidad(
        Long id,
        String nombre,
        String descripcion,
        List<Long> tecnologiaIds
) {}