package com.eneque.pedidoservice.services;

import com.eneque.pedidoservice.dto.*;
import com.eneque.pedidoservice.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

@Service
public class ReferenciaService {
    private final RestClient clienteClient;
    private final RestClient platoClient;

    public ReferenciaService(RestClient.Builder builder,
            @Value("${services.cliente.url}") String clienteUrl,
            @Value("${services.plato.url}") String platoUrl) {
        clienteClient = builder.clone().baseUrl(clienteUrl).build();
        platoClient = builder.clone().baseUrl(platoUrl).build();
    }

    public void validarCliente(Long id) {
        try {
            ClienteExterno cliente = clienteClient.get().uri("/api/clientes/{id}", id).retrieve().body(ClienteExterno.class);
            if (cliente == null || !id.equals(cliente.id())) throw new ServicioExternoException(HttpStatus.BAD_GATEWAY, "Respuesta inválida de cliente-service");
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Cliente no encontrado: " + id);
        } catch (RestClientException ex) {
            throw fallo("cliente-service", ex);
        }
    }

    public PlatoExterno obtenerPlato(Long id) {
        try {
            PlatoExterno plato = platoClient.get().uri("/api/platos/{id}", id).retrieve().body(PlatoExterno.class);
            if (plato == null || !id.equals(plato.id()) || plato.precio() == null || plato.precio().signum() <= 0 || plato.disponible() == null)
                throw new ServicioExternoException(HttpStatus.BAD_GATEWAY, "Respuesta inválida de plato-service");
            return plato;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RecursoNoEncontradoException("Plato no encontrado: " + id);
        } catch (RestClientException ex) {
            throw fallo("plato-service", ex);
        }
    }

    private ServicioExternoException fallo(String servicio, RestClientException ex) {
        HttpStatus estado = ex instanceof ResourceAccessException ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.BAD_GATEWAY;
        return new ServicioExternoException(estado, "No se pudo consultar " + servicio);
    }
}
