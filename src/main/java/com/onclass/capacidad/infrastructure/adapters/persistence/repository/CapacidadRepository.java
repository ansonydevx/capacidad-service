package com.onclass.capacidad.infrastructure.adapters.persistence.repository;

import com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadRepository extends ReactiveCrudRepository<CapacidadEntity, Long> {

    Mono<CapacidadEntity> findByNombre(String nombre);
    Mono<Long> countByIdIn(List<Long> ids);

    @Query("""
            SELECT * FROM capacidades
            ORDER BY nombre
            LIMIT :size OFFSET :offset
            """)
    Flux<CapacidadEntity> findAllPaged(int size, long offset);
}
