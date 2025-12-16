package com.grupo04sa.sistema_via_mail.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para peticiones de comandos parseadas desde el asunto del correo
 * Formato esperado: COMANDO["param1","param2",param3]
 */
public class CommandRequest {

    private String comando;
    private List<String> parametros = new ArrayList<>();
    private String emailRemitente;
    private String asuntoOriginal;

    public CommandRequest() {
    }

    public CommandRequest(String comando, List<String> parametros, String emailRemitente, String asuntoOriginal) {
        this.comando = comando;
        this.parametros = parametros != null ? parametros : new ArrayList<>();
        this.emailRemitente = emailRemitente;
        this.asuntoOriginal = asuntoOriginal;
    }

    public static CommandRequestBuilder builder() {
        return new CommandRequestBuilder();
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public List<String> getParametros() {
        return parametros;
    }

    public void setParametros(List<String> parametros) {
        this.parametros = parametros;
    }

    public String getEmailRemitente() {
        return emailRemitente;
    }

    public void setEmailRemitente(String emailRemitente) {
        this.emailRemitente = emailRemitente;
    }

    public String getAsuntoOriginal() {
        return asuntoOriginal;
    }

    public void setAsuntoOriginal(String asuntoOriginal) {
        this.asuntoOriginal = asuntoOriginal;
    }

    public static class CommandRequestBuilder {
        private String comando;
        private List<String> parametros = new ArrayList<>();
        private String emailRemitente;
        private String asuntoOriginal;

        public CommandRequestBuilder comando(String comando) {
            this.comando = comando;
            return this;
        }

        public CommandRequestBuilder parametros(List<String> parametros) {
            this.parametros = parametros;
            return this;
        }

        public CommandRequestBuilder emailRemitente(String emailRemitente) {
            this.emailRemitente = emailRemitente;
            return this;
        }

        public CommandRequestBuilder asuntoOriginal(String asuntoOriginal) {
            this.asuntoOriginal = asuntoOriginal;
            return this;
        }

        public CommandRequest build() {
            return new CommandRequest(comando, parametros, emailRemitente, asuntoOriginal);
        }
    }
}
