package com.onclass.capacidad.domain.spi;

import reactor.core.publisher.Mono;

import java.util.List;

public interface TecnologiaQueryPort {
    Mono<Boolean> existenTecnologias(List<Long> tecnologiaIds);
}
