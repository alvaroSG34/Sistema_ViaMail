package com.grupo04sa.sistema_via_mail.util;

import com.grupo04sa.sistema_via_mail.model.*;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utilidad para formatear respuestas de comandos en texto plano legible
 */
@Component
public class ResponseFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Formatea un Usuario para mostrar en respuesta
     */
    public String formatUsuario(Usuario usuario) {
        StringBuilder sb = new StringBuilder();
        sb.append("- ID: ").append(usuario.getId()).append("\n");
        sb.append("- CI: ").append(usuario.getCi()).append("\n");
        sb.append("- Nombre: ").append(usuario.getNombreCompleto()).append("\n");
        sb.append("- Rol: ").append(usuario.getRol()).append("\n");
        if (usuario.getTelefono() != null) {
            sb.append("- Teléfono: ").append(usuario.getTelefono()).append("\n");
        }
        if (usuario.getCorreo() != null) {
            sb.append("- Email: ").append(usuario.getCorreo()).append("\n");
        }
        sb.append("- Fecha Registro: ").append(usuario.getCreatedAt().format(DATE_FORMATTER)).append("\n");
        return sb.toString();
    }

    /**
     * Formatea lista de Usuarios
     */
    public String formatUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            return "No se encontraron usuarios.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Total: ").append(usuarios.size()).append(" usuario(s)\n\n");
        
        for (int i = 0; i < usuarios.size(); i++) {
            sb.append("Usuario #").append(i + 1).append(":\n");
            sb.append(formatUsuario(usuarios.get(i))).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Formatea un Vehículo
     */
    public String formatVehiculo(Vehiculo vehiculo) {
        StringBuilder sb = new StringBuilder();
        sb.append("- ID: ").append(vehiculo.getId()).append("\n");
        sb.append("- Placa: ").append(vehiculo.getPlaca()).append("\n");
        sb.append("- Marca/Modelo: ").append(vehiculo.getMarca()).append(" ").append(vehiculo.getModelo()).append("\n");
        if (vehiculo.getAnio() != null) {
            sb.append("- Año: ").append(vehiculo.getAnio()).append("\n");
        }
        if (vehiculo.getColor() != null) {
            sb.append("- Color: ").append(vehiculo.getColor()).append("\n");
        }
        if (vehiculo.getEstado() != null) {
            sb.append("- Estado: ").append(vehiculo.getEstado()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Formatea una Ruta
     */
    public String formatRuta(Ruta ruta) {
        StringBuilder sb = new StringBuilder();
        sb.append("- ID: ").append(ruta.getId()).append("\n");
        sb.append("- Nombre: ").append(ruta.getNombreCompleto()).append("\n");
        sb.append("- Origen: ").append(ruta.getOrigen()).append("\n");
        sb.append("- Destino: ").append(ruta.getDestino()).append("\n");
        return sb.toString();
    }

    /**
     * Formatea un Viaje
     */
    public String formatViaje(Viaje viaje) {
        StringBuilder sb = new StringBuilder();
        sb.append("- ID: ").append(viaje.getId()).append("\n");
        sb.append("- Fecha Salida: ").append(viaje.getFechaSalida().format(DATE_FORMATTER)).append("\n");
        if (viaje.getFechaLlegada() != null) {
            sb.append("- Fecha Llegada: ").append(viaje.getFechaLlegada().format(DATE_FORMATTER)).append("\n");
        }
        sb.append("- Precio: Bs. ").append(viaje.getPrecio()).append("\n");
        sb.append("- Asientos Totales: ").append(viaje.getAsientosTotales()).append("\n");
        sb.append("- Estado: ").append(viaje.getEstado()).append("\n");
        return sb.toString();
    }

    /**
     * Formatea un Boleto
     */
    public String formatBoleto(Boleto boleto) {
        StringBuilder sb = new StringBuilder();
        sb.append("- ID: ").append(boleto.getId()).append("\n");
        sb.append("- Asiento: ").append(boleto.getAsiento()).append("\n");
        sb.append("- Fecha Venta: ").append(boleto.getCreatedAt().format(DATE_FORMATTER)).append("\n");
        return sb.toString();
    }

    /**
     * Formatea una Encomienda
     */
    public String formatEncomienda(Encomienda encomienda) {
        StringBuilder sb = new StringBuilder();
        sb.append("- ID Venta: ").append(encomienda.getVentaId()).append("\n");
        sb.append("- Peso: ").append(encomienda.getPeso()).append(" kg\n");
        sb.append("- Destinatario: ").append(encomienda.getNombreDestinatario()).append("\n");
        if (encomienda.getDescripcion() != null) {
            sb.append("- Descripción: ").append(encomienda.getDescripcion()).append("\n");
        }
        sb.append("- Modalidad Pago: ").append(encomienda.getModalidadPago()).append("\n");
        sb.append("- Monto Pagado Origen: Bs. ").append(encomienda.getMontoPagadoOrigen()).append("\n");
        sb.append("- Monto Pagado Destino: Bs. ").append(encomienda.getMontoPagadoDestino()).append("\n");
        sb.append("- Fecha Registro: ").append(encomienda.getCreatedAt().format(DATE_FORMATTER)).append("\n");
        return sb.toString();
    }

    /**
     * Formatea una Venta
     */
    public String formatVenta(Venta venta) {
        StringBuilder sb = new StringBuilder();
        sb.append("- ID: ").append(venta.getId()).append("\n");
        sb.append("- Tipo: ").append(venta.getTipo()).append("\n");
        sb.append("- Monto Total: Bs. ").append(venta.getMontoTotal()).append("\n");
        sb.append("- Estado Pago: ").append(venta.getEstadoPago()).append("\n");
        sb.append("- Fecha: ").append(venta.getFecha().format(DATE_FORMATTER)).append("\n");
        return sb.toString();
    }

    /**
     * Formatea un PagoVenta
     */
    public String formatPago(PagoVenta pago) {
        StringBuilder sb = new StringBuilder();
        sb.append("- ID: ").append(pago.getId()).append("\n");
        sb.append("- Cuota: ").append(pago.getNumCuota()).append("\n");
        sb.append("- Monto: Bs. ").append(pago.getMonto()).append("\n");
        sb.append("- Método: ").append(pago.getMetodoPago()).append("\n");
        sb.append("- Estado: ").append(pago.getEstadoPago()).append("\n");
        if (pago.getFechaPago() != null) {
            sb.append("- Fecha Pago: ").append(pago.getFechaPago().format(DATE_FORMATTER)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Formatea lista genérica con contador
     */
    public String formatLista(List<?> lista, String nombreEntidad) {
        if (lista.isEmpty()) {
            return "No se encontraron " + nombreEntidad + ".";
        }
        
        return "Total: " + lista.size() + " " + nombreEntidad + " encontrado(s).";
    }

    /**
     * Formatea mensaje de éxito simple
     */
    public String formatExito(String mensaje) {
        return "✓ " + mensaje;
    }

    /**
     * Formatea mensaje de error simple
     */
    public String formatError(String mensaje) {
        return "✗ " + mensaje;
    }
}
