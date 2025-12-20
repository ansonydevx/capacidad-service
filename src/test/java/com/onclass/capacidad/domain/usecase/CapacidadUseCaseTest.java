package com.onclass.capacidad.domain.usecase;

import com.onclass.capacidad.domain.enums.TechnicalMessage;
import com.onclass.capacidad.domain.exceptions.BusinessException;
import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.domain.spi.CapacidadPersistencePort;
import com.onclass.capacidad.domain.spi.TecnologiaQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.LongStream;

import static org.mockito.Mockito.*;

class CapacidadUseCaseTest {

    private CapacidadPersistencePort persistencePort;
    private TecnologiaQueryPort tecnologiaQueryPort;
    private CapacidadUseCase useCase;

    @BeforeEach
    void setup() {
        persistencePort = Mockito.mock(CapacidadPersistencePort.class);
        tecnologiaQueryPort = Mockito.mock(TecnologiaQueryPort.class);
        useCase = new CapacidadUseCase(
                persistencePort,
                tecnologiaQueryPort);
    }

    @Test
    void deberiaFallarSiTieneMenosDe3Tecnologias() {
        Capacidad capacidad = new Capacidad(
                null,
                "Backend",
                "Backend dev",
                List.of(1L, 2L)
        );

        StepVerifier.create(useCase.registrar(capacidad))
                .expectErrorMatches(ex ->
                        ex instanceof BusinessException &&
                                ((BusinessException) ex).getTechnicalMessage()
                                        == TechnicalMessage.MINIMO_TECNOLOGIAS)
                .verify();
    }

    @Test
    void deberiaFallarSiTieneTecnologiasRepetidas() {
        Capacidad capacidad = new Capacidad(
                null,
                "Backend",
                "Backend dev",
                List.of(1L, 1L, 2L)
        );

        StepVerifier.create(useCase.registrar(capacidad))
                .expectErrorMatches(ex ->
                        ex instanceof BusinessException &&
                                ((BusinessException) ex).getTechnicalMessage()
                                        == TechnicalMessage.TECNOLOGIAS_REPETIDAS)
                .verify();
    }

    @Test
    void deberiaFallarSiTieneMasDe20Tecnologias() {
        List<Long> ids = LongStream.rangeClosed(1, 21)
                .boxed()
                .toList();

        Capacidad capacidad = new Capacidad(
                null,
                "Fullstack",
                "Fullstack dev",
                ids
        );

        StepVerifier.create(useCase.registrar(capacidad))
                .expectErrorMatches(ex ->
                        ex instanceof BusinessException &&
                                ((BusinessException) ex).getTechnicalMessage()
                                        == TechnicalMessage.MAXIMO_TECNOLOGIAS)
                .verify();
    }

    @Test
    void deberiaRegistrarCapacidadCorrectamente() {
        Capacidad capacidad = new Capacidad(
                null,
                "Backend",
                "Backend dev",
                List.of(1L, 2L, 3L)
        );

        when(persistencePort.existsByNombre(any()))
                .thenReturn(Mono.just(false));

        when(tecnologiaQueryPort.existenTecnologias(any()))
                .thenReturn(Mono.just(true));

        when(persistencePort.save(any()))
                .thenReturn(Mono.just(capacidad));

        StepVerifier.create(useCase.registrar(capacidad))
                .expectNext(capacidad)
                .verifyComplete();
    }

}
