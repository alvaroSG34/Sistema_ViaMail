package com.grupo04sa.sistema_via_mail.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio para gestión de correos electrónicos
 * Lectura POP3 y envío SMTP
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${mail.pop3.host}")
    private String pop3Host;

    @Value("${mail.pop3.port}")
    private int pop3Port;

    @Value("${mail.pop3.username}")
    private String pop3Username;

    @Value("${mail.pop3.password}")
    private String pop3Password;

    @Value("${mail.pop3.ssl.enable}")
    private boolean pop3SslEnable;

    @Value("${mail.from.address}")
    private String fromAddress;

    @Value("${mail.from.name}")
    private String fromName;

    @Value("${mail.filter.start-date:2025-12-15}")
    private String filterStartDate;

    // Variables para mantener la conexión POP3 abierta durante el procesamiento
    private Folder currentFolder = null;
    private Store currentStore = null;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Lee correos no leídos desde el servidor POP3
     * 
     * @return Lista de mensajes no leídos
     */
    public List<Message> leerCorreosNoLeidos() {
        List<Message> mensajesNoLeidos = new ArrayList<>();

        try {
            // Cerrar conexión anterior si existe
            cerrarConexionPOP3();

            log.debug("Conectando al servidor POP3: {}:{}", pop3Host, pop3Port);

            // Configurar propiedades POP3
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.pop3.host", pop3Host);
            properties.put("mail.pop3.port", pop3Port);
            properties.put("mail.pop3.ssl.enable", pop3SslEnable);
            properties.put("mail.pop3.ssl.trust", "*");

            // Crear sesión y conectar
            Session session = Session.getInstance(properties);
            currentStore = session.getStore("pop3");
            currentStore.connect(pop3Host, pop3Username, pop3Password);

            // Abrir carpeta INBOX en modo READ_WRITE
            currentFolder = currentStore.getFolder("INBOX");
            currentFolder.open(Folder.READ_WRITE);

            // Obtener todos los mensajes
            Message[] messages = currentFolder.getMessages();
            log.debug("Total de mensajes en bandeja: {}", messages.length);

            // Parsear fecha de inicio del filtro
            LocalDate startDate = LocalDate.parse(filterStartDate);
            log.info("Filtrando correos desde: {}", startDate);

            // Filtrar mensajes por fecha (POP3 no maneja flags SEEN de forma confiable)
            // Solo procesamos mensajes que NO estén marcados para eliminación
            int filtradosPorFecha = 0;
            for (Message message : messages) {
                // Verificar que no esté marcado para eliminar
                if (!message.isSet(Flags.Flag.DELETED)) {
                    // Verificar fecha del mensaje
                    Date receivedDate = message.getReceivedDate();
                    if (receivedDate != null) {
                        LocalDate messageDate = receivedDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        if (!messageDate.isBefore(startDate)) {
                            mensajesNoLeidos.add(message);
                        } else {
                            filtradosPorFecha++;
                            log.debug("Correo descartado por fecha: {} (anterior a {})",
                                    messageDate, startDate);
                        }
                    } else {
                        // Si no tiene fecha, lo incluimos por seguridad
                        mensajesNoLeidos.add(message);
                    }
                }
            }

            if (filtradosPorFecha > 0) {
                log.info("Correos filtrados por fecha (anteriores a {}): {}",
                        startDate, filtradosPorFecha);
            }

            log.info("Correos no leídos encontrados: {}", mensajesNoLeidos.size());

            // Mantener conexión abierta para poder marcar y eliminar mensajes después

        } catch (Exception e) {
            log.error("Error al leer correos: {}", e.getMessage(), e);
            cerrarConexionPOP3(); // Cerrar en caso de error
        }

        return mensajesNoLeidos;
    }

    /**
     * Marca un mensaje como leído (POP3 no soporta flags, así que lo eliminamos)
     * En POP3, la única forma de evitar reprocesar es eliminar el mensaje
     */
    public void marcarComoLeido(Message message) {
        try {
            // POP3 no soporta flags SEEN de forma confiable
            // La solución es eliminar el mensaje después de procesarlo
            message.setFlag(Flags.Flag.DELETED, true);
            log.info("Mensaje marcado para eliminación (procesado correctamente)");
        } catch (MessagingException e) {
            log.error("Error al marcar mensaje para eliminar: {}", e.getMessage());
        }
    }

    /**
     * Extrae el asunto de un mensaje
     */
    public String getAsunto(Message message) {
        try {
            return message.getSubject();
        } catch (MessagingException e) {
            log.error("Error al obtener asunto: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Extrae el remitente de un mensaje
     */
    public String getRemitente(Message message) {
        try {
            Address[] from = message.getFrom();
            if (from != null && from.length > 0) {
                return ((InternetAddress) from[0]).getAddress();
            }
        } catch (MessagingException e) {
            log.error("Error al obtener remitente: {}", e.getMessage());
        }
        return "";
    }

    /**
     * Obtiene el email del remitente de un mensaje
     * Método alternativo a getRemitente()
     */
    public String obtenerEmailRemitente(Message message) {
        return getRemitente(message);
    }

    /**
     * Envía un correo
     * Método sobrecargado
     */
    public void enviarCorreo(String destinatario, String asunto, String contenido) {
        enviarRespuesta(destinatario, asunto, contenido);
    }

    /**
     * Envía un correo de respuesta
     * 
     * @param destinatario Email del destinatario
     * @param asunto       Asunto del correo
     * @param contenido    Contenido del mensaje
     */
    public void enviarRespuesta(String destinatario, String asunto, String contenido) {
        try {
            log.debug("Enviando respuesta a: {}", destinatario);

            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(fromAddress, fromName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Re: " + asunto);
            message.setText(contenido, "UTF-8", "plain");

            mailSender.send(message);

            log.info("Respuesta enviada exitosamente a: {}", destinatario);

        } catch (Exception e) {
            log.error("Error al enviar respuesta: {}", e.getMessage(), e);
            // No lanzar excepción - el error se registra pero no detiene el flujo
            // throw new RuntimeException("Error al enviar correo de respuesta", e);
        }
    }

    /**
     * Cierra la conexión POP3 actual y aplica expunge
     * El parámetro 'true' en folder.close() hace expunge (elimina mensajes marcados
     * como DELETED)
     */
    public void cerrarConexionPOP3() {
        try {
            if (currentFolder != null && currentFolder.isOpen()) {
                currentFolder.close(true); // true = expunge (eliminar mensajes marcados como DELETED)
                log.info("✅ Folder cerrado con expunge - Mensajes marcados eliminados del servidor");
                currentFolder = null;
            }
            if (currentStore != null && currentStore.isConnected()) {
                currentStore.close();
                log.debug("Conexión POP3 cerrada");
                currentStore = null;
            }
        } catch (MessagingException e) {
            log.error("Error al cerrar conexión POP3: {}", e.getMessage());
        }
    }
}
