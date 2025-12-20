package com.onclass.capacidad.domain.api;

import com.onclass.capacidad.domain.model.Capacidad;
import reactor.core.publisher.Mono;

public interface CapacidadServicePort {
    
    Mono<Capacidad> registrar(Capacidad tecnologia);
}
