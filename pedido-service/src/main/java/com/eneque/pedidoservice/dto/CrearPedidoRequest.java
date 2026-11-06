package com.eneque.pedidoservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CrearPedidoRequest(
        @NotNull @Positive Long idCliente,
        @NotEmpty List<@NotNull @Valid DetalleRequest> detalles) {}
