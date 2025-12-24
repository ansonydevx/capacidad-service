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

import java.util.Comparator;
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
                .flatMap(this::verificarDuplicidad)
                .flatMap(this::verificarTecnologiasExisten)
                .flatMap(persistencePort::save);
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
                    ordenar(capacidades, sortBy, direction);
                    return mapearConTecnologias(capacidades);
                });
    }

    @Override
    public Flux<CapacidadListado> obtenerPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }

        return persistencePort.findAllByIdIn(ids)
                .collectList()
                .flatMapMany(this::mapearConTecnologias);
    }

    private Mono<Capacidad> validar(Capacidad c) {
        if (c.tecnologiaIds() == null || c.tecnologiaIds().size() < 3)
            return Mono.error(new BusinessException(TechnicalMessage.MINIMO_TECNOLOGIAS));

        if (c.tecnologiaIds().size() > 20)
            return Mono.error(new BusinessException(TechnicalMessage.MAXIMO_TECNOLOGIAS));

        if (new HashSet<>(c.tecnologiaIds()).size() != c.tecnologiaIds().size())
            return Mono.error(new BusinessException(TechnicalMessage.TECNOLOGIAS_REPETIDAS));

        return Mono.just(c);
    }

    private Mono<Capacidad> verificarDuplicidad(Capacidad c) {
        return persistencePort.existsByNombre(c.nombre())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new BusinessException(TechnicalMessage.CAPACIDAD_DUPLICADA))
                        : Mono.just(c)
                );
    }

    private Mono<Capacidad> verificarTecnologiasExisten(Capacidad c) {
        return tecnologiaQueryPort.existenTecnologias(c.tecnologiaIds())
                .flatMap(existen -> Boolean.TRUE.equals(existen)
                        ? Mono.just(c)
                        : Mono.error(new BusinessException(TechnicalMessage.TECNOLOGIAS_NO_EXISTEN))
                );
    }

    private void ordenar(List<Capacidad> capacidades, String sortBy, String direction) {
        Comparator<Capacidad> comparator;

        if ("cantidad".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparingInt(c -> c.tecnologiaIds().size());
        } else {
            comparator = Comparator.comparing(
                    Capacidad::nombre,
                    String.CASE_INSENSITIVE_ORDER
            );
        }

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        capacidades.sort(comparator);
    }

    private Flux<CapacidadListado> mapearConTecnologias(List<Capacidad> capacidades) {
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
    }
}
