package com.grupo04sa.sistema_via_mail.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo04sa.sistema_via_mail.exception.EntityNotFoundException;
import com.grupo04sa.sistema_via_mail.exception.ValidationException;
import com.grupo04sa.sistema_via_mail.model.Boleto;
import com.grupo04sa.sistema_via_mail.model.Usuario;
import com.grupo04sa.sistema_via_mail.model.Venta;
import com.grupo04sa.sistema_via_mail.model.Viaje;
import com.grupo04sa.sistema_via_mail.repository.BoletoRepository;
import com.grupo04sa.sistema_via_mail.repository.UsuarioRepository;
import com.grupo04sa.sistema_via_mail.repository.VentaRepository;
import com.grupo04sa.sistema_via_mail.repository.ViajeRepository;
import com.grupo04sa.sistema_via_mail.util.CommandValidator;

/**
 * Servicio de lógica de negocio para Boletos
 */
@Service
public class BoletoService {
    private static final Logger log = LoggerFactory.getLogger(BoletoService.class);

    private final BoletoRepository boletoRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;
    private final CommandValidator validator;

    public BoletoService(BoletoRepository boletoRepository, ViajeRepository viajeRepository,
            UsuarioRepository usuarioRepository, VentaRepository ventaRepository,
            CommandValidator validator) {
        this.boletoRepository = boletoRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.ventaRepository = ventaRepository;
        this.validator = validator;
    }

    /**
     * Vender boleto (crear venta + boleto)
     */
    @Transactional
    public Boleto venderBoleto(String asiento, Long viajeId, Long clienteId, String metodoPago) {
        log.debug("Vendiendo boleto - Asiento: {}, Viaje: {}, Cliente: {}", asiento, viajeId, clienteId);

        // Validaciones
        if (!validator.isNotEmpty(asiento)) {
            throw new ValidationException("asiento", "El número de asiento es requerido");
        }

        if (!validator.isValidMetodoPago(metodoPago)) {
            throw new ValidationException("metodoPago", "Método de pago inválido. Valores: Efectivo, QR");
        }

        // Verificar que el viaje existe
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new EntityNotFoundException("Viaje", viajeId));

        // Verificar que el viaje esté disponible para venta
        if (!viaje.isProgramado()) {
            throw new ValidationException("viaje", "El viaje no está en estado 'programado'");
        }

        if (!viaje.getFechaSalida().isAfter(LocalDateTime.now())) {
            throw new ValidationException("viaje", "La fecha y hora del viaje ya pasaron");
        }

        // Verificar que el asiento no esté ocupado
        if (boletoRepository.existsByViajeIdAndAsiento(viajeId, asiento)) {
            throw new ValidationException("asiento", "El asiento " + asiento + " ya está ocupado");
        }

        // Verificar que el asiento no exceda la capacidad
        Long boletosVendidos = boletoRepository.countByViajeId(viajeId);
        if (boletosVendidos >= viaje.getAsientosTotales()) {
            throw new ValidationException("viaje", "No hay asientos disponibles en este viaje");
        }

        // Verificar que el cliente existe
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", clienteId));

        if (!"Cliente".equals(cliente.getRol())) {
            throw new ValidationException("cliente", "El usuario debe tener rol Cliente");
        }

        // Crear venta
        Venta venta = Venta.builder()
                .fecha(LocalDateTime.now())
                .montoTotal(viaje.getPrecio())
                .tipo("Boleto")
                .estadoPago("Efectivo".equals(metodoPago) ? "Pagado" : "Pendiente")
                .usuario(cliente)
                .vehiculo(viaje.getVehiculo())
                .build();

        venta = ventaRepository.save(venta);

        // Crear boleto
        Boleto boleto = Boleto.builder()
                .asiento(asiento)
                .venta(venta)
                .ruta(viaje.getRuta())
                .viaje(viaje)
                .build();

        boleto = boletoRepository.save(boleto);

        log.info("Boleto vendido exitosamente - ID: {}, Asiento: {}, Viaje: {}",
                boleto.getId(), asiento, viajeId);

        return boleto;
    }

    /**
     * Listar todos los boletos
     */
    public List<Boleto> listarTodos() {
        return boletoRepository.findAll();
    }

    /**
     * Listar boletos por viaje
     */
    public List<Boleto> listarPorViaje(Long viajeId) {
        return boletoRepository.findByViajeId(viajeId);
    }

    /**
     * Obtener boleto por ID
     */
    public Boleto obtenerPorId(Long id) {
        return boletoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Boleto", id));
    }

    /**
     * Obtener asientos ocupados de un viaje
     */
    public List<String> obtenerAsientosOcupados(Long viajeId) {
        return boletoRepository.findAsientosOcupadosByViaje(viajeId);
    }
}
