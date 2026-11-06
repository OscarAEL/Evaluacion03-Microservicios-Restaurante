package com.eneque.pedidoservice.dto;

import com.eneque.pedidoservice.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record EstadoRequest(@NotNull EstadoPedido estado) {}
