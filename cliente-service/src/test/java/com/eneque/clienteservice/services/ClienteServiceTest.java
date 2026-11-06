package com.eneque.clienteservice.services;

import com.eneque.clienteservice.dto.ClienteRequest;
import com.eneque.clienteservice.exception.*;
import com.eneque.clienteservice.model.Cliente;
import com.eneque.clienteservice.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {
    private final ClienteRepository repository = mock(ClienteRepository.class);
    private final ClienteService service = new ClienteService(repository);

    @Test void rechazaCorreoDuplicado() {
        when(repository.existsByCorreo("ana@example.com")).thenReturn(true);
        ClienteRequest request = new ClienteRequest("Ana", "Rojas", " ANA@EXAMPLE.COM ", "999888777");
        assertThrows(CorreoDuplicadoException.class, () -> service.crear(request));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test void normalizaDatosAlCrear() {
        when(repository.saveAndFlush(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));
        var resultado = service.crear(new ClienteRequest(" Ana ", " Rojas ", " ANA@EXAMPLE.COM ", " 999888777 "));
        assertEquals("ana@example.com", resultado.correo());
        assertEquals("Ana", resultado.nombre());
        assertEquals("999888777", resultado.telefono());
    }

    @Test void informaClienteInexistente() {
        when(repository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.buscar(42L));
    }
}
