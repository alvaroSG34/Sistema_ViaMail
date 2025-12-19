package com.grupo04sa.sistema_via_mail.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo04sa.sistema_via_mail.exception.EntityNotFoundException;
import com.grupo04sa.sistema_via_mail.model.PagoVenta;
import com.grupo04sa.sistema_via_mail.model.Venta;
import com.grupo04sa.sistema_via_mail.repository.PagoVentaRepository;
import com.grupo04sa.sistema_via_mail.repository.VentaRepository;
import com.grupo04sa.sistema_via_mail.util.CommandValidator;

/**
 * Servicio para gestión de pagos de ventas
 */
@Service
@Transactional
public class PagoVentaService {
    private static final Logger log = LoggerFactory.getLogger(PagoVentaService.class);

    private final PagoVentaRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final CommandValidator validator;

    public PagoVentaService(PagoVentaRepository pagoRepository, VentaRepository ventaRepository,
            CommandValidator validator) {
        this.pagoRepository = pagoRepository;
        this.ventaRepository = ventaRepository;
        this.validator = validator;
    }

    /**
     * Registra un nuevo pago
     */
    public PagoVenta registrar(Long ventaId, BigDecimal monto, String metodoPago, Integer numCuota) {
        log.info("Registrando pago para venta ID: {}", ventaId);

        // Buscar venta
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + ventaId));

        // Normalizar método de pago
        metodoPago = validator.normalizeMetodoPago(metodoPago);

        // Calcular siguiente número de cuota automáticamente
        List<PagoVenta> pagosExistentes = pagoRepository.findByVentaId(ventaId);
        short siguienteCuota = (short) (pagosExistentes.size() + 1);

        // Validar que no haya sobrepago
        BigDecimal totalPagado = pagosExistentes.stream()
                .map(PagoVenta::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoPendiente = venta.getMontoTotal().subtract(totalPagado);

        if (saldoPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            throw new com.grupo04sa.sistema_via_mail.exception.ValidationException("pago",
                    "La venta ya está completamente pagada. Total: Bs. " + venta.getMontoTotal());
        }

        if (monto.compareTo(saldoPendiente) > 0) {
            throw new com.grupo04sa.sistema_via_mail.exception.ValidationException("monto",
                    "El monto excede el saldo pendiente. Saldo pendiente: Bs. " + saldoPendiente
                            + ", monto intentado: Bs. " + monto);
        }

        // Crear pago
        PagoVenta pago = new PagoVenta();
        pago.setVenta(venta);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setNumCuota(siguienteCuota); // Usar número de cuota calculado
        pago.setFechaPago(LocalDateTime.now());
        pago.setEstadoPago("pagado");

        pago = pagoRepository.save(pago);
        log.info("Pago registrado exitosamente (ID: {})", pago.getId());

        // Verificar si se completó el pago total
        actualizarEstadoVenta(venta);

        return pago;
    }

    /**
     * Actualiza el estado de la venta según los pagos realizados
     */
    private void actualizarEstadoVenta(Venta venta) {
        // Obtener todos los pagos de esta venta
        List<PagoVenta> pagos = pagoRepository.findByVentaId(venta.getId());

        // Calcular el total pagado
        BigDecimal totalPagado = pagos.stream()
                .map(PagoVenta::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Comparar con el monto total de la venta
        if (totalPagado.compareTo(venta.getMontoTotal()) >= 0) {
            venta.setEstadoPago("Pagado");
            ventaRepository.save(venta);
            log.info("Venta ID {} actualizada a estado 'Pagado'. Total pagado: {}",
                    venta.getId(), totalPagado);
        } else {
            log.debug("Venta ID {} aún pendiente. Pagado: {} de {}",
                    venta.getId(), totalPagado, venta.getMontoTotal());
        }
    }

    /**
     * Lista todos los pagos
     */
    @Transactional(readOnly = true)
    public List<PagoVenta> listarTodos() {
        log.debug("Listando todos los pagos");
        return pagoRepository.findAll();
    }

    /**
     * Obtiene un pago por ID
     */
    @Transactional(readOnly = true)
    public PagoVenta obtenerPorId(Long id) {
        log.debug("Buscando pago con ID: {}", id);
        return pagoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado con ID: " + id));
    }

    /**
     * Lista pagos por venta
     */
    @Transactional(readOnly = true)
    public List<PagoVenta> listarPorVenta(Long ventaId) {
        log.debug("Listando pagos de venta ID: {}", ventaId);
        return pagoRepository.findByVentaId(ventaId);
    }
}
