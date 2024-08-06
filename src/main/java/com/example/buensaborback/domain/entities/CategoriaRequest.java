package com.example.buensaborback.domain.entities;


import com.example.buensaborback.domain.entities.Categoria;

import java.util.List;

public class CategoriaRequest {
    private Categoria categoria;
    private List<Long> sucursalesIds;

    // Getters and setters
    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Long> getSucursalesIds() {
        return sucursalesIds;
    }

    public void setSucursalesIds(List<Long> sucursalesIds) {
        this.sucursalesIds = sucursalesIds;
    }
}