CREATE TABLE capacidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(90) NOT NULL
);

CREATE TABLE capacidades_tecnologias (
    capacidad_id BIGINT NOT NULL,
    tecnologia_id BIGINT NOT NULL,
    PRIMARY KEY (capacidad_id, tecnologia_id)
);