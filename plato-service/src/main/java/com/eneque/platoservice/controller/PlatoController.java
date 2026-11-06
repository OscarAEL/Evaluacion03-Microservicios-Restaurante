package com.eneque.platoservice.controller;

import com.eneque.platoservice.dto.*;
import com.eneque.platoservice.services.PlatoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/platos")
public class PlatoController {
    private final PlatoService service;
    public PlatoController(PlatoService service) { this.service = service; }
    @GetMapping public List<PlatoResponse> listar() { return service.listar(); }
    @GetMapping("/{id}") public PlatoResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<PlatoResponse> crear(@Valid @RequestBody PlatoRequest request) {
        PlatoResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/platos/" + creado.id())).body(creado);
    }
    @PutMapping("/{id}") public PlatoResponse actualizar(@PathVariable Long id, @Valid @RequestBody PlatoRequest request) {
        return service.actualizar(id, request);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
