package com.grupo04sa.sistema_via_mail.repository;

import com.grupo04sa.sistema_via_mail.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

    List<EmailLog> findByEmailRemitente(String emailRemitente);

    List<EmailLog> findByComando(String comando);

    List<EmailLog> findByEstado(String estado);

    @Query("SELECT e FROM EmailLog e WHERE e.createdAt BETWEEN :desde AND :hasta")
    List<EmailLog> findByFechaBetween(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT e FROM EmailLog e WHERE e.emailRemitente = :email AND e.comando = :comando")
    List<EmailLog> findByEmailAndComando(@Param("email") String email, @Param("comando") String comando);

    @Query("SELECT COUNT(e) FROM EmailLog e WHERE e.estado = 'EXITOSO'")
    Long countExitosos();

    @Query("SELECT COUNT(e) FROM EmailLog e WHERE e.estado = 'ERROR'")
    Long countErrores();
}
