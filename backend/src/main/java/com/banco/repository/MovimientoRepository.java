package com.banco.repository;

import com.banco.model.Movimiento;
import com.banco.model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaIdOrderByFechaDesc(Long cuentaId);

    Optional<Movimiento> findFirstByCuentaIdOrderByFechaDescIdDesc(Long cuentaId);

    /**
     * Total retirado en el día por cuenta. Los débitos se guardan con signo
     * negativo, por eso se suma el valor absoluto para comparar contra el
     * límite diario de retiro.
     */
    @Query("select coalesce(sum(abs(m.valor)), 0) from Movimiento m " +
        "where m.cuenta.id = :cuentaId and m.tipoMovimiento = :tipo " +
        "and m.fecha between :desde and :hasta")
    BigDecimal totalPorTipoYRango(@Param("cuentaId") Long cuentaId,
                                  @Param("tipo") TipoMovimiento tipo,
                                  @Param("desde") LocalDateTime desde,
                                  @Param("hasta") LocalDateTime hasta);

    @Query("select m from Movimiento m " +
        "where m.cuenta.cliente.id = :clienteId " +
        "and m.fecha between :desde and :hasta " +
        "order by m.fecha asc")
    List<Movimiento> findByClienteYRango(@Param("clienteId") Long clienteId,
                                         @Param("desde") LocalDateTime desde,
                                         @Param("hasta") LocalDateTime hasta);
}
