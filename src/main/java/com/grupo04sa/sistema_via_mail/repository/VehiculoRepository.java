package com.grupo04sa.sistema_via_mail.repository;

import com.grupo04sa.sistema_via_mail.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByConductorId(Long conductorId);

    @Query("SELECT v FROM Vehiculo v WHERE v.estado = :estado")
    List<Vehiculo> findByEstado(@Param("estado") String estado);

    @Query("SELECT v FROM Vehiculo v JOIN FETCH v.conductor WHERE v.id = :id")
    Optional<Vehiculo> findByIdWithConductor(@Param("id") Long id);

    boolean existsByPlaca(String placa);
}
