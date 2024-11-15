package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString(exclude = {"categoriaPadre", "subCategorias"})
@SuperBuilder

@JsonIgnoreProperties({"hibernateLazyInitializer","articulos"})
public class Categoria extends Base {

    private String denominacion;

    @ManyToMany(mappedBy = "categorias", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @Builder.Default
    @JsonIgnoreProperties({"hibernateLazyInitializer", "domicilio","empresa", "imagenes","articulos","categorias"})
    private Set<Sucursal> sucursales = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "categoria", fetch = FetchType.LAZY)
    @Builder.Default

    private Set<Articulo> articulos = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "categoria_padre_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "sucursales", "subCategorias"})
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnoreProperties({"hibernateLazyInitializer","categoriaPadre"})
    private Set<Categoria> subCategorias = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name= "categoria_id")
    @Builder.Default
    protected Set<Imagen> imagenes = new HashSet<Imagen>();


}