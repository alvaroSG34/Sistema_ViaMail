package com.grupo04sa.sistema_via_mail.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad Encomienda - Mapea tabla 'encomiendas'
 * Clave primaria: venta_id (relación 1:1 con Venta)
 */
@Entity
@Table(name = "encomiendas")
public class Encomienda {

    @Id
    @Column(name = "venta_id")
    private Long ventaId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    @Column(name = "peso", nullable = false, precision = 10, scale = 2)
    private BigDecimal peso;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "nombre_destinatario", nullable = false, length = 150)
    private String nombreDestinatario;

    @Column(name = "img_url", columnDefinition = "TEXT")
    private String imgUrl;

    @Column(name = "modalidad_pago", nullable = false, length = 20)
    private String modalidadPago; // origen, mixto, destino

    @Column(name = "metodo_pago_destino", length = 30)
    private String metodoPagoDestino;

    @Column(name = "monto_pagado_origen", precision = 10, scale = 2)
    private BigDecimal montoPagadoOrigen;

    @Column(name = "monto_pagado_destino", precision = 10, scale = 2)
    private BigDecimal montoPagadoDestino;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getModalidadPago() {
        return modalidadPago;
    }

    public void setModalidadPago(String modalidadPago) {
        this.modalidadPago = modalidadPago;
    }

    public String getMetodoPagoDestino() {
        return metodoPagoDestino;
    }

    public void setMetodoPagoDestino(String metodoPagoDestino) {
        this.metodoPagoDestino = metodoPagoDestino;
    }

    public BigDecimal getMontoPagadoOrigen() {
        return montoPagadoOrigen;
    }

    public void setMontoPagadoOrigen(BigDecimal montoPagadoOrigen) {
        this.montoPagadoOrigen = montoPagadoOrigen;
    }

    public BigDecimal getMontoPagadoDestino() {
        return montoPagadoDestino;
    }

    public void setMontoPagadoDestino(BigDecimal montoPagadoDestino) {
        this.montoPagadoDestino = montoPagadoDestino;
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
    public static EncomiendaBuilder builder() {
        return new EncomiendaBuilder();
    }

    public static class EncomiendaBuilder {
        private Venta venta;
        private Ruta ruta;
        private Viaje viaje;
        private BigDecimal peso;
        private String descripcion;
        private String nombreDestinatario;
        private String imgUrl;
        private String modalidadPago;
        private String metodoPagoDestino;
        private BigDecimal montoPagadoOrigen;
        private BigDecimal montoPagadoDestino;

        public EncomiendaBuilder venta(Venta venta) {
            this.venta = venta;
            return this;
        }

        public EncomiendaBuilder ruta(Ruta ruta) {
            this.ruta = ruta;
            return this;
        }

        public EncomiendaBuilder viaje(Viaje viaje) {
            this.viaje = viaje;
            return this;
        }

        public EncomiendaBuilder peso(BigDecimal peso) {
            this.peso = peso;
            return this;
        }

        public EncomiendaBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public EncomiendaBuilder nombreDestinatario(String nombreDestinatario) {
            this.nombreDestinatario = nombreDestinatario;
            return this;
        }

        public EncomiendaBuilder imgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
            return this;
        }

        public EncomiendaBuilder modalidadPago(String modalidadPago) {
            this.modalidadPago = modalidadPago;
            return this;
        }

        public EncomiendaBuilder metodoPagoDestino(String metodoPagoDestino) {
            this.metodoPagoDestino = metodoPagoDestino;
            return this;
        }

        public EncomiendaBuilder montoPagadoOrigen(BigDecimal montoPagadoOrigen) {
            this.montoPagadoOrigen = montoPagadoOrigen;
            return this;
        }

        public EncomiendaBuilder montoPagadoDestino(BigDecimal montoPagadoDestino) {
            this.montoPagadoDestino = montoPagadoDestino;
            return this;
        }

        public Encomienda build() {
            Encomienda encomienda = new Encomienda();
            encomienda.venta = this.venta;
            encomienda.ruta = this.ruta;
            encomienda.viaje = this.viaje;
            encomienda.peso = this.peso;
            encomienda.descripcion = this.descripcion;
            encomienda.nombreDestinatario = this.nombreDestinatario;
            encomienda.imgUrl = this.imgUrl;
            encomienda.modalidadPago = this.modalidadPago;
            encomienda.metodoPagoDestino = this.metodoPagoDestino;
            encomienda.montoPagadoOrigen = this.montoPagadoOrigen;
            encomienda.montoPagadoDestino = this.montoPagadoDestino;
            return encomienda;
        }
    }

    // Métodos de utilidad
    public boolean isModalidadOrigen() {
        return "origen".equals(modalidadPago);
    }

    public boolean isModalidadMixto() {
        return "mixto".equals(modalidadPago);
    }

    public boolean isModalidadDestino() {
        return "destino".equals(modalidadPago);
    }
}
