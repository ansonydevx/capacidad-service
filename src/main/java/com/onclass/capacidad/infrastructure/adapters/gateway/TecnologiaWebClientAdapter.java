package com.onclass.capacidad.infrastructure.adapters.gateway;

import com.onclass.capacidad.domain.spi.TecnologiaQueryPort;
import com.onclass.capacidad.infrastructure.entrypoints.dto.TecnologiaExistsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class TecnologiaWebClientAdapter implements TecnologiaQueryPort {

    private final WebClient webClient;

    @Override
    public Mono<Boolean> existenTecnologias(List<Long> tecnologiaIds) {
        return webClient.post()
                .uri("/tecnologias/exists")
                .bodyValue(new TecnologiaExistsRequest(tecnologiaIds))
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
