package com.grupo04sa.sistema_via_mail.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.grupo04sa.sistema_via_mail.dto.CommandRequest;
import com.grupo04sa.sistema_via_mail.exception.CommandException;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;

/**
 * Servicio para parsear comandos desde el asunto del correo
 * Formato esperado: COMANDO["param1","param2",param3]
 */
@Service
public class CommandParserService {

    private static final Logger log = LoggerFactory.getLogger(CommandParserService.class);

    // Patrón: COMANDO seguido de [ params ] opcional
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^([A-Z]+)(?:\\[(.*)\\])?$");

    /**
     * Parsea el mensaje de correo completo y extrae el comando y parámetros
     * 
     * @param mensaje        Mensaje de correo
     * @param emailRemitente Email del remitente
     * @return CommandRequest con comando y parámetros parseados
     */
    public CommandRequest parsear(Message mensaje, String emailRemitente) {
        try {
            String asunto = mensaje.getSubject();
            return parse(asunto, emailRemitente);
        } catch (MessagingException e) {
            throw new CommandException("Error al obtener asunto del mensaje: " + e.getMessage(), e);
        }
    }

    /**
     * Parsea el asunto del correo y extrae el comando y parámetros
     * 
     * @param asunto         Asunto del correo
     * @param emailRemitente Email del remitente
     * @return CommandRequest con comando y parámetros parseados
     */
    public CommandRequest parse(String asunto, String emailRemitente) {
        log.debug("Parseando comando del asunto: {}", asunto);

        if (asunto == null || asunto.trim().isEmpty()) {
            throw new CommandException("El asunto del correo está vacío");
        }

        String asuntoLimpio = asunto.trim();
        Matcher matcher = COMMAND_PATTERN.matcher(asuntoLimpio);

        if (!matcher.matches()) {
            throw new CommandException("Formato de comando inválido. Use: COMANDO[\"param1\",\"param2\",param3]");
        }

        String comando = matcher.group(1);
        String parametrosStr = matcher.group(2);

        List<String> parametros = new ArrayList<>();
        if (parametrosStr != null && !parametrosStr.trim().isEmpty()) {
            parametros = parseParametros(parametrosStr);
        }

        log.debug("Comando parseado: {} con {} parámetros", comando, parametros.size());

        return CommandRequest.builder()
                .comando(comando)
                .parametros(parametros)
                .emailRemitente(emailRemitente)
                .asuntoOriginal(asunto)
                .build();
    }

    /**
     * Parsea la lista de parámetros
     * Soporta strings entre comillas dobles y números sin comillas
     */
    private List<String> parseParametros(String parametrosStr) {
        List<String> parametros = new ArrayList<>();

        // Patrón para capturar: "string con espacios" o número
        Pattern paramPattern = Pattern.compile("\"([^\"]*)\"|([^,\\s]+)");
        Matcher matcher = paramPattern.matcher(parametrosStr);

        while (matcher.find()) {
            String param;
            if (matcher.group(1) != null) {
                // String entre comillas
                param = matcher.group(1);
            } else {
                // Número o valor sin comillas
                param = matcher.group(2);
            }

            // No agregar valores NULL explícitos, simplemente omitirlos
            if (param != null && !param.isEmpty() && !"null".equalsIgnoreCase(param)) {
                parametros.add(param);
            }
        }

        log.debug("Parámetros parseados: {}", parametros);
        return parametros;
    }

    /**
     * Valida que el número de parámetros sea el esperado
     */
    public void validarNumeroParametros(CommandRequest request, int esperados) {
        int recibidos = request.getParametros().size();
        if (recibidos != esperados) {
            throw new CommandException(
                    String.format("Número incorrecto de parámetros para %s. Esperados: %d, Recibidos: %d",
                            request.getComando(), esperados, recibidos));
        }
    }

    /**
     * Valida que el número de parámetros esté en un rango
     */
    public void validarNumeroParametros(CommandRequest request, int minimo, int maximo) {
        int recibidos = request.getParametros().size();
        if (recibidos < minimo || recibidos > maximo) {
            throw new CommandException(
                    String.format("Número incorrecto de parámetros para %s. Esperados: %d-%d, Recibidos: %d",
                            request.getComando(), minimo, maximo, recibidos));
        }
    }
}
