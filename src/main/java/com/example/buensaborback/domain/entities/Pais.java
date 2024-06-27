package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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
@ToString(callSuper = true)
@SuperBuilder
@Audited
@JsonIgnoreProperties({"hibernateLazyInitializer", "provincias"})
public class Pais extends Base{

    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pais", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<Provincia> provincias = new HashSet<>();

}
