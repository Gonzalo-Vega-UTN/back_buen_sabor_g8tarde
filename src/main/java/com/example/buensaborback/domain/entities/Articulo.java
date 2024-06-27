package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
@JsonIgnoreProperties({"hibernateLazyInitializer","promocionDetalle"})
public class Articulo extends Base {

    protected String denominacion;
    protected Double precioVenta;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name= "articulo_id")
    @Builder.Default
    protected Set<Imagen> imagenes = new HashSet<Imagen>();

    @ManyToOne(cascade = CascadeType.ALL)

    @JsonIgnoreProperties({"hibernateLazyInitializer","imagenes","promocionDetalle"})
    protected UnidadMedida unidadMedida;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "categoria_id")

    @JsonIgnoreProperties({"hibernateLazyInitializer","sucursales","articulos"})
    protected Categoria categoria;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "articulo")
    @Builder.Default
    protected Set<PromocionDetalle> promocionDetalle = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    @JsonIgnoreProperties("articulos")
    private Sucursal sucursal;

}
