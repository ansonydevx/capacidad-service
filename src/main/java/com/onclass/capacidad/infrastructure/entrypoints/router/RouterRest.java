package com.onclass.capacidad.infrastructure.entrypoints.router;

import com.onclass.capacidad.infrastructure.entrypoints.handler.CapacidadHandler;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/capacidades",
                    method = RequestMethod.POST,
                    beanClass = CapacidadHandler.class,
                    beanMethod = "registrar"
            ),
            @RouterOperation(
                    path = "/capacidades",
                    method = RequestMethod.GET,
                    beanClass = CapacidadHandler.class,
                    beanMethod = "listar"
            ),
            @RouterOperation(
                    path = "/capacidades/exists",
                    method = RequestMethod.POST,
                    beanClass = CapacidadHandler.class,
                    beanMethod = "existen"
            ),
            @RouterOperation(
                    path = "/capacidades/by-ids",
                    method = RequestMethod.POST,
                    beanClass = CapacidadHandler.class,
                    beanMethod = "obtenerPorIds"
            )
    })
    public RouterFunction<ServerResponse> routerFunction(CapacidadHandler handler) {
        return RouterFunctions.route()
                .POST("/capacidades", handler::registrar)
                .GET("/capacidades", handler::listar)
                .POST("/capacidades/exists", handler::existen)
                .POST("/capacidades/by-ids", handler::obtenerPorIds)
                .build();
    }
}
