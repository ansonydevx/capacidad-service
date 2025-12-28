package com.onclass.capacidad.infrastructure.adapters.gateway;

import com.onclass.capacidad.domain.spi.TecnologiaQueryPort;
import com.onclass.capacidad.infrastructure.entrypoints.dto.IdsRequest;
import com.onclass.capacidad.infrastructure.entrypoints.dto.TecnologiaExistsRequest;
import com.onclass.capacidad.infrastructure.entrypoints.dto.TecnologiaResumen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class TecnologiaWebClientAdapter implements TecnologiaQueryPort {

    private final WebClient webClient;

    @Override
    public Mono<Boolean> existenTecnologias(List<Long> tecnologiaIds) {
        return webClient
                .post()
                .uri("/tecnologias/exists")
                .bodyValue(new TecnologiaExistsRequest(tecnologiaIds))
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    @Override
    public Mono<Map<Long, String>> obtenerTecnologiasPorId(List<Long> ids) {
        return webClient.post()
                .uri("/tecnologias/by-ids")
                .bodyValue(ids)
                .retrieve()
                .bodyToFlux(TecnologiaResumen.class)
                .collectMap(
                        TecnologiaResumen::id,
                        TecnologiaResumen::nombre
                );
    }

    @Override
    public Mono<Void> eliminarTecnologias(List<Long> tecnologiaIds) {
        return webClient.post()
                .uri("/tecnologias/delete-by-ids")
                .bodyValue(new IdsRequest(tecnologiaIds))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.empty();
                    }

                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("Error en ms tecnologia")
                            .doOnNext(msg -> log.error("Error desde ms tecnologia: {}", msg))
                            .flatMap(msg -> Mono.error(new RuntimeException(msg)));
                });
    }
}
