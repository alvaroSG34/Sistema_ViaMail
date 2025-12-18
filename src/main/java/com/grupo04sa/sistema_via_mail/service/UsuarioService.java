package com.grupo04sa.sistema_via_mail.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo04sa.sistema_via_mail.exception.EntityNotFoundException;
import com.grupo04sa.sistema_via_mail.exception.ValidationException;
import com.grupo04sa.sistema_via_mail.model.Usuario;
import com.grupo04sa.sistema_via_mail.repository.UsuarioRepository;
import com.grupo04sa.sistema_via_mail.util.CommandValidator;

/**
 * Servicio de lógica de negocio para Usuarios
 */
@Service
public class UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final CommandValidator validator;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, CommandValidator validator) {
        this.usuarioRepository = usuarioRepository;
        this.validator = validator;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Crear nuevo usuario
     */
    @Transactional
    public Usuario crear(String ci, String nombre, String apellido, String rol,
            String telefono, String correo) {
        log.debug("Creando usuario con CI: {}", ci);

        // Validaciones
        if (!validator.isValidCI(ci)) {
            throw new ValidationException("ci", "CI inválido. Debe contener entre 5 y 15 dígitos");
        }

        if (!validator.isNotEmpty(nombre)) {
            throw new ValidationException("nombre", "El nombre es requerido");
        }

        if (!validator.isNotEmpty(apellido)) {
            throw new ValidationException("apellido", "El apellido es requerido");
        }

        if (!validator.isValidRol(rol)) {
            throw new ValidationException("rol",
                    "Rol inválido. Valores permitidos: Admin, Secretaria, Conductor, Cliente");
        }

        if (telefono != null && !telefono.isEmpty() && !validator.isValidTelefono(telefono)) {
            throw new ValidationException("telefono", "Teléfono inválido. Debe contener entre 5 y 15 dígitos");
        }

        if (correo != null && !correo.isEmpty() && !validator.isValidEmail(correo)) {
            throw new ValidationException("correo", "Email inválido");
        }

        // Verificar que no exista CI duplicado
        if (usuarioRepository.existsByCi(ci)) {
            throw new ValidationException("ci", "Ya existe un usuario con el CI: " + ci);
        }

        // Verificar que no exista correo duplicado
        if (correo != null && !correo.isEmpty() && usuarioRepository.existsByCorreo(correo)) {
            throw new ValidationException("correo", "Ya existe un usuario con el email: " + correo);
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setCi(ci);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setRol(rol);
        usuario.setTelefono(telefono);
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(ci)); // Password por defecto = CI
        usuario.setTemaPreferido("claro"); // Valor por defecto requerido por BD
        usuario.setModoContraste("normal"); // Valor por defecto
        usuario.setTamanoFuente("medio"); // Valor por defecto

        usuario = usuarioRepository.save(usuario);

        log.info("Usuario creado exitosamente: {} (ID: {})", usuario.getNombreCompleto(), usuario.getId());

        return usuario;
    }

    /**
     * Listar todos los usuarios activos
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllActive();
    }

    /**
     * Listar usuarios por rol
     */
    public List<Usuario> listarPorRol(String rol) {
        if (!validator.isValidRol(rol)) {
            throw new ValidationException("rol", "Rol inválido");
        }
        return usuarioRepository.findByRolAndActive(rol);
    }

    /**
     * Obtener usuario por ID
     */
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario", id));
    }

    /**
     * Obtener usuario por CI
     */
    public Usuario obtenerPorCI(String ci) {
        return usuarioRepository.findByCiAndActive(ci)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con CI " + ci + " no encontrado"));
    }

    /**
     * Actualizar usuario
     */
    @Transactional
    public Usuario actualizar(Long id, String nombre, String apellido, String telefono, String correo) {
        Usuario usuario = obtenerPorId(id);

        if (nombre != null && !nombre.isEmpty()) {
            usuario.setNombre(nombre);
        }

        if (apellido != null && !apellido.isEmpty()) {
            usuario.setApellido(apellido);
        }

        if (telefono != null && !telefono.isEmpty()) {
            if (!validator.isValidTelefono(telefono)) {
                throw new ValidationException("telefono", "Teléfono inválido");
            }
            usuario.setTelefono(telefono);
        }

        if (correo != null && !correo.isEmpty()) {
            if (!validator.isValidEmail(correo)) {
                throw new ValidationException("correo", "Email inválido");
            }
            // Verificar que no exista otro usuario con ese correo
            if (usuarioRepository.existsByCorreo(correo)) {
                Usuario existente = usuarioRepository.findByCorreo(correo).orElse(null);
                if (existente != null && !existente.getId().equals(id)) {
                    throw new ValidationException("correo", "Ya existe otro usuario con ese email");
                }
            }
            usuario.setCorreo(correo);
        }

        usuario = usuarioRepository.save(usuario);

        log.info("Usuario actualizado: {} (ID: {})", usuario.getNombreCompleto(), usuario.getId());

        return usuario;
    }

    /**
     * Eliminar usuario (soft delete)
     */
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setDeletedAt(LocalDateTime.now());
        usuarioRepository.save(usuario);

        log.info("Usuario eliminado (soft delete): {} (ID: {})", usuario.getNombreCompleto(), usuario.getId());
    }
}
