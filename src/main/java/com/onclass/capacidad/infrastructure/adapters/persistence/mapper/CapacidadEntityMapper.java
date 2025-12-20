package com.onclass.capacidad.infrastructure.adapters.persistence.mapper;

import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CapacidadEntityMapper {
    @Mapping(target = "tecnologiaIds", ignore = true)
    Capacidad toModel(CapacidadEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "descripcion", target = "descripcion")
    CapacidadEntity toEntity(Capacidad capacidad);
}
