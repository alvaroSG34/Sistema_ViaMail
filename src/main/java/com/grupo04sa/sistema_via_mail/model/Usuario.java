package com.grupo04sa.sistema_via_mail.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad Usuario - Mapea tabla 'usuarios'
 * Roles: Admin, Secretaria, Usuario
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "ci", nullable = false, unique = true, length = 20)
    private String ci;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "correo", unique = true, length = 120)
    private String correo;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "rol", nullable = false, length = 20)
    private String rol; // Admin, Secretaria, Conductor, Cliente

    @Column(name = "img_url", columnDefinition = "TEXT")
    private String imgUrl;

    @Column(name = "tema_preferido", length = 20)
    private String temaPreferido;

    @Column(name = "modo_contraste", length = 20)
    private String modoContraste;

    @Column(name = "tamano_fuente", length = 20)
    private String tamanoFuente;

    @Column(name = "remember_token")
    private String rememberToken;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTemaPreferido() {
        return temaPreferido;
    }

    public void setTemaPreferido(String temaPreferido) {
        this.temaPreferido = temaPreferido;
    }

    public String getModoContraste() {
        return modoContraste;
    }

    public void setModoContraste(String modoContraste) {
        this.modoContraste = modoContraste;
    }

    public String getTamanoFuente() {
        return tamanoFuente;
    }

    public void setTamanoFuente(String tamanoFuente) {
        this.tamanoFuente = tamanoFuente;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
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
    public static UsuarioBuilder builder() {
        return new UsuarioBuilder();
    }

    public static class UsuarioBuilder {
        private String ci;
        private String nombre;
        private String apellido;
        private String rol;
        private String telefono;
        private String correo;
        private String password;

        public UsuarioBuilder ci(String ci) {
            this.ci = ci;
            return this;
        }

        public UsuarioBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public UsuarioBuilder apellido(String apellido) {
            this.apellido = apellido;
            return this;
        }

        public UsuarioBuilder rol(String rol) {
            this.rol = rol;
            return this;
        }

        public UsuarioBuilder telefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public UsuarioBuilder correo(String correo) {
            this.correo = correo;
            return this;
        }

        public UsuarioBuilder password(String password) {
            this.password = password;
            return this;
        }

        public Usuario build() {
            Usuario usuario = new Usuario();
            usuario.ci = this.ci;
            usuario.nombre = this.nombre;
            usuario.apellido = this.apellido;
            usuario.rol = this.rol;
            usuario.telefono = this.telefono;
            usuario.correo = this.correo;
            usuario.password = this.password;
            return usuario;
        }
    }

    // Métodos de utilidad
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public boolean isAdmin() {
        return "Admin".equals(rol);
    }

    public boolean isSecretaria() {
        return "Secretaria".equals(rol);
    }

    public boolean isSecretariaOrAdmin() {
        return "Secretaria".equals(rol) || "Admin".equals(rol);
    }

    public boolean isConductor() {
        return "Conductor".equals(rol);
    }

    public boolean isCliente() {
        return "Cliente".equals(rol);
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
