package com.grupo04sa.sistema_via_mail.exception;

/**
 * Excepción para errores de comandos
 */
public class CommandException extends RuntimeException {
    
    private final String codigo;
    
    public CommandException(String mensaje) {
        super(mensaje);
        this.codigo = "CMD_ERROR";
    }
    
    public CommandException(String mensaje, String codigo) {
        super(mensaje);
        this.codigo = codigo;
    }
    
    public CommandException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = "CMD_ERROR";
    }
    
    public CommandException(String mensaje, String codigo, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }
    
    public String getCodigo() {
        return codigo;
    }
}
