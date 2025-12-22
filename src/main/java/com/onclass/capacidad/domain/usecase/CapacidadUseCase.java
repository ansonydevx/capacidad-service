package com.onclass.capacidad.domain.usecase;

import com.onclass.capacidad.domain.api.CapacidadServicePort;
import com.onclass.capacidad.domain.enums.TechnicalMessage;
import com.onclass.capacidad.domain.exceptions.BusinessException;
import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.domain.spi.CapacidadPersistencePort;
import com.onclass.capacidad.domain.spi.TecnologiaQueryPort;
import com.onclass.capacidad.infrastructure.entrypoints.dto.CapacidadListado;
import com.onclass.capacidad.infrastructure.entrypoints.dto.TecnologiaResumen;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;

public class CapacidadUseCase implements CapacidadServicePort {

    private final CapacidadPersistencePort persistencePort;
    private final TecnologiaQueryPort tecnologiaQueryPort;

    public CapacidadUseCase(CapacidadPersistencePort persistencePort, TecnologiaQueryPort tecnologiaQueryPort) {
        this.persistencePort = persistencePort;
        this.tecnologiaQueryPort = tecnologiaQueryPort;
    }

    @Override
    public Mono<Capacidad> registrar(Capacidad capacidad) {
        return validar(capacidad)
                .then(Mono.defer(() ->
                        persistencePort.existsByNombre(capacidad.nombre())
                                .flatMap(exists -> {
                                    if (Boolean.TRUE.equals(exists)) {
                                        return Mono.error(
                                                new BusinessException(TechnicalMessage.CAPACIDAD_DUPLICADA));
                                    }
                                    return tecnologiaQueryPort.existenTecnologias(
                                            capacidad.tecnologiaIds());
                                })
                                .flatMap(existen -> {
                                    if (Boolean.FALSE.equals(existen)) {
                                        return Mono.error(new BusinessException(
                                                TechnicalMessage.TECNOLOGIAS_NO_EXISTEN));
                                    }
                                    return persistencePort.save(capacidad);
                                })
                ));
    }

    @Override
    public Mono<Boolean> existenPorIds(List<Long> ids) {
        return persistencePort.countByIds(ids)
                .map(count -> count == ids.size());
    }

    @Override
    public Flux<CapacidadListado> listar(int page, int size, String sortBy, String direction) {
        return persistencePort.findAll(page, size)
                .collectList()
                .flatMapMany(capacidades -> {
                    if ("cantidad".equalsIgnoreCase(sortBy)) {
                        capacidades.sort((c1, c2) -> {
                            int compare = Integer.compare(
                                    c1.tecnologiaIds().size(),
                                    c2.tecnologiaIds().size()
                            );
                            return direction.equalsIgnoreCase("desc") ? -compare : compare;
                        });
                    }

                    if ("nombre".equalsIgnoreCase(sortBy)
                            && direction.equalsIgnoreCase("desc")) {
                        capacidades.sort(
                                (c1, c2) -> c2.nombre().compareToIgnoreCase(c1.nombre())
                        );
                    }

                    List<Long> tecnologiaIds = capacidades.stream()
                            .flatMap(c -> c.tecnologiaIds().stream())
                            .distinct()
                            .toList();

                    return tecnologiaQueryPort.obtenerTecnologiasPorId(tecnologiaIds)
                            .flatMapMany(tecnologiasMap ->
                                    Flux.fromIterable(capacidades)
                                            .map(capacidad ->
                                                    new CapacidadListado(
                                                            capacidad.id(),
                                                            capacidad.nombre(),
                                                            capacidad.tecnologiaIds().stream()
                                                                    .map(id -> new TecnologiaResumen(
                                                                            id,
                                                                            tecnologiasMap.get(id)
                                                                    ))
                                                                    .toList()
                                                    )));
                });
    }

    @Override
    public Flux<CapacidadListado> listarPorIds(List<Long> ids) {
        return persistencePort.findByIds(ids)
                .collectList()
                .flatMapMany(capacidades -> {
                    List<Long> tecnologiaIds = capacidades.stream()
                            .flatMap(c -> c.tecnologiaIds().stream())
                            .distinct()
                            .toList();

                    return tecnologiaQueryPort.obtenerTecnologiasPorId(tecnologiaIds)
                            .flatMapMany(tecnologiasMap ->
                                    Flux.fromIterable(capacidades)
                                            .map(capacidad ->
                                                    new CapacidadListado(
                                                            capacidad.id(),
                                                            capacidad.nombre(),
                                                            capacidad.tecnologiaIds().stream()
                                                                    .map(id -> new TecnologiaResumen(
                                                                            id,
                                                                            tecnologiasMap.get(id)
                                                                    ))
                                                                    .toList()
                                                    )));
                });
    }

    private Mono<Void> validar(Capacidad c) {
        if (c.tecnologiaIds() == null || c.tecnologiaIds().size() < 3)
            return Mono.error(new BusinessException(
                    TechnicalMessage.MINIMO_TECNOLOGIAS));

        if (c.tecnologiaIds().size() > 20)
            return Mono.error(new BusinessException(
                    TechnicalMessage.MAXIMO_TECNOLOGIAS));

        if (new HashSet<>(c.tecnologiaIds()).size() != c.tecnologiaIds().size())
            return Mono.error(new BusinessException(
                    TechnicalMessage.TECNOLOGIAS_REPETIDAS));

        return Mono.empty();
    }
}
