package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@SuperBuilder
@JsonIgnoreProperties({"hibernateLazyInitializer","domicilios"})
public class Localidad extends Base{

    private String nombre;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "id_provincia")
    private Provincia provincia;


    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "localidad")
    @Builder.Default
    @ToString.Exclude
    private Set<Domicilio> domicilios = new HashSet<>();


}
