package com.grupo04sa.sistema_via_mail.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad EmailLog - Nueva tabla para auditoría de comandos ejecutados
 * Se creará mediante migración Laravel en TransComarapa
 */
@Entity
@Table(name = "email_logs")
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_remitente", nullable = false)
    private String emailRemitente;

    @Column(name = "comando", nullable = false, length = 50)
    private String comando;

    @Column(name = "parametros", columnDefinition = "TEXT")
    private String parametros;

    @Column(name = "respuesta", columnDefinition = "TEXT")
    private String respuesta;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // EXITOSO, ERROR

    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;

    @Column(name = "tiempo_ejecucion")
    private Integer tiempoEjecucion; // en milisegundos

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailRemitente() {
        return emailRemitente;
    }

    public void setEmailRemitente(String emailRemitente) {
        this.emailRemitente = emailRemitente;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getParametros() {
        return parametros;
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public Integer getTiempoEjecucion() {
        return tiempoEjecucion;
    }

    public void setTiempoEjecucion(Integer tiempoEjecucion) {
        this.tiempoEjecucion = tiempoEjecucion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder manual
    public static EmailLogBuilder builder() {
        return new EmailLogBuilder();
    }

    public static class EmailLogBuilder {
        private String emailRemitente;
        private String comando;
        private String parametros;
        private String respuesta;
        private String estado;
        private String mensajeError;
        private Integer tiempoEjecucion;

        public EmailLogBuilder emailRemitente(String emailRemitente) {
            this.emailRemitente = emailRemitente;
            return this;
        }

        public EmailLogBuilder comando(String comando) {
            this.comando = comando;
            return this;
        }

        public EmailLogBuilder parametros(String parametros) {
            this.parametros = parametros;
            return this;
        }

        public EmailLogBuilder respuesta(String respuesta) {
            this.respuesta = respuesta;
            return this;
        }

        public EmailLogBuilder estado(String estado) {
            this.estado = estado;
            return this;
        }

        public EmailLogBuilder mensajeError(String mensajeError) {
            this.mensajeError = mensajeError;
            return this;
        }

        public EmailLogBuilder tiempoEjecucion(Integer tiempoEjecucion) {
            this.tiempoEjecucion = tiempoEjecucion;
            return this;
        }

        public EmailLog build() {
            EmailLog emailLog = new EmailLog();
            emailLog.emailRemitente = this.emailRemitente;
            emailLog.comando = this.comando;
            emailLog.parametros = this.parametros;
            emailLog.respuesta = this.respuesta;
            emailLog.estado = this.estado;
            emailLog.mensajeError = this.mensajeError;
            emailLog.tiempoEjecucion = this.tiempoEjecucion;
            return emailLog;
        }
    }
}
