package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository  extends JpaRepository<Categoria, Long>{
    List<Categoria> findByAltaTrue();
    @Query("SELECT DISTINCT c FROM Categoria c " +
            "LEFT JOIN c.sucursales s " +
            "WHERE c.categoriaPadre IS NULL AND  s.id = :sucursalId")
    List<Categoria>findAllBySucursal(@Param("sucursalId") Long sucursalId);

    boolean existsByDenominacionIgnoreCase(String denominacion);
    @Query("SELECT c FROM Categoria c WHERE c.categoriaPadre IS NULL")
    List<Categoria> findCategoriasPadres();

    @Query("SELECT c FROM Categoria c JOIN c.sucursales s WHERE s.id = :sucursalId")
    List<Categoria> findAllBySucu(@Param("sucursalId") Long sucursalId);

    Categoria findByDenominacionIgnoreCase(String denominacion);

}