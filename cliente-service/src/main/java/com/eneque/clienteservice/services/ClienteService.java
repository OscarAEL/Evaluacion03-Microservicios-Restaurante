package com.eneque.clienteservice.services;

import com.eneque.clienteservice.dto.*;
import com.eneque.clienteservice.exception.*;
import com.eneque.clienteservice.model.Cliente;
import com.eneque.clienteservice.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
public class ClienteService {
    private final ClienteRepository repository;
    public ClienteService(ClienteRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() { return repository.findAll().stream().map(this::respuesta).toList(); }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) { return respuesta(obtener(id)); }

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        String correo = normalizarCorreo(request.correo());
        if (repository.existsByCorreo(correo)) throw new CorreoDuplicadoException();
        Cliente cliente = new Cliente();
        asignar(cliente, request, correo);
        return respuesta(repository.saveAndFlush(cliente));
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = obtener(id);
        String correo = normalizarCorreo(request.correo());
        if (repository.existsByCorreoAndIdNot(correo, id)) throw new CorreoDuplicadoException();
        asignar(cliente, request, correo);
        return respuesta(repository.saveAndFlush(cliente));
    }

    @Transactional
    public void eliminar(Long id) { repository.delete(obtener(id)); }

    private Cliente obtener(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + id));
    }
    private String normalizarCorreo(String correo) { return correo.trim().toLowerCase(Locale.ROOT); }
    private void asignar(Cliente cliente, ClienteRequest request, String correo) {
        cliente.setNombre(request.nombre().trim());
        cliente.setApellido(request.apellido().trim());
        cliente.setCorreo(correo);
        cliente.setTelefono(request.telefono().trim());
    }
    private ClienteResponse respuesta(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNombre(), c.getApellido(), c.getCorreo(), c.getTelefono());
    }
}
