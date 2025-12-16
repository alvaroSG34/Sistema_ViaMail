package com.grupo04sa.sistema_via_mail.repository;

import com.grupo04sa.sistema_via_mail.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    @Query("SELECT r FROM Ruta r WHERE r.origen = :origen AND r.destino = :destino")
    Optional<Ruta> findByOrigenAndDestino(@Param("origen") String origen, @Param("destino") String destino);

    List<Ruta> findByOrigen(String origen);

    List<Ruta> findByDestino(String destino);

    @Query("SELECT r FROM Ruta r WHERE r.nombre LIKE %:keyword% OR r.origen LIKE %:keyword% OR r.destino LIKE %:keyword%")
    List<Ruta> searchByKeyword(@Param("keyword") String keyword);
}
