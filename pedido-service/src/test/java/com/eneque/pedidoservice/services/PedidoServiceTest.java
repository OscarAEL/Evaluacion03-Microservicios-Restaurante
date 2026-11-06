package com.eneque.pedidoservice.services;

import com.eneque.pedidoservice.dto.*;
import com.eneque.pedidoservice.exception.*;
import com.eneque.pedidoservice.model.*;
import com.eneque.pedidoservice.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {
    private final PedidoRepository repository = mock(PedidoRepository.class);
    private final ReferenciaService referencias = mock(ReferenciaService.class);
    private final PedidoService service = new PedidoService(repository, referencias);

    @Test void calculaPreciosYTotalDesdePlatoService() {
        when(referencias.obtenerPlato(1L)).thenReturn(new PlatoExterno(1L, new BigDecimal("29.90"), true));
        when(referencias.obtenerPlato(2L)).thenReturn(new PlatoExterno(2L, new BigDecimal("35.00"), true));
        when(repository.saveAndFlush(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));
        PedidoResponse pedido = service.crear(new CrearPedidoRequest(1L,
                List.of(new DetalleRequest(1L, 2), new DetalleRequest(2L, 1))));
        verify(referencias).validarCliente(1L);
        assertEquals(EstadoPedido.PENDIENTE, pedido.estado());
        assertEquals(new BigDecimal("94.80"), pedido.total());
        assertEquals(new BigDecimal("59.80"), pedido.detalles().get(0).subtotal());
        assertEquals(new BigDecimal("35.00"), pedido.detalles().get(1).subtotal());
    }

    @Test void rechazaPlatoNoDisponibleSinGuardar() {
        when(referencias.obtenerPlato(2L)).thenReturn(new PlatoExterno(2L, new BigDecimal("35.00"), false));
        assertThrows(ConflictoNegocioException.class, () -> service.crear(
                new CrearPedidoRequest(1L, List.of(new DetalleRequest(2L, 1)))));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test void rechazaTransicionDesdeCancelado() {
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoPedido.CANCELADO);
        when(repository.findById(7L)).thenReturn(Optional.of(pedido));
        assertThrows(ConflictoNegocioException.class, () -> service.cambiarEstado(7L, EstadoPedido.CONFIRMADO));
    }

    @Test void informaPedidoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.buscar(99L));
    }
}
