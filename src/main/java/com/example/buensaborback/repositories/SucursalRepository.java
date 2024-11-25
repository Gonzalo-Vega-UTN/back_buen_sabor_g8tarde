package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.Empresa;
import com.example.buensaborback.domain.entities.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long>{
    List<Sucursal> findByEmpresaId(Long empresaId);

    boolean existsByNombreIgnoreCaseAndEmpresa(String nombre, Empresa empresa);
}