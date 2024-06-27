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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@SuperBuilder
@Audited
@JsonIgnoreProperties({"hibernateLazyInitializer","domicilios"})
public class Localidad extends Base{

    private String nombre;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_provincia")
    private Provincia provincia;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "localidad", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<Domicilio> domicilios = new HashSet<>();


}
