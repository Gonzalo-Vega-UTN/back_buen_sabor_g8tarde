package com.example.buensaborback.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","pedidos"})
public class Cliente extends Base{

    private String nombre;
    private String apellido;
    private String telefono;
    private LocalDate fechaNacimiento;

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer","cliente","empleado"})
    private Usuario usuario;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name= "cliente_id")
    @Builder.Default
    @ToString.Exclude
    protected Set<Imagen> imagenes = new HashSet<Imagen>();


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cliente")
    @Builder.Default
    @ToString.Exclude
    private Set<Pedido> pedidos = new HashSet<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Cliente_domicilio",
            joinColumns = @JoinColumn(name = "Cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "domicilio_id"))
    @Builder.Default
    @ToString.Exclude
    private Set<Domicilio> domicilios = new HashSet<>();



}
