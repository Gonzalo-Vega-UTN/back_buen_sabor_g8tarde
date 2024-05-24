package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.ArticuloInsumo;
import com.example.buensaborback.domain.entities.Categoria;
import com.example.buensaborback.domain.entities.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloInsumoRepository extends BaseRepository<ArticuloInsumo,Long> {

    List<ArticuloInsumo> findByCategoria(Categoria categoria);
    List<ArticuloInsumo> findByUnidadMedida(UnidadMedida unidadMedida);
    List<ArticuloInsumo> findByCategoriaAndUnidadMedida(Categoria categoria,UnidadMedida unidadMedida);
}
