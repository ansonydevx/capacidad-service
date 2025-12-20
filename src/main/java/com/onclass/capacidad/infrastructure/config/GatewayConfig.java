package com.onclass.capacidad.infrastructure.config;

import com.onclass.capacidad.domain.spi.TecnologiaQueryPort;
import com.onclass.capacidad.infrastructure.adapters.gateway.TecnologiaWebClientAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

    @Bean
    TecnologiaQueryPort tecnologiaQueryPort(WebClient tecnologiaWebClient) {
        return new TecnologiaWebClientAdapter(tecnologiaWebClient);
    }

    @Bean
    WebClient tecnologiaWebClient(@Value("${services.tecnologias.base-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
