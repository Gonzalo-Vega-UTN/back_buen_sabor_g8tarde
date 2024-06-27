package com.example.buensaborback.domain.entities;

import com.example.buensaborback.domain.entities.enums.Estado;
import com.example.buensaborback.domain.entities.enums.FormaPago;
import com.example.buensaborback.domain.entities.enums.TipoEnvio;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
@Audited
@JsonIgnoreProperties({"hibernateLazyInitializer", "empleado","factura"})
public class Pedido extends Base{

    private LocalTime horaEstimadaFinalizacion;
    private Double total;
    private Double totalCosto;
    private Estado estado;
    private TipoEnvio tipoEnvio;
    private FormaPago formaPago;
    private LocalDate fechaPedido;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "empleado_id")

    private Empleado empleado;

    @ManyToOne(cascade = CascadeType.ALL) //Dirección a donde se envía el pedido
    @JsonIgnoreProperties({"hibernateLazyInitializer", "localidad","clientes"})
    private Domicilio domicilio;

    @ManyToOne(cascade = CascadeType.ALL)

    private Sucursal sucursal;

    @OneToOne(cascade = CascadeType.ALL)
    private Factura factura;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido")
    @Builder.Default
    private Set<DetallePedido> detallePedidos = new HashSet<>();


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;


}
