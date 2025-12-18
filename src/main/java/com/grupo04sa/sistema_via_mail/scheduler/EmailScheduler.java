package com.grupo04sa.sistema_via_mail.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.grupo04sa.sistema_via_mail.dto.CommandRequest;
import com.grupo04sa.sistema_via_mail.dto.CommandResponse;
import com.grupo04sa.sistema_via_mail.service.CommandExecutorService;
import com.grupo04sa.sistema_via_mail.service.CommandParserService;
import com.grupo04sa.sistema_via_mail.service.EmailService;

import jakarta.mail.Message;

/**
 * Scheduler que procesa correos entrantes cada 60 segundos
 */
@Component
public class EmailScheduler {

    private static final Logger log = LoggerFactory.getLogger(EmailScheduler.class);

    private final EmailService emailService;
    private final CommandParserService parserService;
    private final CommandExecutorService executorService;

    @Value("${mail.enabled:true}")
    private boolean mailEnabled;

    public EmailScheduler(EmailService emailService, CommandParserService parserService,
            CommandExecutorService executorService) {
        this.emailService = emailService;
        this.parserService = parserService;
        this.executorService = executorService;

    }

    /**
     * Procesa correos no leídos cada 30 segundos (configurable)
     */
    @Scheduled(fixedDelayString = "${email.scheduler.polling.interval:30000}")
    public void procesarCorreos() {
        if (!mailEnabled) {
            log.debug("Procesamiento de correo deshabilitado");
            return;
        }

        log.info("Iniciando procesamiento de correos - {}", LocalDateTime.now());

        try {
            // Leer correos no leídos
            List<Message> mensajes = emailService.leerCorreosNoLeidos();

            if (mensajes.isEmpty()) {
                log.debug("No hay correos nuevos para procesar");
                return;
            }

            log.info("Procesando {} correo(s) nuevo(s)", mensajes.size());

            // Procesar cada correo
            for (Message mensaje : mensajes) {
                procesarCorreo(mensaje);
            }

            // Cerrar conexión y aplicar cambios (eliminar mensajes procesados)
            emailService.cerrarConexionPOP3();
            log.info("✅ Conexión POP3 cerrada - Mensajes procesados eliminados del servidor");

        } catch (Exception e) {
            log.error("Error al procesar correos: {}", e.getMessage(), e);
        }
    }

    /**
     * Procesa un correo individual
     */
    private void procesarCorreo(Message mensaje) {
        long inicio = System.currentTimeMillis();
        String emailRemitente = null;
        String comando = null;
        String parametrosStr = null;

        try {
            // Extraer email del remitente
            emailRemitente = emailService.obtenerEmailRemitente(mensaje);
            log.info("Procesando correo de: {}", emailRemitente);

            // Parsear comando del asunto
            CommandRequest request = parserService.parsear(mensaje, emailRemitente);
            comando = request.getComando();
            parametrosStr = request.getParametros().toString();

            log.info("Comando: {} con {} parámetro(s)", comando, request.getParametros().size());

            // Ejecutar comando
            CommandResponse response = executorService.ejecutar(request);

            // Preparar respuesta
            String asuntoRespuesta = "RE: " + comando + " - " + response.getEstado();
            String cuerpoRespuesta = response.formatear();

            // Intentar enviar respuesta (opcional - puede fallar sin afectar el
            // procesamiento)
            try {
                emailService.enviarCorreo(emailRemitente, asuntoRespuesta, cuerpoRespuesta);
                log.info("Respuesta enviada a: {} - Estado: {}", emailRemitente, response.getEstado());
            } catch (Exception emailEx) {
                log.warn("No se pudo enviar email de respuesta a {} (comando ejecutado correctamente): {}",
                        emailRemitente, emailEx.getMessage());
            }

            // Marcar como leído
            emailService.marcarComoLeido(mensaje);

            // Calcular tiempo de ejecución
            int tiempoEjecucion = (int) (System.currentTimeMillis() - inicio);

            log.info("Correo procesado exitosamente en {}ms", tiempoEjecucion);

        } catch (Exception e) {
            log.error("Error al procesar correo de {}: {}", emailRemitente, e.getMessage(), e);

            try {
                // Intentar enviar respuesta de error (opcional)
                String asuntoError = "RE: ERROR - " + (comando != null ? comando : "COMANDO_INVALIDO");
                String cuerpoError = "ERROR AL PROCESAR COMANDO\n\n" +
                        "Error: " + e.getMessage() + "\n\n" +
                        "Por favor verifica el formato del comando y vuelve a intentar.";

                if (emailRemitente != null) {
                    try {
                        emailService.enviarCorreo(emailRemitente, asuntoError, cuerpoError);
                    } catch (Exception emailEx) {
                        log.warn("No se pudo enviar email de error a {}: {}", emailRemitente, emailEx.getMessage());
                    }

                    // Registrar error en log
                    int tiempoEjecucion = (int) (System.currentTimeMillis() - inicio);

                }

                // Marcar como leído de todas formas
                emailService.marcarComoLeido(mensaje);

            } catch (Exception ex) {
                log.error("Error al enviar respuesta de error: {}", ex.getMessage(), ex);
            }
        }
    }

    /**
     * Log de inicio del scheduler
     */
    public void logInicio() {
        if (mailEnabled) {
            log.info("EmailScheduler iniciado - Polling cada 60 segundos");
        } else {
            log.info("EmailScheduler deshabilitado por configuración");
        }
    }
}
