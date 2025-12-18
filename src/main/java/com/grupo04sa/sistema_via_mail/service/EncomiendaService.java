package com.grupo04sa.sistema_via_mail.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo04sa.sistema_via_mail.exception.EntityNotFoundException;
import com.grupo04sa.sistema_via_mail.exception.ValidationException;
import com.grupo04sa.sistema_via_mail.model.Encomienda;
import com.grupo04sa.sistema_via_mail.model.PagoVenta;
import com.grupo04sa.sistema_via_mail.model.Ruta;
import com.grupo04sa.sistema_via_mail.model.Usuario;
import com.grupo04sa.sistema_via_mail.model.Venta;
import com.grupo04sa.sistema_via_mail.model.Viaje;
import com.grupo04sa.sistema_via_mail.repository.EncomiendaRepository;
import com.grupo04sa.sistema_via_mail.repository.PagoVentaRepository;
import com.grupo04sa.sistema_via_mail.repository.UsuarioRepository;
import com.grupo04sa.sistema_via_mail.repository.VentaRepository;
import com.grupo04sa.sistema_via_mail.repository.ViajeRepository;
import com.grupo04sa.sistema_via_mail.util.CommandValidator;

/**
 * Servicio de lógica de negocio para Encomiendas
 */
@Service
public class EncomiendaService {
    private static final Logger log = LoggerFactory.getLogger(EncomiendaService.class);

    private final EncomiendaRepository encomiendaRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;
    private final PagoVentaRepository pagoVentaRepository;
    private final CommandValidator validator;

    public EncomiendaService(EncomiendaRepository encomiendaRepository, ViajeRepository viajeRepository,
            UsuarioRepository usuarioRepository,
            VentaRepository ventaRepository, PagoVentaRepository pagoVentaRepository,
            CommandValidator validator) {
        this.encomiendaRepository = encomiendaRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.ventaRepository = ventaRepository;
        this.pagoVentaRepository = pagoVentaRepository;
        this.validator = validator;
    }

    /**
     * Registrar encomienda (crear venta + encomienda)
     * 
     * @param montoOrigen Monto pagado en origen (opcional, requerido para modalidad
     *                    mixto)
     */
    @Transactional
    public Encomienda registrarEncomienda(Long viajeId, Long clienteId,
            BigDecimal peso, String destinatario, BigDecimal precio,
            String modalidadPago, String metodoPago, BigDecimal montoOrigen) {
        log.debug("Registrando encomienda - Viaje: {}, Cliente: {}, Destinatario: {}",
                viajeId, clienteId, destinatario);

        // Validaciones
        if (!validator.isPositive(peso.toString())) {
            throw new ValidationException("peso", "El peso debe ser mayor a 0");
        }

        if (!validator.isNotEmpty(destinatario)) {
            throw new ValidationException("destinatario", "El nombre del destinatario es requerido");
        }

        if (!validator.isPositive(precio.toString())) {
            throw new ValidationException("precio", "El precio debe ser mayor a 0");
        }

        if (!validator.isValidModalidadPago(modalidadPago)) {
            throw new ValidationException("modalidadPago", "Modalidad inválida. Valores: origen, mixto, destino");
        }

        // Si modalidad es origen o mixto, validar método de pago
        if (("origen".equals(modalidadPago) || "mixto".equals(modalidadPago)) &&
                !validator.isValidMetodoPago(metodoPago)) {
            throw new ValidationException("metodoPago", "Método de pago inválido. Valores: Efectivo, QR");
        }

        // Verificar que el viaje existe y está disponible
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new EntityNotFoundException("Viaje", viajeId));

        boolean esDisponible = false;
        if ("programado".equals(viaje.getEstado())) {
            esDisponible = viaje.getFechaSalida().isAfter(LocalDateTime.now());
        } else if ("en_curso".equals(viaje.getEstado())) {
            esDisponible = viaje.getFechaLlegada() == null ||
                    viaje.getFechaLlegada().isAfter(LocalDateTime.now());
        }

        if (!esDisponible) {
            throw new ValidationException("viaje", "El viaje no está disponible");
        }

        // Obtener la ruta desde el viaje
        Ruta ruta = viaje.getRuta();

        // Verificar que el cliente existe
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", clienteId));

        // Calcular montos pagados según modalidad
        BigDecimal montoPagadoOrigen = BigDecimal.ZERO;
        BigDecimal montoPagadoDestino = BigDecimal.ZERO;
        String estadoPago = "Pendiente";

        if ("origen".equals(modalidadPago)) {
            // Modalidad origen: se paga todo en origen
            if ("Efectivo".equals(metodoPago)) {
                montoPagadoOrigen = precio;
                estadoPago = "Pagado";
            }
        } else if ("mixto".equals(modalidadPago)) {
            // Modalidad mixto: requiere monto a pagar en origen
            if (montoOrigen == null || montoOrigen.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("montoOrigen",
                        "Para modalidad mixto debe especificar el monto a pagar en origen");
            }
            if (montoOrigen.compareTo(precio) > 0) {
                throw new ValidationException("montoOrigen",
                        "El monto en origen no puede ser mayor al precio total");
            }
            if ("Efectivo".equals(metodoPago) || "QR".equals(metodoPago)) {
                montoPagadoOrigen = montoOrigen;
                montoPagadoDestino = BigDecimal.ZERO;
                // Si pagó todo en origen, está pagado completamente
                if (montoPagadoOrigen.compareTo(precio) == 0) {
                    estadoPago = "Pagado";
                } else {
                    estadoPago = "Pendiente";
                }
            }
        }
        // Si es "destino", todo queda en 0 y Pendiente (pago en destino)

        // Crear venta
        Venta venta = Venta.builder()
                .fecha(LocalDateTime.now())
                .montoTotal(precio)
                .tipo("Encomienda")
                .estadoPago(estadoPago)
                .usuario(cliente)
                .vehiculo(viaje.getVehiculo())
                .build();

        venta = ventaRepository.save(venta);

        // Registrar pago automáticamente si se pagó en origen
        if (montoPagadoOrigen.compareTo(BigDecimal.ZERO) > 0) {
            PagoVenta pago = new PagoVenta();
            pago.setVenta(venta);
            pago.setMonto(montoPagadoOrigen);
            pago.setMetodoPago(metodoPago);
            pago.setNumCuota((short) 1);
            pago.setFechaPago(LocalDateTime.now());
            pago.setEstadoPago("pagado");
            pagoVentaRepository.save(pago);
            log.info("Pago en origen registrado automáticamente: ${}", montoPagadoOrigen);
        }

        // Crear encomienda
        Encomienda encomienda = Encomienda.builder()
                .venta(venta)
                .ruta(ruta)
                .viaje(viaje)
                .peso(peso)
                .nombreDestinatario(destinatario)
                .modalidadPago(modalidadPago)
                .montoPagadoOrigen(montoPagadoOrigen)
                .montoPagadoDestino(montoPagadoDestino)
                .build();

        encomienda = encomiendaRepository.save(encomienda);

        log.info("Encomienda registrada exitosamente - Venta ID: {}", venta.getId());

        return encomienda;
    }

    /**
     * Listar todas las encomiendas
     */
    public List<Encomienda> listarTodas() {
        return encomiendaRepository.findAll();
    }

    /**
     * Listar encomiendas por ruta
     */
    public List<Encomienda> listarPorRuta(Long rutaId) {
        return encomiendaRepository.findByRutaId(rutaId);
    }

    /**
     * Obtener encomienda por venta ID
     */
    public Encomienda obtenerPorVentaId(Long ventaId) {
        return encomiendaRepository.findByVentaIdWithDetails(ventaId)
                .orElseThrow(() -> new EntityNotFoundException("Encomienda con VentaID", ventaId));
    }
}
