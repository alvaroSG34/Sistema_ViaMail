package com.grupo04sa.sistema_via_mail.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad Venta - Mapea tabla 'ventas'
 * Tipos: Boleto, Encomienda
 * Estados de pago: Pendiente, Pagado, anulado
 */
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo; // Boleto, Encomienda

    @Column(name = "estado_pago", nullable = false, length = 20)
    private String estadoPago; // Pendiente, Pagado, anulado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Boleto> boletos = new ArrayList<>();

    @OneToOne(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Encomienda encomienda;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagoVenta> pagos = new ArrayList<>();

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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public List<Boleto> getBoletos() {
        return boletos;
    }

    public void setBoletos(List<Boleto> boletos) {
        this.boletos = boletos;
    }

    public Encomienda getEncomienda() {
        return encomienda;
    }

    public void setEncomienda(Encomienda encomienda) {
        this.encomienda = encomienda;
    }

    public List<PagoVenta> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoVenta> pagos) {
        this.pagos = pagos;
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

    // Builder manual
    public static VentaBuilder builder() {
        return new VentaBuilder();
    }

    public static class VentaBuilder {
        private LocalDateTime fecha;
        private BigDecimal montoTotal;
        private String tipo;
        private String estadoPago;
        private Usuario usuario;
        private Vehiculo vehiculo;
        private List<Boleto> boletos = new ArrayList<>();
        private List<PagoVenta> pagos = new ArrayList<>();

        public VentaBuilder fecha(LocalDateTime fecha) {
            this.fecha = fecha;
            return this;
        }

        public VentaBuilder montoTotal(BigDecimal montoTotal) {
            this.montoTotal = montoTotal;
            return this;
        }

        public VentaBuilder tipo(String tipo) {
            this.tipo = tipo;
            return this;
        }

        public VentaBuilder estadoPago(String estadoPago) {
            this.estadoPago = estadoPago;
            return this;
        }

        public VentaBuilder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public VentaBuilder vehiculo(Vehiculo vehiculo) {
            this.vehiculo = vehiculo;
            return this;
        }

        public VentaBuilder boletos(List<Boleto> boletos) {
            this.boletos = boletos;
            return this;
        }

        public VentaBuilder pagos(List<PagoVenta> pagos) {
            this.pagos = pagos;
            return this;
        }

        public Venta build() {
            Venta venta = new Venta();
            venta.fecha = this.fecha;
            venta.montoTotal = this.montoTotal;
            venta.tipo = this.tipo;
            venta.estadoPago = this.estadoPago;
            venta.usuario = this.usuario;
            venta.vehiculo = this.vehiculo;
            venta.boletos = this.boletos;
            venta.pagos = this.pagos;
            return venta;
        }
    }

    // Métodos de utilidad
    public boolean isBoleto() {
        return "Boleto".equals(tipo);
    }

    public boolean isEncomienda() {
        return "Encomienda".equals(tipo);
    }

    public boolean isPendiente() {
        return "Pendiente".equals(estadoPago);
    }

    public boolean isPagado() {
        return "Pagado".equals(estadoPago);
    }

    public boolean isAnulado() {
        return "anulado".equals(estadoPago);
    }
}
