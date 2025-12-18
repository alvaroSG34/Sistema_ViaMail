package com.grupo04sa.sistema_via_mail.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo04sa.sistema_via_mail.exception.EntityNotFoundException;
import com.grupo04sa.sistema_via_mail.model.Venta;
import com.grupo04sa.sistema_via_mail.repository.VentaRepository;

/**
 * Servicio para gestión de ventas
 */
@Service
@Transactional
public class VentaService {
    private static final Logger log = LoggerFactory.getLogger(VentaService.class);

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    /**
     * Lista todas las ventas
     */
    @Transactional(readOnly = true)
    public List<Venta> listarTodas() {
        log.debug("Listando todas las ventas");
        return ventaRepository.findAll();
    }

    /**
     * Obtiene una venta por ID
     */
    @Transactional(readOnly = true)
    public Venta obtenerPorId(Long id) {
        log.debug("Buscando venta con ID: {}", id);
        return ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));
    }
}
