package com.onclass.capacidad.application.config;

import com.onclass.capacidad.domain.api.CapacidadServicePort;
import com.onclass.capacidad.domain.spi.CapacidadPersistencePort;
import com.onclass.capacidad.domain.spi.TecnologiaQueryPort;
import com.onclass.capacidad.domain.usecase.CapacidadUseCase;
import com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadPersistenceAdapter;
import com.onclass.capacidad.infrastructure.adapters.persistence.mapper.CapacidadEntityMapper;
import com.onclass.capacidad.infrastructure.adapters.persistence.repository.CapacidadRepository;
import com.onclass.capacidad.infrastructure.adapters.persistence.repository.CapacidadTecnologiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {

    private final CapacidadRepository capacidadRepository;
    private final CapacidadTecnologiaRepository capacidadTecnologiaRepository;
    private final CapacidadEntityMapper capacidadEntityMapper;

    @Bean
    public CapacidadPersistencePort capacidadPersistencePort() {
        return new CapacidadPersistenceAdapter(
                capacidadRepository, capacidadTecnologiaRepository, capacidadEntityMapper);
    }

    @Bean
    public CapacidadServicePort capacidadServicePort(
            CapacidadPersistencePort capacidadPersistencePort,
            TecnologiaQueryPort tecnologiaQueryPort
    ) {
        return new CapacidadUseCase(capacidadPersistencePort, tecnologiaQueryPort);
    }
}
