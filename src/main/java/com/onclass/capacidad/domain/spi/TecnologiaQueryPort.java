package com.onclass.capacidad.domain.spi;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface TecnologiaQueryPort {
    Mono<Boolean> existenTecnologias(List<Long> tecnologiaIds);
    Mono<Map<Long, String>> obtenerTecnologiasPorId(List<Long> ids);

    Mono<Void> eliminarTecnologias(List<Long> tecnologiaIds);
}
