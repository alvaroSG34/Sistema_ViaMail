package com.grupo04sa.sistema_via_mail.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grupo04sa.sistema_via_mail.model.Boleto;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {

    List<Boleto> findByVentaId(Long ventaId);

    List<Boleto> findByViajeId(Long viajeId);

    @Query("SELECT b FROM Boleto b WHERE b.viaje.id = :viajeId AND b.asiento = :asiento")
    Optional<Boleto> findByViajeIdAndAsiento(@Param("viajeId") Long viajeId, @Param("asiento") String asiento);

    @Query("SELECT b.asiento FROM Boleto b WHERE b.viaje.id = :viajeId")
    List<String> findAsientosOcupadosByViaje(@Param("viajeId") Long viajeId);

    @Query("SELECT COUNT(b) FROM Boleto b WHERE b.viaje.id = :viajeId")
    Long countByViajeId(@Param("viajeId") Long viajeId);

    boolean existsByViajeIdAndAsiento(Long viajeId, String asiento);

    @Query("SELECT b FROM Boleto b " +
            "JOIN FETCH b.venta v " +
            "JOIN FETCH v.usuario " +
            "JOIN FETCH b.ruta " +
            "JOIN FETCH b.viaje " +
            "WHERE b.id = :id")
    Optional<Boleto> findByIdWithDetails(@Param("id") Long id);
}
