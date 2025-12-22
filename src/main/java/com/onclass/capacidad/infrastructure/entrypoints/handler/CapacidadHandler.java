package com.onclass.capacidad.infrastructure.entrypoints.handler;

import com.onclass.capacidad.domain.api.CapacidadServicePort;
import com.onclass.capacidad.domain.enums.TechnicalMessage;
import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.infrastructure.entrypoints.dto.CapacidadDTO;
import com.onclass.capacidad.infrastructure.entrypoints.dto.CapacidadExistsRequest;
import com.onclass.capacidad.infrastructure.entrypoints.dto.CapacidadListado;
import com.onclass.capacidad.infrastructure.entrypoints.dto.IdsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CapacidadHandler {

    private final CapacidadServicePort capacidadServicePort;

    public Mono<ServerResponse> registrar(ServerRequest request) {
        return request.bodyToMono(CapacidadDTO.class)
                .map(dto -> new Capacidad(
                        null,
                        dto.getNombre(),
                        dto.getDescripcion(),
                        dto.getTecnologiaIds()))
                .flatMap(capacidadServicePort::registrar)
                .flatMap(c -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(TechnicalMessage.TECNOLOGIA_CREADA.getMessage()));
    }

    public Mono<ServerResponse> existen(ServerRequest request) {
        return request.bodyToMono(CapacidadExistsRequest.class)
                .doOnNext(req -> log.info("IDs recibidos: {}", req.ids()))
                .flatMap(req -> capacidadServicePort.existenPorIds(req.ids()))
                .flatMap(result -> ServerResponse.ok().bodyValue(result));
    }

    public Mono<ServerResponse> listar(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("nombre");
        String direction = request.queryParam("direction").orElse("asc");

        return ServerResponse.ok()
                .body(
                        capacidadServicePort.listar(page, size, sortBy, direction),
                        CapacidadListado.class
                );
    }

    public Mono<ServerResponse> listarPorIds(ServerRequest request) {
        return request.bodyToMono(IdsRequest.class)
                .flatMapMany(req -> capacidadServicePort.listarPorIds(req.ids()))
                .collectList()
                .flatMap(list ->
                        ServerResponse.ok().bodyValue(list));
    }
}
