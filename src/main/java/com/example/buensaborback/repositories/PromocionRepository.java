package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.Promocion;
import com.example.buensaborback.domain.entities.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    boolean existsByDenominacion(String denominacion);

    Promocion findByDenominacionIgnoreCase(String denominacion);

    boolean existsByDenominacionIgnoreCase(String denominacion);

    List<Promocion> findBySucursales(Sucursal sucursales);
}
