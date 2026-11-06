package com.eneque.pedidoservice.dto;

import com.eneque.pedidoservice.model.EstadoPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(Long id, LocalDateTime fecha, Long idCliente, BigDecimal total,
                             EstadoPedido estado, List<DetalleResponse> detalles) {}
