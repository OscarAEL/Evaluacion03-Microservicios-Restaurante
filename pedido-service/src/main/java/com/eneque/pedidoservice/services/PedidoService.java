package com.eneque.pedidoservice.services;

import com.eneque.pedidoservice.dto.*;
import com.eneque.pedidoservice.exception.*;
import com.eneque.pedidoservice.model.*;
import com.eneque.pedidoservice.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {
    private final PedidoRepository repository;
    private final ReferenciaService referencias;
    public PedidoService(PedidoRepository repository, ReferenciaService referencias) {
        this.repository = repository; this.referencias = referencias;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() { return repository.findAll().stream().map(this::respuesta).toList(); }
    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id) { return respuesta(obtener(id)); }

    @Transactional
    public PedidoResponse crear(CrearPedidoRequest request) {
        referencias.validarCliente(request.idCliente());
        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setIdCliente(request.idCliente());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleRequest entrada : request.detalles()) {
            PlatoExterno plato = referencias.obtenerPlato(entrada.idPlato());
            if (!plato.disponible()) throw new ConflictoNegocioException("Plato no disponible: " + entrada.idPlato());
            DetallePedido detalle = new DetallePedido();
            detalle.setIdPlato(entrada.idPlato());
            detalle.setCantidad(entrada.cantidad());
            detalle.setPrecio(plato.precio());
            detalle.setSubtotal(plato.precio().multiply(BigDecimal.valueOf(entrada.cantidad())));
            pedido.agregarDetalle(detalle);
            total = total.add(detalle.getSubtotal());
        }
        pedido.setTotal(total);
        return respuesta(repository.saveAndFlush(pedido));
    }

    @Transactional
    public PedidoResponse cambiarEstado(Long id, EstadoPedido nuevo) {
        Pedido pedido = obtener(id);
        EstadoPedido actual = pedido.getEstado();
        if (actual != nuevo && (actual == EstadoPedido.CANCELADO ||
                (actual == EstadoPedido.CONFIRMADO && nuevo != EstadoPedido.CANCELADO)))
            throw new ConflictoNegocioException("Transición de estado no permitida: " + actual + " -> " + nuevo);
        pedido.setEstado(nuevo);
        return respuesta(repository.save(pedido));
    }

    @Transactional
    public void eliminar(Long id) { repository.delete(obtener(id)); }

    private Pedido obtener(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));
    }
    private PedidoResponse respuesta(Pedido p) {
        List<DetalleResponse> detalles = p.getDetalles().stream()
                .map(d -> new DetalleResponse(d.getId(), d.getIdPlato(), d.getCantidad(), d.getPrecio(), d.getSubtotal())).toList();
        return new PedidoResponse(p.getId(), p.getFecha(), p.getIdCliente(), p.getTotal(), p.getEstado(), detalles);
    }
}
