package com.eneque.pedidoservice.dto;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CrearPedidoRequestTest {
    @Test void rechazaIdsNoPositivosYListaVacia() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertFalse(validator.validate(new CrearPedidoRequest(0L, List.of())).isEmpty());
            assertFalse(validator.validate(new CrearPedidoRequest(1L,
                    List.of(new DetalleRequest(-1L, 0)))).isEmpty());
        }
    }
}
