package com.grupo04sa.sistema_via_mail.exception;

/**
 * Excepción para errores de validación de datos
 */
public class ValidationException extends RuntimeException {
    
    private final String campo;
    
    public ValidationException(String mensaje) {
        super(mensaje);
        this.campo = null;
    }
    
    public ValidationException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }
    
    public ValidationException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.campo = null;
    }
    
    public String getCampo() {
        return campo;
    }
    
    public String getMensajeCompleto() {
        if (campo != null) {
            return "Campo '" + campo + "': " + getMessage();
        }
        return getMessage();
    }
}
