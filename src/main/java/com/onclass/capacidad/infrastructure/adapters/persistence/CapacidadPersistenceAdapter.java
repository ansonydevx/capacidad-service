package com.onclass.capacidad.infrastructure.adapters.persistence;

import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.domain.spi.CapacidadPersistencePort;
import com.onclass.capacidad.infrastructure.adapters.persistence.mapper.CapacidadEntityMapper;
import com.onclass.capacidad.infrastructure.adapters.persistence.repository.CapacidadRepository;
import com.onclass.capacidad.infrastructure.adapters.persistence.repository.CapacidadTecnologiaRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CapacidadPersistenceAdapter implements CapacidadPersistencePort {

    private final CapacidadRepository capacidadRepository;
    private final CapacidadTecnologiaRepository capacidadTecnologiaRepository;
    private final CapacidadEntityMapper mapper;

    @Override
    public Mono<Boolean> existsByNombre(String nombre) {
        return capacidadRepository.findByNombre(nombre)
                .hasElement();
    }

    @Override
    public Mono<Capacidad> save(Capacidad capacidad) {
        return capacidadRepository.save(mapper.toEntity(capacidad))
                .flatMap(saved ->
                        Flux.fromIterable(capacidad.tecnologiaIds())
                                .flatMap(id ->
                                        capacidadTecnologiaRepository.save(
                                                new CapacidadTecnologiaEntity(saved.getId(), id)))
                                .then(Mono.just(
                                        new Capacidad(saved.getId(), saved.getNombre(), saved.getDescripcion(), capacidad.tecnologiaIds()))));
    }
}
