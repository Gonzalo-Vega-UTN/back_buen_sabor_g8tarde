package com.example.buensaborback.domain.entities;


import com.example.buensaborback.domain.entities.Categoria;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;

import java.util.List;
@Getter
@Data
public class CategoriaRequest {
    private Categoria categoria;
    private List<Long> sucursalesIds;

}