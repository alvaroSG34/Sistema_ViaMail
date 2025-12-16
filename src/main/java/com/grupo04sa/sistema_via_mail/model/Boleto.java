package com.grupo04sa.sistema_via_mail.model;

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

/**
 * Entidad Boleto - Mapea tabla 'boletos'
 */
@Entity
@Table(name = "boletos")
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asiento", nullable = false, length = 5)
    private String asiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

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

    public String getAsiento() {
        return asiento;
    }

    public void setAsiento(String asiento) {
        this.asiento = asiento;
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
    public static BoletoBuilder builder() {
        return new BoletoBuilder();
    }

    public static class BoletoBuilder {
        private String asiento;
        private Venta venta;
        private Ruta ruta;
        private Viaje viaje;

        public BoletoBuilder asiento(String asiento) {
            this.asiento = asiento;
            return this;
        }

        public BoletoBuilder venta(Venta venta) {
            this.venta = venta;
            return this;
        }

        public BoletoBuilder ruta(Ruta ruta) {
            this.ruta = ruta;
            return this;
        }

        public BoletoBuilder viaje(Viaje viaje) {
            this.viaje = viaje;
            return this;
        }

        public Boleto build() {
            Boleto boleto = new Boleto();
            boleto.asiento = this.asiento;
            boleto.venta = this.venta;
            boleto.ruta = this.ruta;
            boleto.viaje = this.viaje;
            return boleto;
        }
    }
}
