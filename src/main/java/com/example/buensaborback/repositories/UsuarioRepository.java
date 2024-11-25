package com.example.buensaborback.repositories;

import com.example.buensaborback.domain.entities.Usuario;
import com.example.buensaborback.domain.entities.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByAuth0IdAndUsername(String clave, String username);
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByRol(Rol rol);
}