package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.ArticuloManufacturado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;


@Repository
public interface ArticuloManufacturadoRepository extends JpaRepository<ArticuloManufacturado, Long>, BusquedaRepository<ArticuloManufacturado> {
    @Query("""
                select a from ArticuloManufacturado a
                inner join a.categoria sucCategoria
                inner join sucCategoria.sucursales sucursales
                left join sucCategoria.subCategorias subCategorias
                where sucursales.id = ?1 and (a.categoria.id = ?2 or subCategorias.id in ?3)
            """)
    List<ArticuloManufacturado> findBySucursalCategoriaAndSubCategorias(Long idSucursal, Long idCategoria, Collection<Long> subCategoriasIds);

    Set<ArticuloManufacturado> findByAltaTrueAndCategoria_IdAndSucursal_Id(Long id, Long id1);

}
