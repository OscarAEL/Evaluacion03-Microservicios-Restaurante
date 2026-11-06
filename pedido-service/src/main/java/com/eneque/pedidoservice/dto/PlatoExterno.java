package com.eneque.pedidoservice.dto;

import java.math.BigDecimal;

public record PlatoExterno(Long id, BigDecimal precio, Boolean disponible) {}
