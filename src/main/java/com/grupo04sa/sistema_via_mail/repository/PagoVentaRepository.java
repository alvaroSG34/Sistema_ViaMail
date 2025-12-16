package com.grupo04sa.sistema_via_mail.repository;

import com.grupo04sa.sistema_via_mail.model.PagoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoVentaRepository extends JpaRepository<PagoVenta, Long> {

    List<PagoVenta> findByVentaId(Long ventaId);

    Optional<PagoVenta> findByVentaIdAndNumCuota(Long ventaId, Integer numCuota);

    List<PagoVenta> findByEstadoPago(String estadoPago);

    @Query("SELECT p FROM PagoVenta p WHERE p.venta.id = :ventaId AND p.estadoPago = :estadoPago")
    List<PagoVenta> findByVentaIdAndEstadoPago(@Param("ventaId") Long ventaId, @Param("estadoPago") String estadoPago);

    @Query("SELECT SUM(p.monto) FROM PagoVenta p WHERE p.venta.id = :ventaId AND p.estadoPago = 'Pagado'")
    BigDecimal getTotalPagadoByVenta(@Param("ventaId") Long ventaId);

    @Query("SELECT p FROM PagoVenta p WHERE p.transactionId = :transactionId")
    Optional<PagoVenta> findByTransactionId(@Param("transactionId") String transactionId);

    @Query("SELECT p FROM PagoVenta p WHERE p.paymentMethodTransactionId = :paymentMethodTransactionId")
    Optional<PagoVenta> findByPaymentMethodTransactionId(@Param("paymentMethodTransactionId") String paymentMethodTransactionId);

    boolean existsByVentaIdAndNumCuota(Long ventaId, Integer numCuota);
}
