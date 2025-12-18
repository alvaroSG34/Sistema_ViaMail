package com.grupo04sa.sistema_via_mail.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo04sa.sistema_via_mail.exception.EntityNotFoundException;
import com.grupo04sa.sistema_via_mail.model.Ruta;
import com.grupo04sa.sistema_via_mail.repository.RutaRepository;

/**
 * Servicio para gestión de rutas
 */
@Service
@Transactional
public class RutaService {
    private static final Logger log = LoggerFactory.getLogger(RutaService.class);

    private final RutaRepository rutaRepository;

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    /**
     * Crea una nueva ruta
     */
    public Ruta crear(String origen, String destino, String nombre) {
        log.info("Creando ruta: {} -> {}", origen, destino);

        Ruta ruta = new Ruta();
        ruta.setOrigen(origen.trim());
        ruta.setDestino(destino.trim());
        ruta.setNombre(nombre != null ? nombre.trim() : origen + " - " + destino);

        ruta = rutaRepository.save(ruta);
        log.info("Ruta creada exitosamente (ID: {})", ruta.getId());

        return ruta;
    }

    /**
     * Lista todas las rutas
     */
    @Transactional(readOnly = true)
    public List<Ruta> listarTodas() {
        log.debug("Listando todas las rutas");
        return rutaRepository.findAll();
    }

    /**
     * Obtiene una ruta por ID
     */
    @Transactional(readOnly = true)
    public Ruta obtenerPorId(Long id) {
        log.debug("Buscando ruta con ID: {}", id);
        return rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada con ID: " + id));
    }

    /**
     * Actualiza una ruta
     */
    public Ruta actualizar(Long id, String origen, String destino, String nombre) {
        log.info("Actualizando ruta ID: {}", id);

        Ruta ruta = obtenerPorId(id);

        if (origen != null && !origen.isBlank()) {
            ruta.setOrigen(origen.trim());
        }
        if (destino != null && !destino.isBlank()) {
            ruta.setDestino(destino.trim());
        }
        if (nombre != null && !nombre.isBlank()) {
            ruta.setNombre(nombre.trim());
        }

        ruta = rutaRepository.save(ruta);
        log.info("Ruta actualizada exitosamente (ID: {})", ruta.getId());

        return ruta;
    }

    /**
     * Elimina una ruta
     */
    public void eliminar(Long id) {
        log.info("Eliminando ruta ID: {}", id);

        Ruta ruta = obtenerPorId(id);
        rutaRepository.delete(ruta);

        log.info("Ruta eliminada exitosamente (ID: {})", id);
    }
}
