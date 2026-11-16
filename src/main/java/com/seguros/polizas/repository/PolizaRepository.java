package com.seguros.polizas.repository;

import com.seguros.polizas.entity.EstadoPoliza;
import com.seguros.polizas.entity.Poliza;
import com.seguros.polizas.entity.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    @Query("SELECT p FROM Poliza p WHERE (:tipo IS NULL OR p.tipo = :tipo) AND (:estado IS NULL OR p.estado = :estado)")
    List<Poliza> findByFiltros(@Param("tipo") TipoPoliza tipo, @Param("estado") EstadoPoliza estado);

    Optional<Poliza> findByNumeroPoliza(String numeroPoliza);
}
