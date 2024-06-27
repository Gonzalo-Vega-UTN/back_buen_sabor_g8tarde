package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@SuperBuilder
@Audited
@JsonIgnoreProperties({"hibernateLazyInitializer","pedido","promociones"})
public class DetallePedido extends Base {

    private Integer cantidad;
    private Double subTotal;

    @ManyToOne(cascade = CascadeType.ALL)
    private Articulo articulo;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "detallePedido", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Promocion>promociones = new HashSet<>();

}
