package com.onclass.capacidad.domain.spi;

import com.onclass.capacidad.domain.model.Capacidad;
import reactor.core.publisher.Mono;

public interface CapacidadPersistencePort {

    Mono<Boolean> existsByNombre(String nombre);
    Mono<Capacidad> save(Capacidad tecnologia);
}
