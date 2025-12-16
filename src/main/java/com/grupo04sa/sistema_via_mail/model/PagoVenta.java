package com.grupo04sa.sistema_via_mail.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entidad PagoVenta - Mapea tabla 'pago_ventas'
 * Representa los pagos o cuotas de una venta
 */
@Entity
@Table(name = "pago_ventas", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "venta_id", "num_cuota" })
})
public class PagoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @Column(name = "num_cuota", nullable = false)
    private Short numCuota;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "metodo_pago", nullable = false, length = 30)
    private String metodoPago; // Efectivo, QR

    @Column(name = "qr_base64", columnDefinition = "TEXT")
    private String qrBase64;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_method_transaction_id")
    private String paymentMethodTransactionId;

    @Column(name = "estado_pago", nullable = false, length = 20)
    private String estadoPago; // Pendiente, Pagado, anulado

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Short getNumCuota() {
        return numCuota;
    }

    public void setNumCuota(Short numCuota) {
        this.numCuota = numCuota;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getQrBase64() {
        return qrBase64;
    }

    public void setQrBase64(String qrBase64) {
        this.qrBase64 = qrBase64;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentMethodTransactionId() {
        return paymentMethodTransactionId;
    }

    public void setPaymentMethodTransactionId(String paymentMethodTransactionId) {
        this.paymentMethodTransactionId = paymentMethodTransactionId;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Métodos de utilidad
    public boolean isPendiente() {
        return "Pendiente".equals(estadoPago);
    }

    public boolean isPagado() {
        return "Pagado".equals(estadoPago);
    }

    public boolean isAnulado() {
        return "anulado".equals(estadoPago);
    }

    public boolean isEfectivo() {
        return "Efectivo".equals(metodoPago);
    }

    public boolean isQR() {
        return "QR".equals(metodoPago);
    }
}
