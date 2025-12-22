package com.onclass.capacidad.domain.spi;

import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadQueryPort {
    Mono<Boolean> existenCapacidades(List<Long> capacidadIds);
}
