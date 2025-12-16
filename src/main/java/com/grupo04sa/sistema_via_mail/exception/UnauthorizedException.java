package com.grupo04sa.sistema_via_mail.exception;

/**
 * Excepción cuando el usuario no tiene permisos para ejecutar un comando
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String mensaje) {
        super(mensaje);
    }
    
    public UnauthorizedException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
