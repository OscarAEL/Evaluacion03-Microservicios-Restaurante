package com.eneque.platoservice.services;

import com.eneque.platoservice.dto.PlatoRequest;
import com.eneque.platoservice.exception.RecursoNoEncontradoException;
import com.eneque.platoservice.model.Plato;
import com.eneque.platoservice.repository.PlatoRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlatoServiceTest {
    private final PlatoRepository repository = mock(PlatoRepository.class);
    private final PlatoService service = new PlatoService(repository);

    @Test void creaPlatoConPrecioDecimal() {
        when(repository.save(any(Plato.class))).thenAnswer(i -> i.getArgument(0));
        var resultado = service.crear(new PlatoRequest(" Ceviche ", " Pescado fresco ", new BigDecimal("29.90"), true));
        assertEquals("Ceviche", resultado.nombre());
        assertEquals(new BigDecimal("29.90"), resultado.precio());
        assertTrue(resultado.disponible());
    }

    @Test void informaPlatoInexistente() {
        when(repository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(42L));
    }
}
