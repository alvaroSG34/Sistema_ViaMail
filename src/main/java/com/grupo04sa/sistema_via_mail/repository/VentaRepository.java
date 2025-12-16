package com.grupo04sa.sistema_via_mail.repository;

import com.grupo04sa.sistema_via_mail.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByUsuarioId(Long usuarioId);

    List<Venta> findByVehiculoId(Long vehiculoId);

    List<Venta> findByTipo(String tipo);

    List<Venta> findByEstadoPago(String estadoPago);

    @Query("SELECT v FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta")
    List<Venta> findByFechaBetween(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT v FROM Venta v WHERE DATE(v.fecha) = DATE(:fecha)")
    List<Venta> findByFecha(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT v FROM Venta v JOIN FETCH v.usuario JOIN FETCH v.vehiculo WHERE v.id = :id")
    Optional<Venta> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.boletos LEFT JOIN FETCH v.encomienda LEFT JOIN FETCH v.pagos WHERE v.id = :id")
    Optional<Venta> findByIdWithAll(@Param("id") Long id);

    @Query("SELECT SUM(v.montoTotal) FROM Venta v WHERE v.fecha BETWEEN :desde AND :hasta AND v.estadoPago = 'Pagado'")
    Double getTotalVentasByPeriodo(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COUNT(v) FROM Venta v WHERE DATE(v.fecha) = DATE(:fecha)")
    Long countByFecha(@Param("fecha") LocalDateTime fecha);
}
