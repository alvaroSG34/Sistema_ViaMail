package com.grupo04sa.sistema_via_mail.dto;

/**
 * DTO para respuestas de comandos que se enviarán por correo
 */
public class CommandResponse {

    private String comando;
    private String estado; // EXITOSO, ERROR
    private String mensaje;
    private String datos;
    private String mensajeError;

    public CommandResponse() {
    }

    public CommandResponse(String comando, String estado, String mensaje, String datos, String mensajeError) {
        this.comando = comando;
        this.estado = estado;
        this.mensaje = mensaje;
        this.datos = datos;
        this.mensajeError = mensajeError;
    }

    public static CommandResponseBuilder builder() {
        return new CommandResponseBuilder();
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public static class CommandResponseBuilder {
        private String comando;
        private String estado;
        private String mensaje;
        private String datos;
        private String mensajeError;

        public CommandResponseBuilder comando(String comando) {
            this.comando = comando;
            return this;
        }

        public CommandResponseBuilder estado(String estado) {
            this.estado = estado;
            return this;
        }

        public CommandResponseBuilder mensaje(String mensaje) {
            this.mensaje = mensaje;
            return this;
        }

        public CommandResponseBuilder datos(String datos) {
            this.datos = datos;
            return this;
        }

        public CommandResponseBuilder mensajeError(String mensajeError) {
            this.mensajeError = mensajeError;
            return this;
        }

        public CommandResponse build() {
            return new CommandResponse(comando, estado, mensaje, datos, mensajeError);
        }
    }

    /**
     * Formatea la respuesta completa para enviar por email
     */
    public String formatear() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("TRANS COMARAPA - SISTEMA VIA MAIL\n");
        sb.append("========================================\n\n");

        sb.append("COMANDO: ").append(comando).append("\n");
        sb.append("ESTADO: ").append(estado).append("\n\n");

        if (mensaje != null && !mensaje.isEmpty()) {
            sb.append("MENSAJE:\n");
            sb.append(mensaje).append("\n\n");
        }

        if (datos != null && !datos.isEmpty()) {
            sb.append("DATOS:\n");
            sb.append(datos).append("\n\n");
        }

        if (mensajeError != null && !mensajeError.isEmpty()) {
            sb.append("ERROR:\n");
            sb.append(mensajeError).append("\n\n");
        }

        sb.append("========================================\n");
        sb.append("Fecha: ").append(java.time.LocalDateTime.now()).append("\n");
        sb.append("========================================\n");

        return sb.toString();
    }
}
