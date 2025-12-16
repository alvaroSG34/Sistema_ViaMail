package com.grupo04sa.sistema_via_mail.repository;

import com.grupo04sa.sistema_via_mail.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Long> {

    List<Viaje> findByEstado(String estado);

    List<Viaje> findByRutaId(Long rutaId);

    List<Viaje> findByVehiculoId(Long vehiculoId);

    @Query("SELECT v FROM Viaje v WHERE v.estado = 'programado' AND v.fechaSalida > :now")
    List<Viaje> findDisponiblesParaVenta(@Param("now") LocalDateTime now);

    @Query("SELECT v FROM Viaje v WHERE v.ruta.id = :rutaId AND v.estado = 'programado' AND v.fechaSalida > :now")
    List<Viaje> findDisponiblesByRuta(@Param("rutaId") Long rutaId, @Param("now") LocalDateTime now);

    @Query("SELECT v FROM Viaje v WHERE v.fechaSalida BETWEEN :desde AND :hasta")
    List<Viaje> findByFechaSalidaBetween(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COUNT(b) FROM Boleto b WHERE b.viaje.id = :viajeId")
    Long countBoletosVendidos(@Param("viajeId") Long viajeId);

    @Query("SELECT v FROM Viaje v JOIN FETCH v.ruta JOIN FETCH v.vehiculo WHERE v.id = :id")
    Viaje findByIdWithDetails(@Param("id") Long id);
}
