package com.eneque.platoservice.services;

import com.eneque.platoservice.dto.*;
import com.eneque.platoservice.exception.RecursoNoEncontradoException;
import com.eneque.platoservice.model.Plato;
import com.eneque.platoservice.repository.PlatoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PlatoService {
    private final PlatoRepository repository;
    public PlatoService(PlatoRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<PlatoResponse> listar() { return repository.findAll().stream().map(this::respuesta).toList(); }
    @Transactional(readOnly = true)
    public PlatoResponse buscar(Long id) { return respuesta(obtener(id)); }
    @Transactional
    public PlatoResponse crear(PlatoRequest request) {
        Plato plato = new Plato();
        asignar(plato, request);
        return respuesta(repository.save(plato));
    }
    @Transactional
    public PlatoResponse actualizar(Long id, PlatoRequest request) {
        Plato plato = obtener(id);
        asignar(plato, request);
        return respuesta(repository.save(plato));
    }
    @Transactional
    public void eliminar(Long id) { repository.delete(obtener(id)); }

    private Plato obtener(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado: " + id));
    }
    private void asignar(Plato plato, PlatoRequest request) {
        plato.setNombre(request.nombre().trim());
        plato.setDescripcion(request.descripcion().trim());
        plato.setPrecio(request.precio());
        plato.setDisponible(request.disponible());
    }
    private PlatoResponse respuesta(Plato p) {
        return new PlatoResponse(p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getDisponible());
    }
}
