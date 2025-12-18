package com.grupo04sa.sistema_via_mail.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.grupo04sa.sistema_via_mail.dto.CommandRequest;
import com.grupo04sa.sistema_via_mail.model.Usuario;
import com.grupo04sa.sistema_via_mail.repository.UsuarioRepository;

/**
 * Servicio para validar permisos de ejecución de comandos según el rol del
 * usuario
 */
@Service
public class CommandValidatorService {
    private static final Logger log = LoggerFactory.getLogger(CommandValidatorService.class);

    private final UsuarioRepository usuarioRepository;

    @Value("${security.permit-all:false}")
    private boolean permitAll;

    // Comandos que solo pueden ejecutar Admin
    private static final List<String> COMANDOS_ADMIN = Arrays.asList(
            "INSUSU", "UPDUSU", "DELUSU", // Gestión de usuarios
            "INSVEH", "UPDVEH", "DELVEH", // Gestión de vehículos
            "INSRUT", "UPDRUT", "DELRUT", // Gestión de rutas
            "INSVIA", "UPDVIA", "DELVIA" // Gestión de viajes
    );

    // Comandos que pueden ejecutar Admin y Secretaria
    private static final List<String> COMANDOS_SECRETARIA = Arrays.asList(
            "LISUSU", "GETUSU", // Consultas de usuarios (sensible)
            "INSBOL", "LISBOL", "GETBOL", // Gestión de boletos
            "INSENC", "LISENC", "GETENC", // Gestión de encomiendas
            "LISVEN", "GETVEN", // Consultas de ventas
            "INSPAG", "LISPAG", "GETPAG" // Gestión de pagos
    );

    // Comandos de solo lectura (todos los roles autenticados)
    private static final List<String> COMANDOS_LECTURA = Arrays.asList(
            "HELP", // Comando de ayuda
            "LISVEH", "GETVEH", // Consultas de vehículos
            "LISRUT", "GETRUT", // Consultas de rutas
            "LISVIA", "GETVIA" // Consultas de viajes
    );

    public CommandValidatorService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Valida que el usuario tenga permisos para ejecutar el comando
     * NOTA: Ahora permite cualquier email sin verificar registro en BD
     * 
     * @param request Comando a validar
     * @return Usuario que ejecuta el comando (o usuario anónimo)
     */
    public Usuario validarPermisos(CommandRequest request) {
        log.debug("Validando permisos para comando: {} desde: {}",
                request.getComando(), request.getEmailRemitente());

        // Buscar usuario por email (opcional - si no existe, crear usuario anónimo)
        Usuario usuario = usuarioRepository.findByCorreoAndActive(request.getEmailRemitente())
                .orElseGet(() -> {
                    log.info("Email no registrado: {} - Creando usuario anónimo", request.getEmailRemitente());
                    // Crear usuario anónimo temporal (no se guarda en BD)
                    Usuario anonimo = new Usuario();
                    anonimo.setCorreo(request.getEmailRemitente());
                    anonimo.setNombre("Anónimo");
                    anonimo.setApellido("Usuario");
                    anonimo.setRol("anonimo"); // Rol especial para usuarios no registrados
                    return anonimo;
                });

        String comando = request.getComando();
        String rol = usuario.getRol();

        log.info("Usuario: {} ({}) ejecutando comando: {}",
                usuario.getNombreCompleto(), rol, comando);

        // Si PermitALL está activado, forzar rol anónimo para acceso total sin
        // restricciones
        if (permitAll) {
            log.warn("⚠️  MODO PERMITALL=TRUE - {} puede ejecutar cualquier comando sin restricciones",
                    usuario.getCorreo());
            // Forzar rol anónimo para que HELP muestre todos los comandos
            usuario.setRol("anonimo");
            return usuario;
        }

        // Si permitAll=false, validar que el usuario esté registrado en BD
        if ("anonimo".equals(rol)) {
            throw new IllegalStateException(
                    "Email no registrado: " + usuario.getCorreo() +
                            ". Debe estar registrado en el sistema para ejecutar comandos.");
        }

        // Validar permisos según el comando y rol
        if (COMANDOS_ADMIN.contains(comando)) {
            if (!usuario.isAdmin()) {
                throw new IllegalStateException(
                        "El comando " + comando + " solo puede ser ejecutado por administradores");
            }
        } else if (COMANDOS_SECRETARIA.contains(comando)) {
            if (!usuario.isSecretariaOrAdmin()) {
                throw new IllegalStateException(
                        "El comando " + comando + " solo puede ser ejecutado por administradores o secretarias");
            }
        } else if (COMANDOS_LECTURA.contains(comando)) {
            // Cualquier usuario autenticado puede ejecutar comandos de lectura
            log.debug("Comando de lectura permitido para: {}", rol);
        } else {
            // Comando no reconocido
            throw new IllegalStateException(
                    "Comando no reconocido o no autorizado: " + comando);
        }

        log.info("Permisos validados correctamente para {} ejecutando {}",
                usuario.getNombreCompleto(), comando);

        return usuario;
    }

    /**
     * Verifica si un comando requiere rol Admin
     */
    public boolean requiereAdmin(String comando) {
        return COMANDOS_ADMIN.contains(comando);
    }

    /**
     * Verifica si un comando requiere rol Secretaria o Admin
     */
    public boolean requiereSecretariaOrAdmin(String comando) {
        return COMANDOS_SECRETARIA.contains(comando);
    }

    /**
     * Verifica si un comando es de solo lectura
     */
    public boolean esComandoLectura(String comando) {
        return COMANDOS_LECTURA.contains(comando);
    }
}
