package com.onclass.capacidad.infrastructure.adapters.persistence.mapper;

import com.onclass.capacidad.domain.model.Capacidad;
import com.onclass.capacidad.infrastructure.adapters.persistence.CapacidadEntity;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-26T14:34:03-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.17 (Homebrew)"
)
@Component
public class CapacidadEntityMapperImpl implements CapacidadEntityMapper {

    @Override
    public Capacidad toModel(CapacidadEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String nombre = null;
        String descripcion = null;

        id = entity.getId();
        nombre = entity.getNombre();
        descripcion = entity.getDescripcion();

        List<Long> tecnologiaIds = null;

        Capacidad capacidad = new Capacidad( id, nombre, descripcion, tecnologiaIds );

        return capacidad;
    }

    @Override
    public CapacidadEntity toEntity(Capacidad capacidad) {
        if ( capacidad == null ) {
            return null;
        }

        CapacidadEntity capacidadEntity = new CapacidadEntity();

        capacidadEntity.setNombre( capacidad.nombre() );
        capacidadEntity.setDescripcion( capacidad.descripcion() );

        return capacidadEntity;
    }
}
