package com.onclass.capacidad.domain.api;

import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.infrastructure.entrypoints.dto.CapacidadListado;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CapacidadServicePort {
    
    Mono<Capacidad> registrar(Capacidad tecnologia);
    Flux<CapacidadListado> listar(
            int page,
            int size,
            String sortBy,
            String direction
    );
}
