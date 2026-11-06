package com.eneque.platoservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PlatoRequest(
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Size(max = 500) String descripcion,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = 10, fraction = 2) BigDecimal precio,
        @NotNull Boolean disponible) {}
