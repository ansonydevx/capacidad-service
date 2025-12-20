package com.onclass.capacidad.infrastructure.adapters.persistence.repository;

import com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CapacidadRepository extends ReactiveCrudRepository<CapacidadEntity, Long> {

    Mono<CapacidadEntity> findByNombre(String nombre);
}
