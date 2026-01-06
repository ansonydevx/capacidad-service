package com.onclass.capacidad.infrastructure.entrypoints.error;

import java.time.Instant;

public record ErrorResponse(
   String code,
   String message,
   String param,
   Instant timestamp,
   String path
) {}
