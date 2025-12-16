package com.grupo04sa.sistema_via_mail.service;

import com.grupo04sa.sistema_via_mail.model.EmailLog;
import com.grupo04sa.sistema_via_mail.repository.EmailLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar logs de comandos ejecutados
 */
@Service
public class EmailLogService {
    private static final Logger log = LoggerFactory.getLogger(EmailLogService.class);

    private final EmailLogRepository emailLogRepository;

    public EmailLogService(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    /**
     * Registra la ejecución de un comando
     */
    @Transactional
    public EmailLog registrar(String emailRemitente, String comando, String parametros,
            String respuesta, String estado, String mensajeError,
            Integer tiempoEjecucion) {

        EmailLog emailLog = EmailLog.builder()
                .emailRemitente(emailRemitente)
                .comando(comando)
                .parametros(parametros)
                .respuesta(respuesta)
                .estado(estado)
                .mensajeError(mensajeError)
                .tiempoEjecucion(tiempoEjecucion)
                .build();

        return emailLogRepository.save(emailLog);
    }

    /**
     * Obtener logs por email
     */
    public List<EmailLog> obtenerPorEmail(String email) {
        return emailLogRepository.findByEmailRemitente(email);
    }

    /**
     * Obtener logs por comando
     */
    public List<EmailLog> obtenerPorComando(String comando) {
        return emailLogRepository.findByComando(comando);
    }

    /**
     * Obtener logs exitosos
     */
    public Long contarExitosos() {
        return emailLogRepository.countExitosos();
    }

    /**
     * Obtener logs con error
     */
    public Long contarErrores() {
        return emailLogRepository.countErrores();
    }
}
