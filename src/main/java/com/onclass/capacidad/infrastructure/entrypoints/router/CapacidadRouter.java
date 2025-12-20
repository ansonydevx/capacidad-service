package com.onclass.capacidad.infrastructure.entrypoints.router;

import com.onclass.capacidad.infrastructure.entrypoints.handler.CapacidadHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CapacidadRouter {

    @Bean
    public RouterFunction<ServerResponse> tecnologiaRoutes(
            CapacidadHandler handler) {
        return route(POST("/capacidades"), handler::registrar)
                .andRoute(GET("/capacidades"), handler::listar);
    }
}
