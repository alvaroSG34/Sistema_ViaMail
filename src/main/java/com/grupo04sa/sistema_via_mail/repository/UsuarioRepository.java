package com.grupo04sa.sistema_via_mail.repository;

import com.grupo04sa.sistema_via_mail.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCi(String ci);

    Optional<Usuario> findByCorreo(String correo);

    List<Usuario> findByRol(String rol);

    @Query("SELECT u FROM Usuario u WHERE u.deletedAt IS NULL")
    List<Usuario> findAllActive();

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.deletedAt IS NULL")
    List<Usuario> findByRolAndActive(@Param("rol") String rol);

    @Query("SELECT u FROM Usuario u WHERE u.correo = :correo AND u.deletedAt IS NULL")
    Optional<Usuario> findByCorreoAndActive(@Param("correo") String correo);

    @Query("SELECT u FROM Usuario u WHERE u.ci = :ci AND u.deletedAt IS NULL")
    Optional<Usuario> findByCiAndActive(@Param("ci") String ci);

    boolean existsByCi(String ci);

    boolean existsByCorreo(String correo);
}
