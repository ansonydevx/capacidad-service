package com.onclass.capacidad.infrastructure.adapters.persistence;

import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.domain.spi.CapacidadPersistencePort;
import com.onclass.capacidad.infrastructure.adapters.persistence.mapper.CapacidadEntityMapper;
import com.onclass.capacidad.infrastructure.adapters.persistence.repository.CapacidadRepository;
import com.onclass.capacidad.infrastructure.adapters.persistence.repository.CapacidadTecnologiaRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CapacidadPersistenceAdapter implements CapacidadPersistencePort {

    private final CapacidadRepository capacidadRepository;
    private final CapacidadTecnologiaRepository capacidadTecnologiaRepository;
    private final CapacidadEntityMapper mapper;

    @Override
    public Mono<Long> countByIds(List<Long> ids) {
        return capacidadRepository.countByIdIn(ids);
    }

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

    @Override
    public Flux<Capacidad> findAll(int page, int size) {
        long offset = (long) page * size;

        return capacidadRepository.findAllPaged(size, offset)
                        .flatMap(entity ->
                                capacidadTecnologiaRepository.findByCapacidadId(entity.getId())
                                        .map(CapacidadTecnologiaEntity::getTecnologiaId)
                                        .collectList()
                                        .map(ids -> new Capacidad(
                                                entity.getId(),
                                                entity.getNombre(),
                                                entity.getDescripcion(),
                                                ids
                                        )));
    }

    @Override
    public Flux<Capacidad> findAllByIdIn(List<Long> ids) {
        return capacidadRepository.findAllByIdIn(ids)
                .collectList()
                .flatMapMany(capacidades -> {
                    if (capacidades.isEmpty()) {
                        return Flux.empty();
                    }

                    List<Long> capacidadIds = capacidades.stream()
                            .map(CapacidadEntity::getId)
                            .toList();

                    return capacidadTecnologiaRepository.findAllByCapacidadIdIn(capacidadIds)
                            .collectMultimap(
                                    CapacidadTecnologiaEntity::getCapacidadId,
                                    CapacidadTecnologiaEntity::getTecnologiaId
                            )
                            .flatMapMany(relMap ->
                                    Flux.fromIterable(capacidades)
                                            .map(entity ->
                                                    mapearCapacidad(entity, relMap)));
                });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return capacidadRepository.deleteById(id);
    }

    @Override
    public Flux<Long> findTecnologiaIdsByCapacidadIds(List<Long> capacidadIds) {
        return capacidadTecnologiaRepository.findAllByCapacidadIdIn(capacidadIds)
                .map(CapacidadTecnologiaEntity::getTecnologiaId)
                .distinct();
    }

    @Override
    public Mono<Long> countCapacidadesReferencingTecnologia(Long tecnologiaId) {
        return capacidadTecnologiaRepository.countByTecnologiaId(tecnologiaId);
    }

    @Override
    public Mono<Void> deleteRelacionesByCapacidadIds(List<Long> capacidadIds) {
        return capacidadTecnologiaRepository.deleteByCapacidadIdIn(capacidadIds);
    }

    private Capacidad mapearCapacidad(CapacidadEntity entity, Map<Long, Collection<Long>> relMap) {
        return new Capacidad(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                new ArrayList<>(relMap.getOrDefault(entity.getId(), List.of())
                )
        );
    }
}
