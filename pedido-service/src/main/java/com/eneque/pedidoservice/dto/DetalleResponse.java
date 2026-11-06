package com.eneque.pedidoservice.dto;

import java.math.BigDecimal;

public record DetalleResponse(Long id, Long idPlato, Integer cantidad, BigDecimal precio, BigDecimal subtotal) {}
