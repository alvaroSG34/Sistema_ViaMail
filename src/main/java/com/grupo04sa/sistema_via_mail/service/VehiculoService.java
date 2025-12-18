package com.grupo04sa.sistema_via_mail.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo04sa.sistema_via_mail.exception.EntityNotFoundException;
import com.grupo04sa.sistema_via_mail.exception.ValidationException;
import com.grupo04sa.sistema_via_mail.model.Usuario;
import com.grupo04sa.sistema_via_mail.model.Vehiculo;
import com.grupo04sa.sistema_via_mail.repository.UsuarioRepository;
import com.grupo04sa.sistema_via_mail.repository.VehiculoRepository;

/**
 * Servicio para gestión de vehículos
 */
@Service
@Transactional
public class VehiculoService {
    private static final Logger log = LoggerFactory.getLogger(VehiculoService.class);

    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository, UsuarioRepository usuarioRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crea un nuevo vehículo
     */
    public Vehiculo crear(String placa, String marca, String modelo, Short anio,
            String color, String tipo, String estado, Long conductorId) {
        log.info("Creando vehículo: {}", placa);

        // Validar que la placa no exista
        if (vehiculoRepository.findByPlaca(placa).isPresent()) {
            throw new ValidationException("placa", "Ya existe un vehículo con la placa: " + placa);
        }

        // Buscar conductor
        Usuario conductor = usuarioRepository.findById(conductorId)
                .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado con ID: " + conductorId));

        // Crear vehículo
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(placa.trim().toUpperCase());
        vehiculo.setMarca(marca.trim());
        vehiculo.setModelo(modelo.trim());
        vehiculo.setAnio(anio);
        vehiculo.setColor(color != null ? color.trim() : null);
        vehiculo.setTipo(tipo != null ? tipo.trim() : null);
        vehiculo.setEstado(estado != null ? estado.trim() : "activo");
        vehiculo.setConductor(conductor);

        vehiculo = vehiculoRepository.save(vehiculo);
        log.info("Vehículo creado exitosamente: {} (ID: {})", vehiculo.getPlaca(), vehiculo.getId());

        return vehiculo;
    }

    /**
     * Lista todos los vehículos
     */
    @Transactional(readOnly = true)
    public List<Vehiculo> listarTodos() {
        log.debug("Listando todos los vehículos");
        return vehiculoRepository.findAll();
    }

    /**
     * Obtiene un vehículo por ID
     */
    @Transactional(readOnly = true)
    public Vehiculo obtenerPorId(Long id) {
        log.debug("Buscando vehículo con ID: {}", id);
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con ID: " + id));
    }

    /**
     * Obtiene un vehículo por placa
     */
    @Transactional(readOnly = true)
    public Vehiculo obtenerPorPlaca(String placa) {
        log.debug("Buscando vehículo con placa: {}", placa);
        return vehiculoRepository.findByPlaca(placa.trim().toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no encontrado con placa: " + placa));
    }

    /**
     * Actualiza un vehículo
     */
    public Vehiculo actualizar(Long id, String marca, String modelo, Short anio,
            String color, String tipo, String estado, Long conductorId) {
        log.info("Actualizando vehículo ID: {}", id);

        Vehiculo vehiculo = obtenerPorId(id);

        if (marca != null && !marca.isBlank()) {
            vehiculo.setMarca(marca.trim());
        }
        if (modelo != null && !modelo.isBlank()) {
            vehiculo.setModelo(modelo.trim());
        }
        if (anio != null) {
            vehiculo.setAnio(anio);
        }
        if (color != null) {
            vehiculo.setColor(color.trim());
        }
        if (tipo != null) {
            vehiculo.setTipo(tipo.trim());
        }
        if (estado != null) {
            vehiculo.setEstado(estado.trim());
        }
        if (conductorId != null) {
            Usuario conductor = usuarioRepository.findById(conductorId)
                    .orElseThrow(() -> new EntityNotFoundException("Conductor no encontrado con ID: " + conductorId));
            vehiculo.setConductor(conductor);
        }

        vehiculo = vehiculoRepository.save(vehiculo);
        log.info("Vehículo actualizado exitosamente: {}", vehiculo.getPlaca());

        return vehiculo;
    }

    /**
     * Elimina un vehículo (lógicamente cambiando estado)
     */
    public void eliminar(Long id) {
        log.info("Eliminando vehículo ID: {}", id);

        Vehiculo vehiculo = obtenerPorId(id);
        vehiculo.setEstado("inactivo");
        vehiculoRepository.save(vehiculo);

        log.info("Vehículo eliminado exitosamente: {}", vehiculo.getPlaca());
    }
}
