package com.onclass.capacidad.domain.usecase;

import com.onclass.capacidad.domain.enums.TechnicalMessage;
import com.onclass.capacidad.domain.exceptions.BusinessException;
import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.domain.spi.CapacidadPersistencePort;
import com.onclass.capacidad.domain.spi.TecnologiaQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void deberiaListarCapacidadesOrdenadasPorNombreAsc() {
        Capacidad c1 = new Capacidad(1L, "Backend", "desc", List.of(1L, 2L, 3L));
        Capacidad c2 = new Capacidad(2L, "Cloud", "desc", List.of(4L, 5L, 6L));

        when(persistencePort.findAll(0, 10))
                .thenReturn(Flux.just(c1, c2));

        when(tecnologiaQueryPort.obtenerTecnologiasPorId(anyList()))
                .thenReturn(Mono.just(Map.of(
                        1L, "Java", 2L, "Spring", 3L, "Docker",
                        4L, "AWS", 5L, "Terraform", 6L, "Linux"
                )));

        StepVerifier.create(useCase.listar(0, 10, "nombre", "asc"))
                .assertNext(c -> assertEquals("Backend", c.nombre()))
                .assertNext(c -> assertEquals("Cloud", c.nombre()))
                .verifyComplete();
    }

    @Test
    void deberiaListarCapacidadesOrdenadasPorNombreDesc() {
        Capacidad c1 = new Capacidad(1L, "Backend", "desc", List.of(1L, 2L, 3L));
        Capacidad c2 = new Capacidad(2L, "Cloud", "desc", List.of(4L, 5L, 6L));

        when(persistencePort.findAll(0, 10))
                .thenReturn(Flux.just(c1, c2));

        when(tecnologiaQueryPort.obtenerTecnologiasPorId(anyList()))
                .thenReturn(Mono.just(Map.of(
                        1L, "Java", 2L, "Spring", 3L, "Docker",
                        4L, "AWS", 5L, "Terraform", 6L, "Linux"
                )));

        StepVerifier.create(
                        useCase.listar(0, 10, "nombre", "desc")
                )
                .assertNext(c -> assertEquals("Cloud", c.nombre()))
                .assertNext(c -> assertEquals("Backend", c.nombre()))
                .verifyComplete();
    }


    @Test
    void deberiaOrdenarPorCantidadTecnologiasAsc() {
        Capacidad c1 = new Capacidad(1L, "Backend", "desc", List.of(1L, 2L, 3L));
        Capacidad c2 = new Capacidad(2L, "Cloud", "desc", List.of(4L, 5L, 6L, 7L));

        when(persistencePort.findAll(0, 10))
                .thenReturn(Flux.just(c2, c1));

        when(tecnologiaQueryPort.obtenerTecnologiasPorId(anyList()))
                .thenReturn(Mono.just(Map.of(
                        1L, "Java", 2L, "Spring", 3L, "Docker",
                        4L, "AWS", 5L, "Terraform", 6L, "Linux", 7L, "K8s"
                )));

        StepVerifier.create(useCase.listar(0, 10, "cantidad", "asc"))
                .assertNext(c -> assertEquals(3, c.tecnologias().size()))
                .assertNext(c -> assertEquals(4, c.tecnologias().size()))
                .verifyComplete();
    }

    @Test
    void deberiaRetornarTecnologiasConIdYNombre() {
        Capacidad capacidad = new Capacidad(
                1L, "Backend", "desc", List.of(1L, 2L, 3L)
        );

        when(persistencePort.findAll(0, 10))
                .thenReturn(Flux.just(capacidad));

        when(tecnologiaQueryPort.obtenerTecnologiasPorId(anyList()))
                .thenReturn(Mono.just(Map.of(
                        1L, "Java",
                        2L, "Spring",
                        3L, "Docker"
                )));

        StepVerifier.create(
                        useCase.listar(0, 10, "nombre", "asc")
                )
                .assertNext(c -> {
                    assertEquals(3, c.tecnologias().size());
                    assertEquals("Java", c.tecnologias().get(0).nombre());
                })
                .verifyComplete();
    }

}
