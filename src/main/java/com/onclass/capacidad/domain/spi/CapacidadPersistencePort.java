package com.onclass.capacidad.domain.spi;

import com.onclass.capacidad.domain.model.Capacidad;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadPersistencePort {

    Mono<Long> countByIds(List<Long> ids);
    Mono<Boolean> existsByNombre(String nombre);
    Mono<Capacidad> save(Capacidad tecnologia);
    Flux<Capacidad> findAll(
            int page,
            int size);
}
