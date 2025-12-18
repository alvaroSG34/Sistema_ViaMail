package com.grupo04sa.sistema_via_mail.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.grupo04sa.sistema_via_mail.dto.CommandRequest;
import com.grupo04sa.sistema_via_mail.dto.CommandResponse;
import com.grupo04sa.sistema_via_mail.exception.CommandException;
import com.grupo04sa.sistema_via_mail.exception.EntityNotFoundException;
import com.grupo04sa.sistema_via_mail.exception.UnauthorizedException;
import com.grupo04sa.sistema_via_mail.exception.ValidationException;
import com.grupo04sa.sistema_via_mail.model.Boleto;
import com.grupo04sa.sistema_via_mail.model.Encomienda;
import com.grupo04sa.sistema_via_mail.model.PagoVenta;
import com.grupo04sa.sistema_via_mail.model.Ruta;
import com.grupo04sa.sistema_via_mail.model.Usuario;
import com.grupo04sa.sistema_via_mail.model.Vehiculo;
import com.grupo04sa.sistema_via_mail.model.Venta;
import com.grupo04sa.sistema_via_mail.model.Viaje;
import com.grupo04sa.sistema_via_mail.util.ResponseFormatter;

/**
 * Servicio que ejecuta comandos recibidos por correo
 * Orquesta la ejecución de operaciones CRUD según el comando
 */
@Service
public class CommandExecutorService {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutorService.class);

    private final UsuarioService usuarioService;
    private final BoletoService boletoService;
    private final EncomiendaService encomiendaService;
    private final VehiculoService vehiculoService;
    private final RutaService rutaService;
    private final ViajeService viajeService;
    private final VentaService ventaService;
    private final PagoVentaService pagoService;
    private final CommandParserService parserService;
    private final CommandValidatorService validatorService;
    private final ResponseFormatter formatter;

    public CommandExecutorService(UsuarioService usuarioService, BoletoService boletoService,
            EncomiendaService encomiendaService, VehiculoService vehiculoService, RutaService rutaService,
            ViajeService viajeService, VentaService ventaService, PagoVentaService pagoService,
            CommandParserService parserService, CommandValidatorService validatorService,
            ResponseFormatter formatter) {
        this.usuarioService = usuarioService;
        this.boletoService = boletoService;
        this.encomiendaService = encomiendaService;
        this.vehiculoService = vehiculoService;
        this.rutaService = rutaService;
        this.viajeService = viajeService;
        this.ventaService = ventaService;
        this.pagoService = pagoService;
        this.parserService = parserService;
        this.validatorService = validatorService;
        this.formatter = formatter;
    }

    /**
     * Ejecuta un comando y retorna la respuesta
     */
    public CommandResponse ejecutar(CommandRequest request) {
        log.info("Ejecutando comando: {} desde: {}", request.getComando(), request.getEmailRemitente());

        try {
            // Validar permisos del usuario
            Usuario usuario = validatorService.validarPermisos(request);

            // Ejecutar comando según el tipo
            String comando = request.getComando();
            String resultado = switch (comando) {
                // Comando de ayuda
                case "HELP" -> ejecutarHELP(request, usuario);

                // Comandos de Usuarios
                case "INSUSU" -> ejecutarINSUSU(request);
                case "LISUSU" -> ejecutarLISUSU(request);
                case "GETUSU" -> ejecutarGETUSU(request);
                case "UPDUSU" -> ejecutarUPDUSU(request);
                case "DELUSU" -> ejecutarDELUSU(request);

                // Comandos de Vehículos
                case "INSVEH" -> ejecutarINSVEH(request);
                case "LISVEH" -> ejecutarLISVEH(request);
                case "GETVEH" -> ejecutarGETVEH(request);
                case "UPDVEH" -> ejecutarUPDVEH(request);
                case "DELVEH" -> ejecutarDELVEH(request);

                // Comandos de Rutas
                case "INSRUT" -> ejecutarINSRUT(request);
                case "LISRUT" -> ejecutarLISRUT(request);
                case "GETRUT" -> ejecutarGETRUT(request);
                case "UPDRUT" -> ejecutarUPDRUT(request);
                case "DELRUT" -> ejecutarDELRUT(request);

                // Comandos de Viajes
                case "INSVIA" -> ejecutarINSVIA(request);
                case "LISVIA" -> ejecutarLISVIA(request);
                case "GETVIA" -> ejecutarGETVIA(request);
                case "UPDVIA" -> ejecutarUPDVIA(request);
                case "DELVIA" -> ejecutarDELVIA(request);

                // Comandos de Boletos
                case "INSBOL" -> ejecutarINSBOL(request);
                case "LISBOL" -> ejecutarLISBOL(request);
                case "GETBOL" -> ejecutarGETBOL(request);

                // Comandos de Encomiendas
                case "INSENC" -> ejecutarINSENC(request);
                case "LISENC" -> ejecutarLISENC(request);
                case "GETENC" -> ejecutarGETENC(request);

                // Comandos de Ventas
                case "LISVEN" -> ejecutarLISVEN(request);
                case "GETVEN" -> ejecutarGETVEN(request);

                // Comandos de Pagos
                case "INSPAG" -> ejecutarINSPAG(request);
                case "LISPAG" -> ejecutarLISPAG(request);
                case "GETPAG" -> ejecutarGETPAG(request);

                default -> throw new CommandException("Comando no implementado: " + comando);
            };

            return CommandResponse.builder()
                    .comando(comando)
                    .estado("EXITOSO")
                    .mensaje("Comando ejecutado correctamente")
                    .datos(resultado)
                    .build();

        } catch (UnauthorizedException e) {
            log.warn("Error de autorización: {}", e.getMessage());
            return CommandResponse.builder()
                    .comando(request.getComando())
                    .estado("ERROR")
                    .mensaje("Error de autorización")
                    .mensajeError(e.getMessage())
                    .build();

        } catch (ValidationException e) {
            log.warn("Error de validación: {}", e.getMensajeCompleto());
            return CommandResponse.builder()
                    .comando(request.getComando())
                    .estado("ERROR")
                    .mensaje("Error de validación")
                    .mensajeError(e.getMensajeCompleto())
                    .build();

        } catch (EntityNotFoundException e) {
            log.warn("Entidad no encontrada: {}", e.getMessage());
            return CommandResponse.builder()
                    .comando(request.getComando())
                    .estado("ERROR")
                    .mensaje("Entidad no encontrada")
                    .mensajeError(e.getMessage())
                    .build();

        } catch (CommandException e) {
            log.error("Error en comando: {}", e.getMessage());
            return CommandResponse.builder()
                    .comando(request.getComando())
                    .estado("ERROR")
                    .mensaje("Error al ejecutar comando")
                    .mensajeError(e.getMessage())
                    .build();

        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            return CommandResponse.builder()
                    .comando(request.getComando())
                    .estado("ERROR")
                    .mensaje("Error inesperado")
                    .mensajeError("Error interno del sistema: " + e.getMessage())
                    .build();
        }
    }

    // ==================== COMANDOS DE USUARIOS ====================

    private String ejecutarINSUSU(CommandRequest request) {
        parserService.validarNumeroParametros(request, 6);
        List<String> params = request.getParametros();

        Usuario usuario = usuarioService.crear(
                params.get(0), // CI
                params.get(1), // Nombre
                params.get(2), // Apellido
                params.get(3), // Rol
                params.get(4), // Teléfono
                params.get(5) // Correo
        );

        return formatter.formatUsuario(usuario);
    }

    private String ejecutarLISUSU(CommandRequest request) {
        List<Usuario> usuarios;

        if (request.getParametros().isEmpty()) {
            usuarios = usuarioService.listarTodos();
        } else {
            parserService.validarNumeroParametros(request, 1);
            String rol = request.getParametros().get(0);
            usuarios = usuarioService.listarPorRol(rol);
        }

        return formatter.formatUsuarios(usuarios);
    }

    private String ejecutarGETUSU(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        String parametro = request.getParametros().get(0);

        Usuario usuario;
        try {
            // Intentar como ID numérico
            Long id = Long.parseLong(parametro);
            usuario = usuarioService.obtenerPorId(id);
        } catch (NumberFormatException e) {
            // Si no es número, buscar por CI
            usuario = usuarioService.obtenerPorCI(parametro);
        }

        return formatter.formatUsuario(usuario);
    }

    private String ejecutarUPDUSU(CommandRequest request) {
        parserService.validarNumeroParametros(request, 4, 5);
        List<String> params = request.getParametros();

        Long id = Long.parseLong(params.get(0));
        String nombre = params.size() > 1 ? params.get(1) : null;
        String apellido = params.size() > 2 ? params.get(2) : null;
        String telefono = params.size() > 3 ? params.get(3) : null;
        String correo = params.size() > 4 ? params.get(4) : null;

        Usuario usuario = usuarioService.actualizar(id, nombre, apellido, telefono, correo);

        return formatter.formatUsuario(usuario);
    }

    private String ejecutarDELUSU(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        Long id = Long.parseLong(request.getParametros().get(0));

        usuarioService.eliminar(id);

        return formatter.formatExito("Usuario eliminado correctamente (ID: " + id + ")");
    }

    // ==================== COMANDOS DE BOLETOS ====================

    private String ejecutarINSBOL(CommandRequest request) {
        parserService.validarNumeroParametros(request, 4);
        List<String> params = request.getParametros();

        Boleto boleto = boletoService.venderBoleto(
                params.get(0), // Asiento
                Long.parseLong(params.get(1)), // Viaje ID
                Long.parseLong(params.get(2)), // Cliente ID
                params.get(3) // Método pago
        );

        // Recargar el boleto con todas sus relaciones para formateo completo
        Boleto boletoCompleto = boletoService.obtenerPorId(boleto.getId());
        return formatter.formatBoleto(boletoCompleto);
    }

    private String ejecutarLISBOL(CommandRequest request) {
        List<Boleto> boletos;

        if (request.getParametros().isEmpty()) {
            boletos = boletoService.listarTodos();
        } else {
            parserService.validarNumeroParametros(request, 1);
            Long viajeId = Long.parseLong(request.getParametros().get(0));
            boletos = boletoService.listarPorViaje(viajeId);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Total: ").append(boletos.size()).append(" boleto(s)\n\n");

        for (int i = 0; i < boletos.size(); i++) {
            sb.append("Boleto #").append(i + 1).append(":\n");
            sb.append(formatter.formatBoleto(boletos.get(i))).append("\n");
        }

        return sb.toString();
    }

    private String ejecutarGETBOL(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        Long id = Long.parseLong(request.getParametros().get(0));

        Boleto boleto = boletoService.obtenerPorId(id);

        return formatter.formatBoleto(boleto);
    }

    // ==================== COMANDOS DE ENCOMIENDAS ====================

    private String ejecutarINSENC(CommandRequest request) {
        List<String> params = request.getParametros();

        // Validar número de parámetros: 7 básicos u 8 si incluye monto origen
        if (params.size() != 7 && params.size() != 8) {
            throw new ValidationException("parametros",
                    "Se esperan 7 parámetros (8 para modalidad mixto con monto origen)");
        }

        BigDecimal montoOrigen = null;
        if (params.size() == 8) {
            montoOrigen = new BigDecimal(params.get(7));
        }

        Encomienda encomienda = encomiendaService.registrarEncomienda(
                Long.parseLong(params.get(0)), // Viaje ID
                Long.parseLong(params.get(1)), // Cliente ID
                new BigDecimal(params.get(2)), // Peso
                params.get(3), // Destinatario
                new BigDecimal(params.get(4)), // Precio
                params.get(5), // Modalidad pago
                params.get(6), // Método pago
                montoOrigen // Monto origen (opcional)
        );

        return formatter.formatEncomienda(encomienda);
    }

    private String ejecutarLISENC(CommandRequest request) {
        List<Encomienda> encomiendas = encomiendaService.listarTodas();

        StringBuilder sb = new StringBuilder();
        sb.append("Total: ").append(encomiendas.size()).append(" encomienda(s)\n\n");

        for (int i = 0; i < encomiendas.size(); i++) {
            sb.append("Encomienda #").append(i + 1).append(":\n");
            sb.append(formatter.formatEncomienda(encomiendas.get(i))).append("\n");
        }

        return sb.toString();
    }

    private String ejecutarGETENC(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        Long ventaId = Long.parseLong(request.getParametros().get(0));

        Encomienda encomienda = encomiendaService.obtenerPorVentaId(ventaId);

        return formatter.formatEncomienda(encomienda);
    }

    // ==================== COMANDOS DE VEHÍCULOS ====================

    private String ejecutarINSVEH(CommandRequest request) {
        parserService.validarNumeroParametros(request, 8);
        List<String> params = request.getParametros();

        Vehiculo vehiculo = vehiculoService.crear(
                params.get(0), // Placa
                params.get(1), // Marca
                params.get(2), // Modelo
                Short.parseShort(params.get(3)), // Año
                params.get(4), // Color
                params.get(5), // Tipo
                params.get(6), // Estado
                Long.parseLong(params.get(7)) // Conductor ID
        );

        return formatter.formatVehiculo(vehiculo);
    }

    private String ejecutarLISVEH(CommandRequest request) {
        List<Vehiculo> vehiculos = vehiculoService.listarTodos();
        return formatter.formatVehiculos(vehiculos);
    }

    private String ejecutarGETVEH(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        String parametro = request.getParametros().get(0);

        Vehiculo vehiculo;
        try {
            Long id = Long.parseLong(parametro);
            vehiculo = vehiculoService.obtenerPorId(id);
        } catch (NumberFormatException e) {
            vehiculo = vehiculoService.obtenerPorPlaca(parametro);
        }

        return formatter.formatVehiculo(vehiculo);
    }

    private String ejecutarUPDVEH(CommandRequest request) {
        parserService.validarNumeroParametros(request, 8);
        List<String> params = request.getParametros();

        Vehiculo vehiculo = vehiculoService.actualizar(
                Long.parseLong(params.get(0)), // ID
                params.get(1), // Marca
                params.get(2), // Modelo
                Short.parseShort(params.get(3)), // Año
                params.get(4), // Color
                params.get(5), // Tipo
                params.get(6), // Estado
                Long.parseLong(params.get(7)) // Conductor ID
        );

        return formatter.formatVehiculo(vehiculo);
    }

    private String ejecutarDELVEH(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        Long id = Long.parseLong(request.getParametros().get(0));

        vehiculoService.eliminar(id);

        return formatter.formatExito("Vehículo eliminado correctamente (ID: " + id + ")");
    }

    // ==================== COMANDOS DE RUTAS ====================

    private String ejecutarINSRUT(CommandRequest request) {
        parserService.validarNumeroParametros(request, 2, 3);
        List<String> params = request.getParametros();

        Ruta ruta = rutaService.crear(
                params.get(0), // Origen
                params.get(1), // Destino
                params.size() > 2 ? params.get(2) : null // Nombre (opcional)
        );

        return formatter.formatRuta(ruta);
    }

    private String ejecutarLISRUT(CommandRequest request) {
        List<Ruta> rutas = rutaService.listarTodas();
        return formatter.formatRutas(rutas);
    }

    private String ejecutarGETRUT(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        Long id = Long.parseLong(request.getParametros().get(0));

        Ruta ruta = rutaService.obtenerPorId(id);

        return formatter.formatRuta(ruta);
    }

    private String ejecutarUPDRUT(CommandRequest request) {
        parserService.validarNumeroParametros(request, 3, 4);
        List<String> params = request.getParametros();

        Ruta ruta = rutaService.actualizar(
                Long.parseLong(params.get(0)), // ID
                params.get(1), // Origen
                params.get(2), // Destino
                params.size() > 3 ? params.get(3) : null // Nombre (opcional)
        );

        return formatter.formatRuta(ruta);
    }

    private String ejecutarDELRUT(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        Long id = Long.parseLong(request.getParametros().get(0));

        rutaService.eliminar(id);

        return formatter.formatExito("Ruta eliminada correctamente (ID: " + id + ")");
    }

    // ==================== COMANDOS DE VIAJES ====================

    private String ejecutarINSVIA(CommandRequest request) {
        parserService.validarNumeroParametros(request, 5);
        List<String> params = request.getParametros();

        Viaje viaje = viajeService.crear(
                Long.parseLong(params.get(0)), // Ruta ID
                Long.parseLong(params.get(1)), // Vehículo ID
                params.get(2), // Fecha salida (yyyy-MM-dd HH:mm)
                new BigDecimal(params.get(3)), // Precio
                Integer.parseInt(params.get(4)) // Asientos totales
        );

        return formatter.formatViaje(viaje);
    }

    private String ejecutarLISVIA(CommandRequest request) {
        List<Viaje> viajes = viajeService.listarTodos();
        return formatter.formatViajes(viajes);
    }

    private String ejecutarGETVIA(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        Long id = Long.parseLong(request.getParametros().get(0));

        Viaje viaje = viajeService.obtenerPorId(id);

        return formatter.formatViaje(viaje);
    }

    private String ejecutarUPDVIA(CommandRequest request) {
        parserService.validarNumeroParametros(request, 6);
        List<String> params = request.getParametros();

        Viaje viaje = viajeService.actualizar(
                Long.parseLong(params.get(0)), // ID
                params.get(1), // Fecha salida
                params.get(2), // Fecha llegada
                new BigDecimal(params.get(3)), // Precio
                Integer.parseInt(params.get(4)), // Asientos totales
                params.get(5) // Estado
        );

        return formatter.formatViaje(viaje);
    }

    private String ejecutarDELVIA(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        Long id = Long.parseLong(request.getParametros().get(0));

        viajeService.eliminar(id);

        return formatter.formatExito("Viaje cancelado correctamente (ID: " + id + ")");
    }

    // ==================== COMANDOS DE VENTAS ====================

    private String ejecutarLISVEN(CommandRequest request) {
        List<Venta> ventas = ventaService.listarTodas();
        return formatter.formatVentas(ventas);
    }

    private String ejecutarGETVEN(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        Long id = Long.parseLong(request.getParametros().get(0));

        Venta venta = ventaService.obtenerPorId(id);

        return formatter.formatVenta(venta);
    }

    // ==================== COMANDOS DE PAGOS ====================

    private String ejecutarINSPAG(CommandRequest request) {
        parserService.validarNumeroParametros(request, 3, 4);
        List<String> params = request.getParametros();

        PagoVenta pago = pagoService.registrar(
                Long.parseLong(params.get(0)), // Venta ID
                new BigDecimal(params.get(1)), // Monto
                params.get(2), // Método pago
                params.size() > 3 ? Integer.parseInt(params.get(3)) : null // Número cuota (opcional)
        );

        return formatter.formatPago(pago);
    }

    private String ejecutarLISPAG(CommandRequest request) {
        List<PagoVenta> pagos = pagoService.listarTodos();
        return formatter.formatPagos(pagos);
    }

    private String ejecutarGETPAG(CommandRequest request) {
        parserService.validarNumeroParametros(request, 1);
        Long id = Long.parseLong(request.getParametros().get(0));

        PagoVenta pago = pagoService.obtenerPorId(id);

        return formatter.formatPago(pago);
    }

    /**
     * Ejecuta el comando HELP - Muestra lista de comandos disponibles según el rol
     */
    private String ejecutarHELP(CommandRequest request, Usuario usuario) {
        StringBuilder sb = new StringBuilder();
        String rol = usuario.getRol();

        sb.append("╔════════════════════════════════════════════════════════╗\n");
        sb.append("║  SISTEMA TRANS COMARAPA - AYUDA DE COMANDOS           ║\n");
        sb.append("╚════════════════════════════════════════════════════════╝\n\n");

        // Mensaje personalizado según rol
        if (usuario.isAdmin()) {
            sb.append("🔑 Tienes acceso COMPLETO a todos los comandos del sistema.\n\n");
            agregarComandosAdmin(sb);
        } else if (usuario.isSecretaria()) {
            sb.append("📋 Como Secretaria, puedes gestionar operaciones de venta y consultas.\n\n");
            agregarComandosSecretaria(sb);
        } else if (usuario.isCliente()) {
            sb.append("👥 Como Cliente, puedes realizar consultas de información.\n\n");
            agregarComandosCliente(sb);
        } else {
            // Usuario anónimo o no registrado (security.permit-all=true)
            sb.append("⚠️  MODO PÚBLICO ACTIVO - Puedes usar todos los comandos sin restricciones.\n\n");
            agregarComandosAnonimo(sb); // Mostrar todos los comandos disponibles
        }

        sb.append("\n╔════════════════════════════════════════════════════════╗\n");
        sb.append("║  FORMATO DE USO                                        ║\n");
        sb.append("╚════════════════════════════════════════════════════════╝\n\n");
        sb.append("Envía un correo a: grupo04sa@tecnoweb.org.bo\n");
        sb.append("Asunto: COMANDO[\"param1\",\"param2\",...]\n");
        sb.append("Ejemplo: LISUSU o GETUSU[\"1\"]\n\n");
        sb.append("⏱️  Respuesta automática en menos de 60 segundos.\n");

        return sb.toString();
    }

    /**
     * Agrega comandos disponibles para ADMIN
     */
    private void agregarComandosAdmin(StringBuilder sb) {
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📊 GESTIÓN ADMINISTRATIVA (Solo Admin)\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("👥 USUARIOS:\n");
        sb.append("  • INSUSU[\"ci\",\"nombre\",\"apellido\",\"rol\",\"telefono\",\"email\"]\n");
        sb.append(
                "    Ejemplo: INSUSU[\"12345678\",\"Juan\",\"Perez\",\"Conductor\",\"75551234\",\"juan@mail.com\"]\n");
        sb.append("    ⚠️  Tel: 5-15 dígitos, solo números\n");
        sb.append("  • UPDUSU[\"id\",\"nombre\",\"apellido\",\"tel\",\"email\"]\n");
        sb.append("  • DELUSU[\"id\"]\n\n");

        sb.append("🚗 VEHÍCULOS:\n");
        sb.append("  • INSVEH[params] - Registrar vehículo\n");
        sb.append("  • UPDVEH[params] - Actualizar vehículo\n");
        sb.append("  • DELVEH[\"id\"] - Eliminar vehículo\n\n");

        sb.append("🛣️  RUTAS:\n");
        sb.append("  • INSRUT[params] - Registrar ruta\n");
        sb.append("  • UPDRUT[params] - Actualizar ruta\n");
        sb.append("  • DELRUT[\"id\"] - Eliminar ruta\n\n");

        sb.append("🚌 VIAJES:\n");
        sb.append("  • INSVIA[params] - Programar viaje\n");
        sb.append("  • UPDVIA[params] - Actualizar viaje\n");
        sb.append("  • DELVIA[\"id\"] - Eliminar viaje\n\n");

        agregarComandosSecretaria(sb);
        agregarComandosConsulta(sb);
    }

    /**
     * Agrega comandos disponibles para SECRETARIA
     */
    private void agregarComandosSecretaria(StringBuilder sb) {
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("� CONSULTA DE USUARIOS (Secretaria/Admin)\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("  • LISUSU o LISUSU[\"rol\"] - Listar usuarios\n");
        sb.append("  • GETUSU[\"id\"] - Obtener usuario por ID o CI\n\n");

        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("�💰 OPERACIONES DE VENTA\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("🎫 BOLETOS:\n");
        sb.append("  • INSBOL[\"asiento\",\"viaje_id\",\"cliente_id\",\"metodo_pago\"]\n");
        sb.append("  • LISBOL o LISBOL[\"viaje_id\"]\n");
        sb.append("  • GETBOL[\"id\"]\n\n");

        sb.append("📦 ENCOMIENDAS:\n");
        sb.append("  • INSENC[\"viaje_id\",\"cliente_id\",\"peso\",");
        sb.append("\"destinatario\",\"precio\",\"modalidad\",\"metodo\",(\"monto_origen\")]\n");
        sb.append(
                "    Modalidad ORIGEN (paga todo): INSENC[\"1\",\"2\",\"5.5\",\"Maria\",\"25.00\",\"origen\",\"efectivo\"]\n");
        sb.append(
                "    Modalidad MIXTO (paga parcial): INSENC[\"1\",\"2\",\"5.5\",\"Maria\",\"25.00\",\"mixto\",\"efectivo\",\"15.00\"]\n");
        sb.append(
                "    Modalidad DESTINO (paga después): INSENC[\"1\",\"2\",\"5.5\",\"Maria\",\"25.00\",\"destino\",\"efectivo\"]\n");
        sb.append("  • LISENC\n");
        sb.append("  • GETENC[\"venta_id\"]\n\n");

        sb.append("💵 VENTAS Y PAGOS:\n");
        sb.append("  • LISVEN - Listar ventas\n");
        sb.append("  • GETVEN[\"id\"] - Obtener venta\n");
        sb.append("  • INSPAG[params] - Registrar pago\n");
        sb.append("    Para completar pago pendiente (mixto/destino):\n");
        sb.append("    INSPAG[\"venta_id\",\"monto\",\"metodo\"] → cambia a Pagado automáticamente\n");
        sb.append("  • LISPAG - Listar pagos\n");
        sb.append("  • GETPAG[\"id\"] - Obtener pago\n\n");
        sb.append("  • LISPAG - Listar pagos\n");
        sb.append("  • GETPAG[\"id\"] - Obtener pago\n\n");

        agregarComandosConsulta(sb);
    }

    /**
     * Agrega comandos de consulta (disponibles para CLIENTE)
     */
    private void agregarComandosCliente(StringBuilder sb) {
        agregarComandosConsulta(sb);
        sb.append("\n⚠️  NOTA: Como Cliente, solo puedes realizar consultas.\n");
        sb.append("   No tienes permisos para crear, modificar o eliminar información.\n");
    }

    /**
     * Agrega comandos de consulta disponibles para todos
     */
    private void agregarComandosConsulta(StringBuilder sb) {
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("🔍 COMANDOS DE CONSULTA PÚBLICA (Todos los roles)\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("  • LISVEH - Listar vehículos\n");
        sb.append("  • GETVEH[\"id\"] - Obtener vehículo\n");
        sb.append("  • LISRUT - Listar rutas\n");
        sb.append("  • GETRUT[\"id\"] - Obtener ruta\n");
        sb.append("  • LISVIA - Listar viajes\n");
        sb.append("  • GETVIA[\"id\"] - Obtener viaje\n");
    }

    /**
     * Agrega TODOS los comandos disponibles (para modo público sin autenticación)
     */
    private void agregarComandosAnonimo(StringBuilder sb) {
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📋 TODOS LOS COMANDOS DISPONIBLES (Modo Público)\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        sb.append("👥 USUARIOS:\n");
        sb.append("  • INSUSU[\"ci\",\"nombre\",\"apellido\",\"rol\",\"telefono\",\"email\"]\n");
        sb.append(
                "    Ejemplo: INSUSU[\"12345678\",\"Juan\",\"Perez\",\"Conductor\",\"75551234\",\"juan@mail.com\"]\n");
        sb.append("    ⚠️  Tel: 8 dígitos, empieza con 6 o 7, sin guiones\n");
        sb.append("  • LISUSU o LISUSU[\"rol\"] - Listar usuarios\n");
        sb.append("  • GETUSU[\"id\"] - Obtener usuario\n");
        sb.append("  • UPDUSU[\"id\",\"nombre\",\"apellido\",\"tel\",\"email\"]\n");
        sb.append("  • DELUSU[\"id\"] - Eliminar usuario\n\n");

        sb.append("🚗 VEHÍCULOS:\n");
        sb.append(
                "  • INSVEH[\"placa\",\"marca\",\"modelo\",\"año\",\"color\",\"tipo\",\"estado\",\"conductor_id\"]\n");
        sb.append(
                "    Ejemplo: INSVEH[\"ABC1234\",\"Toyota\",\"Coaster\",\"2020\",\"Blanco\",\"Bus\",\"activo\",\"1\"]\n");
        sb.append("  • LISVEH - Listar vehículos\n");
        sb.append("  • GETVEH[\"id\"] - Obtener vehículo\n");
        sb.append(
                "  • UPDVEH[\"id\",\"placa\",\"marca\",\"modelo\",\"año\",\"color\",\"tipo\",\"estado\",\"conductor_id\"]\n");
        sb.append("  • DELVEH[\"id\"] - Eliminar vehículo\n\n");

        sb.append("🛣️  RUTAS:\n");
        sb.append("  • INSRUT[\"origen\",\"destino\"] o INSRUT[\"origen\",\"destino\",\"nombre\"]\n");
        sb.append("    Ejemplo: INSRUT[\"Santa Cruz\",\"Cochabamba\",\"Ruta Troncal\"]\n");
        sb.append("  • LISRUT - Listar rutas\n");
        sb.append("  • GETRUT[\"id\"] - Obtener ruta\n");
        sb.append("  • UPDRUT[\"id\",\"origen\",\"destino\",\"nombre\"]\n");
        sb.append("  • DELRUT[\"id\"] - Eliminar ruta\n\n");

        sb.append("🚌 VIAJES:\n");
        sb.append("  • INSVIA[\"ruta_id\",\"vehiculo_id\",\"fecha_salida\",\"precio\",\"asientos\"]\n");
        sb.append("    Ejemplo: INSVIA[\"1\",\"1\",\"2025-12-20 08:30\",\"150.50\",\"45\"]\n");
        sb.append("    Formato fecha: yyyy-MM-dd HH:mm\n");
        sb.append("  • LISVIA - Listar viajes\n");
        sb.append("  • GETVIA[\"id\"] - Obtener viaje\n");
        sb.append("  • UPDVIA[\"id\",\"ruta_id\",\"vehiculo_id\",\"fecha\",\"precio\",\"asientos\"]\n");
        sb.append("  • DELVIA[\"id\"] - Cancelar viaje\n\n");

        sb.append("🎫 BOLETOS:\n");
        sb.append("  • INSBOL[\"num_asiento\",\"viaje_id\",\"cliente_id\",\"metodo_pago\"]\n");
        sb.append("    Ejemplo: INSBOL[\"15\",\"1\",\"2\",\"efectivo\"]\n");
        sb.append("  • LISBOL o LISBOL[\"viaje_id\"] - Listar boletos\n");
        sb.append("  • GETBOL[\"id\"] - Obtener boleto\n\n");

        sb.append("📦 ENCOMIENDAS:\n");
        sb.append(
                "  • INSENC[\"viaje_id\",\"cliente_id\",\"peso\",\"destinatario\",\"precio\",\"modalidad\",\"metodo\",(\"monto_origen\")]\n");
        sb.append("    Modalidad ORIGEN: INSENC[\"1\",\"2\",\"5.5\",\"Maria\",\"25.00\",\"origen\",\"efectivo\"]\n");
        sb.append(
                "    Modalidad MIXTO: INSENC[\"1\",\"2\",\"5.5\",\"Maria\",\"25.00\",\"mixto\",\"efectivo\",\"15.00\"]\n");
        sb.append("    Modalidad DESTINO: INSENC[\"1\",\"2\",\"5.5\",\"Maria\",\"25.00\",\"destino\",\"efectivo\"]\n");
        sb.append("  • LISENC - Listar encomiendas\n");
        sb.append("  • GETENC[\"id\"] - Obtener encomienda\n\n");

        sb.append("💰 VENTAS Y PAGOS:\n");
        sb.append("  • LISVEN - Listar ventas\n");
        sb.append("  • GETVEN[\"id\"] - Obtener venta\n");
        sb.append("  • INSPAG[\"venta_id\",\"monto\",\"metodo_pago\"] - Registrar pago\n");
        sb.append("    Ejemplo: INSPAG[\"1\",\"150.50\",\"transferencia\"]\n");
        sb.append("    Para completar pago: cambia estado Pendiente → Pagado automáticamente\n");
        sb.append("  • LISPAG - Listar pagos\n");
        sb.append("  • GETPAG[\"id\"] - Obtener pago\n\n");

        sb.append("❓ AYUDA:\n");
        sb.append("  • HELP - Muestra esta ayuda\n");
    }
}
