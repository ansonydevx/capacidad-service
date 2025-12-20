package com.onclass.capacidad.domain.usecase;

import com.onclass.capacidad.domain.api.CapacidadServicePort;
import com.onclass.capacidad.domain.enums.TechnicalMessage;
import com.onclass.capacidad.domain.exceptions.BusinessException;
import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.domain.spi.CapacidadPersistencePort;
import com.onclass.capacidad.domain.spi.TecnologiaQueryPort;
import reactor.core.publisher.Mono;

import java.util.HashSet;

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
