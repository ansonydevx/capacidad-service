package com.onclass.capacidad.it;

import com.onclass.capacidad.domain.spi.TecnologiaQueryPort;
import com.onclass.capacidad.infrastructure.adapters.persistence.repository.CapacidadRepository;
import com.onclass.capacidad.infrastructure.adapters.persistence.repository.CapacidadTecnologiaRepository;
import com.onclass.capacidad.infrastructure.entrypoints.dto.CapacidadDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class CapacidadIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CapacidadRepository capacidadRepository;

    @Autowired
    private CapacidadTecnologiaRepository capacidadTecnologiaRepository;

    @MockitoBean
    private TecnologiaQueryPort tecnologiaQueryPort;

    @BeforeEach
    void setUp() {
        capacidadTecnologiaRepository.deleteAll().block();
        capacidadRepository.deleteAll().block();

        when(tecnologiaQueryPort.existenTecnologias(anyList()))
                .thenReturn(Mono.just(true));
    }

    @Test
    void registrarCapacidad_ok() {
        CapacidadDTO request = new CapacidadDTO();
        request.setNombre("Backend");
        request.setDescripcion("Capacidades de backend");
        request.setTecnologiaIds(List.of(1L, 2L, 3L));

        webTestClient.post()
                .uri("/capacidades")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNumber()
                .jsonPath("$.nombre").isEqualTo("Backend")
                .jsonPath("$.tecnologiaIds.length()").isEqualTo(3);

        StepVerifier.create(capacidadRepository.findByNombre("Backend"))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(capacidadTecnologiaRepository.findAll())
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void listarCapacidades_paginado_y_ordenado_por_nombre() {
        var backend = capacidadRepository
                .save(new com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadEntity(
                        null, "Backend", "Capacidad backend"))
                .block();

        var frontend = capacidadRepository
                .save(new com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadEntity(
                        null, "Frontend", "Capacidad frontend"))
                .block();

        Assertions.assertNotNull(frontend);
        Assertions.assertNotNull(backend);
        capacidadTecnologiaRepository.saveAll(List.of(
                new com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadTecnologiaEntity(
                        backend.getId(), 1L),
                new com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadTecnologiaEntity(
                        backend.getId(), 2L),
                new com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadTecnologiaEntity(
                        backend.getId(), 3L),

                new com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadTecnologiaEntity(
                        frontend.getId(), 4L),
                new com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadTecnologiaEntity(
                        frontend.getId(), 5L),
                new com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadTecnologiaEntity(
                        frontend.getId(), 6L)
        )).collectList().block();

        when(tecnologiaQueryPort.obtenerTecnologiasPorId(anyList()))
                .thenReturn(Mono.just(
                        java.util.Map.of(
                                1L, "Java",
                                2L, "Spring",
                                3L, "SQL",
                                4L, "HTML",
                                5L, "CSS",
                                6L, "JS"
                        )
                ));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/capacidades")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .queryParam("sortBy", "nombre")
                        .queryParam("direction", "asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].nombre").isEqualTo("Backend")
                .jsonPath("$[0].tecnologias").isArray()
                .jsonPath("$[0].tecnologias[0].id").exists()
                .jsonPath("$[0].tecnologias[0].nombre").exists();
    }

}