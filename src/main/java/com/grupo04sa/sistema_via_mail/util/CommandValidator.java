package com.grupo04sa.sistema_via_mail.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * Utilidad para validar datos en comandos
 */
@Component
public class CommandValidator {

    // Patrones de validación
    private static final Pattern CI_PATTERN = Pattern.compile("^[0-9]{5,15}$");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^[0-9]{5,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PLACA_PATTERN = Pattern.compile("^[A-Z0-9-]{3,15}$");

    /**
     * Valida Cédula de Identidad
     */
    public boolean isValidCI(String ci) {
        return ci != null && CI_PATTERN.matcher(ci).matches();
    }

    /**
     * Valida número de teléfono (5-15 dígitos)
     */
    public boolean isValidTelefono(String telefono) {
        return telefono != null && TELEFONO_PATTERN.matcher(telefono).matches();
    }

    /**
     * Valida correo electrónico
     */
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida placa de vehículo
     */
    public boolean isValidPlaca(String placa) {
        return placa != null && PLACA_PATTERN.matcher(placa.toUpperCase()).matches();
    }

    /**
     * Valida que el string no esté vacío
     */
    public boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Valida que un número sea positivo
     */
    public boolean isPositive(String value) {
        try {
            return new BigDecimal(value).compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida que un número sea válido
     */
    public boolean isValidNumber(String value) {
        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida que un entero sea válido
     */
    public boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida fecha en formato ISO (yyyy-MM-dd HH:mm:ss o yyyy-MM-dd'T'HH:mm:ss)
     */
    public boolean isValidDateTime(String dateTime) {
        try {
            LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
            try {
                LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return true;
            } catch (DateTimeParseException ex) {
                return false;
            }
        }
    }

    /**
     * Valida que la fecha sea futura
     */
    public boolean isFutureDateTime(String dateTime) {
        try {
            LocalDateTime parsed = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return parsed.isAfter(LocalDateTime.now());
        } catch (DateTimeParseException e) {
            try {
                LocalDateTime parsed = LocalDateTime.parse(dateTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return parsed.isAfter(LocalDateTime.now());
            } catch (DateTimeParseException ex) {
                return false;
            }
        }
    }

    /**
     * Valida rol de usuario
     */
    public boolean isValidRol(String rol) {
        return rol != null && ("Admin".equals(rol) ||
                "Secretaria".equals(rol) ||
                "Conductor".equals(rol) ||
                "Cliente".equals(rol));
    }

    /**
     * Valida estado de viaje
     */
    public boolean isValidEstadoViaje(String estado) {
        return estado != null && ("programado".equals(estado) ||
                "en_curso".equals(estado) ||
                "finalizado".equals(estado) ||
                "cancelado".equals(estado));
    }

    /**
     * Valida modalidad de pago de encomienda (case-insensitive)
     */
    public boolean isValidModalidadPago(String modalidad) {
        if (modalidad == null)
            return false;
        String normalized = modalidad.trim().toLowerCase();
        return "origen".equals(normalized) ||
                "mixto".equals(normalized) ||
                "destino".equals(normalized);
    }

    /**
     * Normaliza modalidad de pago a formato estándar (origen, mixto, destino)
     */
    public String normalizeModalidadPago(String modalidad) {
        if (modalidad == null)
            return null;
        return modalidad.trim().toLowerCase();
    }

    /**
     * Valida método de pago (case-insensitive)
     */
    public boolean isValidMetodoPago(String metodo) {
        if (metodo == null)
            return false;
        String normalized = metodo.trim();
        return "efectivo".equalsIgnoreCase(normalized) ||
                "qr".equalsIgnoreCase(normalized);
    }

    /**
     * Normaliza método de pago a formato estándar (Efectivo, QR)
     */
    public String normalizeMetodoPago(String metodo) {
        if (metodo == null)
            return null;
        String normalized = metodo.trim().toLowerCase();
        if ("efectivo".equals(normalized))
            return "Efectivo";
        if ("qr".equals(normalized))
            return "QR";
        return metodo; // Devuelve original si no coincide
    }

    /**
     * Valida estado de pago
     */
    public boolean isValidEstadoPago(String estado) {
        return estado != null && ("Pendiente".equals(estado) ||
                "Pagado".equals(estado) ||
                "anulado".equals(estado));
    }
}
