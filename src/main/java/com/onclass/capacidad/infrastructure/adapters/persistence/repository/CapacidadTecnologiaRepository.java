package com.onclass.capacidad.infrastructure.adapters.persistence.repository;

import com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadTecnologiaEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CapacidadTecnologiaRepository extends ReactiveCrudRepository<CapacidadTecnologiaEntity, Long> {
    Flux<CapacidadTecnologiaEntity> findAllByCapacidadIdIn(List<Long> capacidadIds);
    Flux<CapacidadTecnologiaEntity> findByCapacidadId(Long capacidadId);
}
