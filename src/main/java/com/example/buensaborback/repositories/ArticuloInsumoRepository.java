package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.ArticuloInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ArticuloInsumoRepository extends JpaRepository<ArticuloInsumo, Long>, BusquedaRepository<ArticuloInsumo> {

    @Query("""
                select a from ArticuloInsumo a
                inner join a.categoria sucCategoria
                inner join sucCategoria.sucursales sucursales
                left join sucCategoria.subCategorias subCategorias
                where sucursales.id = ?1 and a.esParaElaborar = false and (a.categoria.id = ?2 or subCategorias.id in ?3)
            """)
    List<ArticuloInsumo> findBySucursalCategoriaAndSubCategoriasAndEsParaElaborar(Long idSucursal, Long idCategoria, List<Long> subCategoriasIds);

    Set<ArticuloInsumo> findByAltaTrueAndCategoria_IdAndSucursal_IdAndEsParaElaborarFalse(Long id, Long id1);
}
