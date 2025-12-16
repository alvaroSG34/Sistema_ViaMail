package com.grupo04sa.sistema_via_mail.repository;

import com.grupo04sa.sistema_via_mail.model.Encomienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EncomiendaRepository extends JpaRepository<Encomienda, Long> {

    List<Encomienda> findByRutaId(Long rutaId);

    List<Encomienda> findByViajeId(Long viajeId);

    @Query("SELECT e FROM Encomienda e WHERE e.modalidadPago = :modalidad")
    List<Encomienda> findByModalidadPago(@Param("modalidad") String modalidad);

    @Query("SELECT e FROM Encomienda e JOIN FETCH e.venta WHERE e.ventaId = :ventaId")
    Optional<Encomienda> findByVentaIdWithDetails(@Param("ventaId") Long ventaId);

    @Query("SELECT e FROM Encomienda e JOIN e.venta v WHERE v.estadoPago = :estadoPago")
    List<Encomienda> findByEstadoPago(@Param("estadoPago") String estadoPago);
}
