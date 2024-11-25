package com.example.buensaborback;

import com.example.buensaborback.bussines.service.impl.LocalidadServiceImpl;
import com.example.buensaborback.client.GeoRefService;
import com.example.buensaborback.domain.entities.*;
import com.example.buensaborback.domain.entities.enums.*;
import com.example.buensaborback.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@SpringBootApplication
public class BuenSaborBackApplication {
    // Aca tiene que inyectar todos los repositorios
// Es por ello que deben crear el paquete reositorio
    private static final Logger logger = LoggerFactory.getLogger(BuenSaborBackApplication.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ArticuloManufacturadoDetalleRepository articuloManufacturadoDetalleRepository;

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private ProvinciaRepository provinciaRepository;

    @Autowired
    private LocalidadRepository localidadRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private DomicilioRepository domicilioRepository;

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ArticuloInsumoRepository articuloInsumoRepository;

    @Autowired
    private ArticuloManufacturadoRepository articuloManufacturadoRepository;

    @Autowired
    private ImagenRepository imagenRepository;

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FacturaRepository FacturaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private LocalidadServiceImpl localidadService;

    @Autowired
    GeoRefService geoRefService;

    public static void main(String[] args) {
        SpringApplication.run(BuenSaborBackApplication.class, args);
        logger.info("Estoy activo en el main");
    }

    @Bean
    @Order(1)
    CommandLineRunner init2() {
        return args -> {
            if (paisRepository.count() > 0) {
                logger.info("La base de datos ya contiene datos de países, se omite init2.");
                return;
            }
            System.out.println("Guardando Datos Domicilio");
            Pais argentina = Pais.builder()
                    .nombre("Argentina")
                    .provincias(geoRefService.getProvincias())
                    .build();
            argentina.getProvincias().forEach(provincia -> {
                provincia.setId(null);
                provincia.setPais(argentina);
                provincia.setLocalidades(geoRefService.getLocalidadesByProvincia(provincia));
            });
            argentina.getProvincias().forEach(provincia -> provincia.getLocalidades().forEach(localidad -> {
                localidad.setId(null);
                localidad.setProvincia(provincia);
            }));
            paisRepository.save(argentina);

        };
    }


    @Bean
    @Order(2)
    CommandLineRunner init() {
        return args -> {
            if (empresaRepository.count() > 0 || sucursalRepository.count() > 0) {
                logger.info("La base de datos ya contiene datos de empresas o sucursales, se omite init.");
                return;
            }
            logger.info("----------------Persistiendo los modelos---------------------");
            //Se crea empresa
            Empresa empresa = Empresa.builder()
                    .cuil(2012334675L)
                    .razonSocial("Empresa de ejemplo A")
                    .nombre("Domodin de dimsdale")
                    .imagenes(Set.of(Imagen.builder().name("empresa").url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRdCvWpOJHdRix0RYrdYDm3PneoLl8xcfJ87A&s").build()))
                    .build();

            //Se crea domicilio para clientes
            Domicilio domicilioCliente = Domicilio.builder()
                    .calle("Avenida siempre viva")
                    .numero(76)
                    .cp(55)
                    //se crea localidad
                    .localidad(null)
                    .build();

            //Se crea domicilio para sucursal
            Domicilio domicilio1 = Domicilio.builder()
                    .calle("Falsa")
                    .numero(123)
                    .cp(123)
                    //se crea localidad
                    .localidad(null)
                    .build();

            //Se crean sucursal 1
            Sucursal sucursal1 = Sucursal.builder()
                    .nombre("Sucursal Lujan")
                    .horarioApertura(LocalTime.of(10, 30, 00))
                    .horarioCierre(LocalTime.of(23, 30, 00))
                    .domicilio(domicilio1)
                    .empresa(empresa)
                    .imagenes(Set.of(Imagen.builder().url("https://res.cloudinary.com/dnjlersq5/image/upload/v1719285019/emqjoymiqeyreskyeiv2.jpg").name("emqjoymiqeyreskyeiv2").build()))
                    .build();

            //Se crea domicilio para sucursal
            Domicilio domicilio2 = Domicilio.builder()
                    .calle("Godoy Cruz")
                    .numero(123)
                    .cp(4120)
                    .localidad(null)
                    .build();

            //Se crea sucursal 2
            Sucursal sucursal2 = Sucursal.builder()
                    .nombre("Surcursal Godoy Cruz")
                    .domicilio(domicilio2)
                    .empresa(empresa)
                    .horarioApertura(LocalTime.of(10, 30, 00))
                    .horarioCierre(LocalTime.of(23, 30, 00))
                    .imagenes(Set.of(Imagen.builder().url("https://res.cloudinary.com/dnjlersq5/image/upload/v1719285019/vbv3xbu8jqoacnraq6kc.jpg").name("vbv3xbu8jqoacnraq6kc").build()))
                    .build();


            //Se agregan las sucursales a la empresa
            empresa.getSucursales().add(sucursal1);
            empresa.getSucursales().add(sucursal2);

            //Se crean categorias de Insumos
            Categoria lacteos = Categoria.builder()
                    .denominacion("Lacteos")
                    .sucursales(new HashSet<>(Set.of(sucursal1, sucursal2)))
                    .imagenes(Set.of(Imagen.builder().name("lacteos").url("https://infoalimentos.org.ar/images/temas/el_rol_del_consumidor/TEMAS_lacteos_interna_horiz_840x410.jpg").build()))
                    .build();
            Categoria harinas = Categoria.builder()
                    .denominacion("Harina")
                    .sucursales(new HashSet<>(Set.of(sucursal1, sucursal2)))
                    .imagenes(Set.of(Imagen.builder().name("harinas").url("https://www.recetasderechupete.com/wp-content/uploads/2018/11/Harina-de-fuerza.jpg").build()))
                    .build();


            //Se crean categorias de Manufacturado
            Categoria pizza = Categoria.builder()
                    .denominacion("Pizzas")
                    .sucursales(new HashSet<>(Set.of(sucursal1, sucursal2)))
                    .imagenes(Set.of(Imagen.builder().name("pizzas").url("https://media.ambito.com/p/e24c324a419cc678f936ac0cdda5209b/adjuntos/239/imagenes/040/952/0040952827/730x0/smart/santo-bar-pizzasjpg.jpg").build()))
                    .build();

            Categoria refrescosCola = Categoria.builder()
                    .denominacion("Refrescos de Cola")
                    .sucursales(new HashSet<>(Set.of(sucursal1)))
                    .imagenes(Set.of(Imagen.builder().name("colas").url("https://www.marketingdirecto.com/wp-content/uploads/2016/05/cola.jpg").build()))
                    .build();

            Categoria bebidas = Categoria.builder()
                    .denominacion("Bebidas")
                    .subCategorias(new HashSet<>(Set.of(refrescosCola)))
                    .sucursales(new HashSet<>(Set.of(sucursal1)))
                    .imagenes(Set.of(Imagen.builder().name("bebidas").url("https://i.pinimg.com/originals/17/53/07/17530782da06b17d1e6654526dbde2be.jpg").build()))
                    .build();
            refrescosCola.setCategoriaPadre(bebidas);
            //Se crean articulos insumo
            //Se crea unidad de medida
            UnidadMedida unidadMedidakg = UnidadMedida.builder()
                    .denominacion("KG")
                    .build();
            UnidadMedida unidadMedidaPorciones = UnidadMedida.builder()
                    .denominacion("Porciones")
                    .build();
            UnidadMedida unidadMedidaLitro = UnidadMedida.builder()
                    .denominacion("Litro")
                    .build();
            //se crea la imagen de queso
            Imagen imagen3 = Imagen.builder()
                    .url("https://www.lacasadelqueso.com.ar/wp-content/uploads/2017/08/queso-mozzarella.jpg")
                    .build();

            ArticuloInsumo queso = ArticuloInsumo.builder()
                    .denominacion("Quesito")
                    .precioVenta(500.0)
                    .precioCompra(100.0)
                    .stockActual(50.0)
                    .stockMaximo(100.0)
                    .esParaElaborar(true)
                    .unidadMedida(unidadMedidakg)
                    .build();
            queso.setSucursal(sucursal1);
            sucursal1.getArticulos().add(queso);

            queso.getImagenes().add(imagen3);
            queso.setCategoria(lacteos);
            lacteos.getArticulos().add(queso);
            //se crea la imagen de harina
            Imagen imagen2 = Imagen.builder()
                    .url("https://i.blogs.es/95d4c3/harina-trigo-tipos/1366_2000.jpg")
                    .build();

            Imagen imagen5 = Imagen.builder()
                    .url("https://atencion24.ar/wp-content/uploads/2022/11/2b53f55e-5057-4548-90fa-cae1a589fe12.jpg")
                    .build();


            ArticuloInsumo harina = ArticuloInsumo.builder()
                    .denominacion("Harina")
                    .precioVenta(500.0)
                    .precioCompra(100.0)
                    .stockActual(50.0)
                    .stockMaximo(100.0)
                    .esParaElaborar(true)
                    .unidadMedida(unidadMedidakg)

                    .build();
            harina.setSucursal(sucursal1);
            sucursal1.getArticulos().add(harina);
            harina.getImagenes().add(imagen2);
            harinas.getArticulos().add(harina);
            harina.setCategoria(harinas);


            //se crea la imagen pizza napolitana
            Imagen imagen1 = Imagen.builder()
                    .url("https://mandolina.co/wp-content/uploads/2023/08/pizza-napolitana-1080x550-1.png")
                    .build();


            //Se crean aritulos manufacturados
            ArticuloManufacturado pizzaNapolitana = ArticuloManufacturado.builder()
                    .denominacion("Pizza Napolitana")
                    .descripcion("La mejor pizza italiana con ingredientes de máxima calidad y frescura ")
                    .precioVenta(6500.00)
                    .preparacion("Listado de pasos")
                    .imagenes(new HashSet<>(Set.of(imagen1)))
                    .unidadMedida(unidadMedidaPorciones)
                    .tiempoEstimadoMinutos(8)
                    .build();
            pizza.getArticulos().add(pizzaNapolitana);
            pizzaNapolitana.setCategoria(pizza);
            pizzaNapolitana.setSucursal(sucursal1);
            sucursal1.getArticulos().add(pizzaNapolitana);
            ArticuloInsumo coca = ArticuloInsumo.builder()
                    .denominacion("Coca-cola")
                    .precioVenta(2500.00)
                    .precioCompra(1000.00)
                    .stockActual(50.0)
                    .stockMaximo(100.0)
                    .esParaElaborar(false)
                    .imagenes(new HashSet<>(Set.of(imagen5)))
                    .unidadMedida(unidadMedidaLitro)
                    .build();
            refrescosCola.getArticulos().add(coca);
            coca.setCategoria(refrescosCola);

            coca.setSucursal(sucursal1);
            sucursal1.getArticulos().add(coca);

            ArticuloManufacturadoDetalle artManufacDetalleQueso = ArticuloManufacturadoDetalle.builder()
                    .articuloInsumo(queso)
                    .cantidad(0.5)
                    .build();
            ArticuloManufacturadoDetalle artManufacDetalleHarina = ArticuloManufacturadoDetalle.builder()
                    .articuloInsumo(harina)
                    .cantidad(0.3)
                    .build();
            pizzaNapolitana.setArticuloManufacturadoDetalles(Set.of(artManufacDetalleHarina, artManufacDetalleQueso));
            artManufacDetalleQueso.setArticuloManufacturado(pizzaNapolitana);
            artManufacDetalleHarina.setArticuloManufacturado(pizzaNapolitana);
//Como agregar los art de una sub cat a la categoria padre


            // Se agregan las categorias a la sucursal
            sucursal1.setCategorias(new HashSet<>(Set.of(lacteos, harinas, pizza, bebidas, refrescosCola)));
            sucursal2.setCategorias(new HashSet<>(Set.of(lacteos, harinas, pizza)));


            //se crea la imagen cliente
            Imagen imagen4 = Imagen.builder()
                    .url("https://cdn-icons-png.flaticon.com/512/4814/4814852.png")
                    .build();

            Usuario usuario1 = Usuario.builder()
                    .auth0Id("aaaa")
                    .username("gonzaPrueba")
                    .email("juanperez@gmail.com")
                    .rol(Rol.Cliente)
                    .build();
            Usuario usuario2 = Usuario.builder()
                    .auth0Id("admin")
                    .username("admin")
                    .email("admin@gmail.com")
                    .rol(Rol.Admin)
                    .build();
            Empleado empleado = Empleado.builder()
                    .apellido("Bleuss")
                    .nombre("Albert")
                    .email("Bleu@gmail.com")
                    .telefono("+54228282")
                    .usuario(usuario2)
                    .build();

            //Se crea factura
            Factura factura1 = Factura.builder()
                    .fechaFacturacion(LocalDate.of(2024, 4, 23))
                    .mpPaymentId(1)
                    .mpMerchantOrderId(1)
                    .mpPreferenceId("MP-12322")
                    .mpPaymentType("Visa")
                    .formaPago(FormaPago.Efectivo)
                    .totalVenta(1400.21)
                    .build();


            //Se crea detalle de pedido

            DetallePedido detallePedido = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(6500.00)
                    .articulo(pizzaNapolitana)
                    .build();

            DetallePedido detallePedido2 = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(5000.00)
                    .articulo(coca)
                    .build();
            DetallePedido detallePedido3 = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(6500.00)
                    .articulo(pizzaNapolitana)
                    .build();

            DetallePedido detallePedido4 = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(5000.00)
                    .articulo(coca)
                    .build();

            //Se crea pedido para el cliente1
            Pedido pedido1 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.of(11, 16, 10))
                    .total(11500.00)
                    .totalCosto(1000.10)
                    .estado(Estado.EnProceso)
                    .tipoEnvio(TipoEnvio.Delivery)
                    .formaPago(FormaPago.MercadoPago)
                    .fechaPedido(LocalDate.of(2024, 4, 23))
                    .factura(factura1)
                    .domicilio(domicilioCliente)
                    .sucursal(sucursal1)
                    .detallePedidos(new HashSet<>(Set.of(detallePedido, detallePedido2)))
                    .build();

            detallePedido.setPedido(pedido1);
            detallePedido2.setPedido(pedido1);
            //Pedido 2
            Pedido pedido2 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.of(11, 16, 10))
                    .total(11500.00)
                    .totalCosto(1000.10)
                    .estado(Estado.EnProceso)
                    .tipoEnvio(TipoEnvio.TakeAway)
                    .formaPago(FormaPago.MercadoPago)
                    .fechaPedido(LocalDate.of(2024, 4, 23))
                    .factura(null)//Ver esto
                    .domicilio(domicilioCliente)
                    .sucursal(sucursal1)
                    .detallePedidos(new HashSet<>(Set.of(detallePedido3, detallePedido4)))
                    .build();

            detallePedido3.setPedido(pedido2);
            detallePedido4.setPedido(pedido2);

            //Se crea cliente
            Cliente cliente1 = Cliente.builder()
                    .nombre("Juan")
                    .apellido("Perez")
                    .telefono("2615854785")
                    .fechaNacimiento(LocalDate.of(1990, 11, 12))
                    .imagenes(Set.of(imagen4))
                    .usuario(usuario1)
                    .pedidos(new HashSet<>(Set.of(pedido1,pedido2)))
                    .domicilios(new HashSet<>(Set.of(domicilioCliente)))
                    .build();

            //se agrega el cliente al domicilio
            domicilioCliente.setClientes(new HashSet<>(Set.of(cliente1)));
            pedido1.setCliente(cliente1);
            pedido2.setCliente(cliente1);
            //Se crea imagen para la promocion
            Imagen imagenPromocion = Imagen.builder()
                    .url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSa5KnhV_vPs_Ww8QFMruBYkK0srngvkuEm1aQWLq1sSg&s")

                    .build();
            //Se crea promocion para pizza

            Promocion promocionPizza = Promocion.builder()
                    .imagenes(new HashSet<>(Set.of(imagenPromocion)))
                    .denominacion("Promocion 2 x 1 en pizza napolitana")
                    .descripcionDescuento("Si llevas dos pizzas napolitana de 8 porciones llevate una gratis ")
                    .fechaDesde(LocalDate.now())
                    .fechaHasta(LocalDate.now().minusWeeks(5))
                    .horaDesde(LocalTime.of(0, 0))
                    .horaHasta(LocalTime.of(0, 0))
                    .tipoPromocion(TipoPromocion.Promocion)
                    .precioPromocional(6500.0)
                    .build();

            PromocionDetalle promocionDetallePizzaNapo = PromocionDetalle.builder()
                    .cantidad(2)
                    .articulo(pizzaNapolitana)
                    .build();
            promocionDetallePizzaNapo.setPromocion(promocionPizza);
            promocionPizza.getDetallesPromocion().add(promocionDetallePizzaNapo);
            detallePedido.setPromociones(new HashSet<>(Set.of(promocionPizza)));
            sucursal1.getPromociones().add(promocionPizza);

            pedido1.setEmpleado(empleado);

            clienteRepository.save(cliente1);
        };
    }

    @Bean
    @Order(3)
    CommandLineRunner init3() {
        return args -> {
            if (domicilioRepository.count() == 0) {
                logger.info("No hay domicilios en la base de datos, se omite init3.");
                return;
            }
            List<Localidad> localidades = this.localidadService.getAll();
            if (!localidades.isEmpty()) {
                List<Domicilio> domicilios = this.domicilioRepository.findAll();
                for (Domicilio domicilio : domicilios){
                    Random random = new Random();
                    int randomIndex = random.nextInt(localidades.size());
                    Localidad localidad = localidades.get(randomIndex);
                    domicilio.setLocalidad(localidades.get(randomIndex));
                    localidad.getDomicilios().add(domicilio);
                    this.domicilioRepository.save(domicilio);
                }

            }


        };

    }

    @Bean
    @Order(4)
    CommandLineRunner init4() {
        //adminr@gmail.com
        //A123456!a
        return args -> {
            Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername("adminr");
            if (!usuarioExistente.isPresent()) { // Verifica si el Optional está vacío
                Usuario usuario = Usuario.builder()
                        .rol(Rol.Admin)
                        .email("adminr@gmail.com")
                        .alta(true)
                        .username("adminr") // Campo opcional
                        .build();
                usuarioRepository.save(usuario);
                logger.info("Usuario 'adminr' creado exitosamente.");
            } else {
                logger.info("Usuario 'adminr' ya existe. No se creó uno nuevo.");
            }
        };


    }




}







