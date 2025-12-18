package com.grupo04sa.sistema_via_mail.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo04sa.sistema_via_mail.exception.EntityNotFoundException;
import com.grupo04sa.sistema_via_mail.model.Ruta;
import com.grupo04sa.sistema_via_mail.model.Vehiculo;
import com.grupo04sa.sistema_via_mail.model.Viaje;
import com.grupo04sa.sistema_via_mail.repository.RutaRepository;
import com.grupo04sa.sistema_via_mail.repository.VehiculoRepository;
import com.grupo04sa.sistema_via_mail.repository.ViajeRepository;

/**
 * Servicio para gestión de viajes
 */
@Service
@Transactional
public class ViajeService {
    private static final Logger log = LoggerFactory.getLogger(ViajeService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ViajeRepository viajeRepository;
    private final RutaRepository rutaRepository;
    private final VehiculoRepository vehiculoRepository;

    public ViajeService(ViajeRepository viajeRepository, RutaRepository rutaRepository,
            VehiculoRepository vehiculoRepository) {
        this.viajeRepository = viajeRepository;
        this.rutaRepository = rutaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    /**
     * Crea un nuevo viaje
     * 
     * @param fechaSalidaStr Formato: "yyyy-MM-dd HH:mm"
     */
    public Viaje crear(Long rutaId, Long vehiculoId, String fechaSalidaStr,
            BigDecimal precio, Integer asientosTotales) {
        log.info("Creando viaje para ruta ID: {}", rutaId);

        // Buscar ruta
        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada con ID: " + rutaId));

        // Buscar vehículo
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con ID: " + vehiculoId));

        // Parsear fecha
        LocalDateTime fechaSalida = LocalDateTime.parse(fechaSalidaStr, DATE_FORMATTER);

        // Crear viaje
        Viaje viaje = new Viaje();
        viaje.setRuta(ruta);
        viaje.setVehiculo(vehiculo);
        viaje.setFechaSalida(fechaSalida);
        viaje.setPrecio(precio);
        viaje.setAsientosTotales(asientosTotales);
        viaje.setEstado("programado");

        viaje = viajeRepository.save(viaje);
        log.info("Viaje creado exitosamente (ID: {})", viaje.getId());

        return viaje;
    }

    /**
     * Lista todos los viajes
     */
    @Transactional(readOnly = true)
    public List<Viaje> listarTodos() {
        log.debug("Listando todos los viajes");
        return viajeRepository.findAll();
    }

    /**
     * Obtiene un viaje por ID
     */
    @Transactional(readOnly = true)
    public Viaje obtenerPorId(Long id) {
        log.debug("Buscando viaje con ID: {}", id);
        return viajeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Viaje no encontrado con ID: " + id));
    }

    /**
     * Actualiza un viaje
     */
    public Viaje actualizar(Long id, String fechaSalidaStr, String fechaLlegadaStr,
            BigDecimal precio, Integer asientosTotales, String estado) {
        log.info("Actualizando viaje ID: {}", id);

        Viaje viaje = obtenerPorId(id);

        if (fechaSalidaStr != null && !fechaSalidaStr.isBlank()) {
            viaje.setFechaSalida(LocalDateTime.parse(fechaSalidaStr, DATE_FORMATTER));
        }
        if (fechaLlegadaStr != null && !fechaLlegadaStr.isBlank()) {
            viaje.setFechaLlegada(LocalDateTime.parse(fechaLlegadaStr, DATE_FORMATTER));
        }
        if (precio != null) {
            viaje.setPrecio(precio);
        }
        if (asientosTotales != null) {
            viaje.setAsientosTotales(asientosTotales);
        }
        if (estado != null && !estado.isBlank()) {
            viaje.setEstado(estado.trim());
        }

        viaje = viajeRepository.save(viaje);
        log.info("Viaje actualizado exitosamente (ID: {})", viaje.getId());

        return viaje;
    }

    /**
     * Elimina un viaje (cambia estado a cancelado)
     */
    public void eliminar(Long id) {
        log.info("Cancelando viaje ID: {}", id);

        Viaje viaje = obtenerPorId(id);
        viaje.setEstado("cancelado");
        viajeRepository.save(viaje);

        log.info("Viaje cancelado exitosamente (ID: {})", id);
    }
}
