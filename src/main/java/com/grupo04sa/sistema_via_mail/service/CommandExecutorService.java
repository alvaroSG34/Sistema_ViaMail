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
import com.grupo04sa.sistema_via_mail.model.Usuario;
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
    private final CommandParserService parserService;
    private final CommandValidatorService validatorService;
    private final ResponseFormatter formatter;

    public CommandExecutorService(UsuarioService usuarioService, BoletoService boletoService,
            EncomiendaService encomiendaService, CommandParserService parserService,
            CommandValidatorService validatorService, ResponseFormatter formatter) {
        this.usuarioService = usuarioService;
        this.boletoService = boletoService;
        this.encomiendaService = encomiendaService;
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

                // Comandos de Boletos
                case "INSBOL" -> ejecutarINSBOL(request);
                case "LISBOL" -> ejecutarLISBOL(request);
                case "GETBOL" -> ejecutarGETBOL(request);

                // Comandos de Encomiendas
                case "INSENC" -> ejecutarINSENC(request);
                case "LISENC" -> ejecutarLISENC(request);
                case "GETENC" -> ejecutarGETENC(request);

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

        return formatter.formatBoleto(boleto);
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
        parserService.validarNumeroParametros(request, 8);
        List<String> params = request.getParametros();

        Encomienda encomienda = encomiendaService.registrarEncomienda(
                Long.parseLong(params.get(0)), // Viaje ID
                Long.parseLong(params.get(1)), // Ruta ID
                Long.parseLong(params.get(2)), // Cliente ID
                new BigDecimal(params.get(3)), // Peso
                params.get(4), // Destinatario
                new BigDecimal(params.get(5)), // Precio
                params.get(6), // Modalidad pago
                params.get(7) // Método pago
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

    /**
     * Ejecuta el comando HELP - Muestra lista de comandos disponibles según el rol
     */
    private String ejecutarHELP(CommandRequest request, Usuario usuario) {
        StringBuilder sb = new StringBuilder();
        String rol = usuario.getRol();

        sb.append("=== SISTEMA TRANS COMARAPA - COMANDOS DISPONIBLES ===\n\n");
        sb.append("Usuario: ").append(usuario.getNombreCompleto()).append("\n");
        sb.append("Rol: ").append(rol).append("\n\n");

        // Comandos de lectura (todos los roles)
        sb.append("--- COMANDOS DE CONSULTA ---\n");
        sb.append("LISUSU[\"filtro\"] - Listar usuarios (* para todos)\n");
        sb.append("GETUSU[\"id\"] - Obtener usuario por ID\n");
        sb.append("LISVEH[\"filtro\"] - Listar vehículos\n");
        sb.append("GETVEH[\"id\"] - Obtener vehículo por ID\n");
        sb.append("LISRUT[\"filtro\"] - Listar rutas\n");
        sb.append("GETRUT[\"id\"] - Obtener ruta por ID\n");
        sb.append("LISVIA[\"filtro\"] - Listar viajes\n");
        sb.append("GETVIA[\"id\"] - Obtener viaje por ID\n\n");

        // Comandos de secretaria
        if (usuario.isSecretariaOrAdmin()) {
            sb.append("--- COMANDOS DE GESTIÓN (SECRETARIA) ---\n");
            sb.append("INSBOL[params] - Insertar boleto\n");
            sb.append("LISBOL[\"viaje_id\"] - Listar boletos de un viaje\n");
            sb.append("GETBOL[\"id\"] - Obtener boleto por ID\n");
            sb.append("INSENC[params] - Insertar encomienda\n");
            sb.append("LISENC[\"filtro\"] - Listar encomiendas\n");
            sb.append("GETENC[\"venta_id\"] - Obtener encomienda\n");
            sb.append("LISVEN[\"filtro\"] - Listar ventas\n");
            sb.append("GETVEN[\"id\"] - Obtener venta\n");
            sb.append("INSPAG[params] - Insertar pago\n");
            sb.append("LISPAG[\"filtro\"] - Listar pagos\n\n");
        }

        // Comandos de admin
        if (usuario.isAdmin()) {
            sb.append("--- COMANDOS DE ADMINISTRACIÓN (ADMIN) ---\n");
            sb.append("INSUSU[params] - Insertar usuario\n");
            sb.append("UPDUSU[params] - Actualizar usuario\n");
            sb.append("DELUSU[\"id\"] - Eliminar usuario\n");
            sb.append("INSVEH[params] - Insertar vehículo\n");
            sb.append("UPDVEH[params] - Actualizar vehículo\n");
            sb.append("DELVEH[\"id\"] - Eliminar vehículo\n");
            sb.append("INSRUT[params] - Insertar ruta\n");
            sb.append("UPDRUT[params] - Actualizar ruta\n");
            sb.append("DELRUT[\"id\"] - Eliminar ruta\n");
            sb.append("INSVIA[params] - Insertar viaje\n");
            sb.append("UPDVIA[params] - Actualizar viaje\n");
            sb.append("DELVIA[\"id\"] - Eliminar viaje\n\n");
        }

        sb.append("Para más información sobre un comando específico, ");
        sb.append("consulte la documentación del sistema.\n");

        return sb.toString();
    }
}
