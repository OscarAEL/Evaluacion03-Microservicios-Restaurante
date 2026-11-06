package com.eneque.pedidoservice.exception;

import org.springframework.http.HttpStatus;

public class ServicioExternoException extends RuntimeException {
    private final HttpStatus estado;
    public ServicioExternoException(HttpStatus estado, String mensaje) { super(mensaje); this.estado = estado; }
    public HttpStatus getEstado() { return estado; }
}
