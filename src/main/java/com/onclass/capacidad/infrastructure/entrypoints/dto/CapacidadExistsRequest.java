package com.onclass.capacidad.infrastructure.entrypoints.dto;

import java.util.List;

public record CapacidadExistsRequest(
        List<Long> ids
) {}
