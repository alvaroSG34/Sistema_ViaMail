package com.grupo04sa.sistema_via_mail.exception;

/**
 * Excepción cuando no se encuentra una entidad
 */
public class EntityNotFoundException extends RuntimeException {
    
    private final String entidad;
    private final Object id;
    
    public EntityNotFoundException(String entidad, Object id) {
        super(entidad + " con ID " + id + " no encontrado(a)");
        this.entidad = entidad;
        this.id = id;
    }
    
    public EntityNotFoundException(String mensaje) {
        super(mensaje);
        this.entidad = null;
        this.id = null;
    }
    
    public String getEntidad() {
        return entidad;
    }
    
    public Object getId() {
        return id;
    }
}
