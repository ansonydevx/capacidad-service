package com.onclass.capacidad.domain.api;

import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.infrastructure.entrypoints.dto.CapacidadListado;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadServicePort {
    
    Mono<Capacidad> registrar(Capacidad tecnologia);
    Mono<Boolean> existenPorIds(List<Long> ids);
    Flux<CapacidadListado> listar(int page, int size, String sortBy, String direction);
    Flux<CapacidadListado> obtenerPorIds(List<Long> ids);

    Mono<Void> eliminarPorIds(List<Long> ids);
    Mono<Integer> contarTecnologiasPorCapacidadIds(List<Long> capacidadIds);
}
