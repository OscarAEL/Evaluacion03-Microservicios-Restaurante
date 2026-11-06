package com.eneque.pedidoservice.dto;

import jakarta.validation.constraints.*;

public record DetalleRequest(@NotNull @Positive Long idPlato, @NotNull @Positive Integer cantidad) {}
