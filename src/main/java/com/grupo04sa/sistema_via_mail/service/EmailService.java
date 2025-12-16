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
            Store store = session.getStore("pop3");
            store.connect(pop3Host, pop3Username, pop3Password);

            // Abrir carpeta INBOX
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Obtener todos los mensajes
            Message[] messages = inbox.getMessages();
            log.debug("Total de mensajes en bandeja: {}", messages.length);

            // Parsear fecha de inicio del filtro
            LocalDate startDate = LocalDate.parse(filterStartDate);
            log.info("Filtrando correos desde: {}", startDate);

            // Filtrar solo los no leídos y desde la fecha especificada
            int filtradosPorFecha = 0;
            for (Message message : messages) {
                if (!message.isSet(Flags.Flag.SEEN)) {
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

            // No cerramos la conexión aquí para permitir marcar como leídos después

        } catch (Exception e) {
            log.error("Error al leer correos: {}", e.getMessage(), e);
        }

        return mensajesNoLeidos;
    }

    /**
     * Marca un mensaje como leído
     */
    public void marcarComoLeido(Message message) {
        try {
            message.setFlag(Flags.Flag.SEEN, true);
            log.debug("Mensaje marcado como leído");
        } catch (MessagingException e) {
            log.error("Error al marcar mensaje como leído: {}", e.getMessage());
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
     * Cierra la conexión con el servidor de correo
     */
    public void cerrarConexion(Message message) {
        try {
            if (message != null && message.getFolder() != null) {
                Folder folder = message.getFolder();
                if (folder.isOpen()) {
                    folder.close(true);
                }
                Store store = folder.getStore();
                if (store.isConnected()) {
                    store.close();
                }
            }
        } catch (MessagingException e) {
            log.error("Error al cerrar conexión: {}", e.getMessage());
        }
    }
}
