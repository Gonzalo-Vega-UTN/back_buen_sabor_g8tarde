package com.example.buensaborback;

import com.example.buensaborback.client.GeoRefService;
import com.example.buensaborback.domain.entities.*;
import com.example.buensaborback.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
	GeoRefService geoRefService;

	public static void main(String[] args) {
		SpringApplication.run(BuenSaborBackApplication.class, args);
		logger.info("Estoy activo en el main");
	}

	@Bean
	CommandLineRunner init2() {
		return args -> {
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
			System.out.println(argentina);
			System.out.println(argentina.getProvincias());
			paisRepository.save(argentina);

		};
	}


	//@Bean
//	CommandLineRunner init() {
//		return args -> {
//			logger.info("----------------Persistiendo los modelos---------------------");
//			//Se crea empresa
//			Empresa empresa = Empresa.builder()
//					.cuil(2012334675L)
//					.razonSocial("Empresa de ejemplo A")
//					.nombre("Domodin de dimsdale")
//					.build();
//
//			//Se crea provincia
//			Provincia provincia = Provincia.builder()
//					.nombre("Mendoza")
//					.pais(Pais.builder()
//							.nombre("Argentina")
//							.build())
//					.build();
//			//Se crea domicilio para clientes
//			Domicilio domicilioCliente=Domicilio.builder()
//					.calle("Avenida siempre viva")
//					.numero(76)
//					.cp(55)
//					//se crea localidad
//					.localidad(
//							Localidad.builder()
//									.nombre("Fondo de bikini")
//									.provincia(provincia)
//									.build()
//					)
//					.build();
//			//Se crea domicilio para sucursal
//				Domicilio domicilio1=Domicilio.builder()
//						.calle("Falsa")
//						.numero(123)
//						.cp(123)
//						//se crea localidad
//						.localidad(
//								Localidad.builder()
//										.nombre("Dimsdale")
//										.provincia(provincia)
//										.build()
//						)
//						.build();
//			//Se crean sucursal 1
//			Sucursal sucursal1 = Sucursal.builder()
//					.nombre("Sucursal Lujan")
//					.horarioApertura(LocalTime.of(10,30,00))
//					.horarioCierre(LocalTime.of(23,30,00))
//					.domicilio(domicilio1)
//					.empresa(empresa)
//					.build();
//
//			//Se crea domicilio para sucursal
//			Domicilio domicilio2 = Domicilio.builder()
//					.calle("Godoy Cruz")
//					.numero(123)
//					.cp(4120)
//					.localidad(Localidad.builder()
//							.nombre("Godoy Cruz")
//							.provincia(provincia)
//							.build())
//					.build();
//
//			//Se crea sucursal 2
//			Sucursal sucursal2 = Sucursal.builder()
//					.nombre("Surcursal Godoy Cruz")
//					.domicilio(domicilio2)
//					.empresa(empresa)
//					.horarioApertura(LocalTime.of(10,30,00))
//					.horarioCierre(LocalTime.of(23,30,00))
//					.build();
//
//
//			//Se agregan las sucursales a la empresa
//			empresa.getSucursales().add(sucursal1);
//			empresa.getSucursales().add(sucursal2);
//
//			//Se crean categorias de Insumos
//			Categoria lacteos = Categoria.builder()
//					.denominacion("Lacteos")
//					.sucursales(new HashSet<>(Set.of(sucursal1, sucursal2)))
//					.build();
//			Categoria harinas = Categoria.builder()
//					.denominacion("Harina")
//					.sucursales(new HashSet<>(Set.of(sucursal1, sucursal2)))
//					.build();
//
//
//			//Se crean categorias de Manufacturado
//			Categoria pizza = Categoria.builder()
//					.denominacion("Pizzas")
//					.sucursales(new HashSet<>(Set.of(sucursal1, sucursal2)))
//					.build();
//
//			Categoria refrescosCola = Categoria.builder()
//					.denominacion("Refrescos de Cola")
//					.sucursales(new HashSet<>(Set.of(sucursal1, sucursal2)))
//					.build();
//
//			Categoria bebidas = Categoria.builder()
//					.denominacion("Bebidas")
//					.subCategorias(new HashSet<>(Set.of(refrescosCola)))
//					.sucursales(new HashSet<>(Set.of(sucursal1, sucursal2)))
//					.build();
//			refrescosCola.setCategoriaPadre(bebidas);
//			//Se crean articulos insumo
//				//Se crea unidad de medida
//			UnidadMedida unidadMedidakg= UnidadMedida.builder()
//					.denominacion("KG")
//					.build();
//			UnidadMedida unidadMedidaPorciones = UnidadMedida.builder()
//					.denominacion("Porciones")
//					.build();
//			UnidadMedida unidadMedidaLitro = UnidadMedida.builder()
//					.denominacion("Litro")
//					.build();
//			//se crea la imagen de queso
//			Imagen imagen3 = Imagen.builder()
//					.url("https://www.lacasadelqueso.com.ar/wp-content/uploads/2017/08/queso-mozzarella.jpg")
//					.build();
//
//			ArticuloInsumo queso= ArticuloInsumo.builder()
//					.denominacion("Quesito")
//					.precioVenta(500.0)
//					.precioCompra(100.0)
//					.stockActual(50.0)
//					.stockMaximo(100.0)
//					.esParaElaborar(true)
//					.unidadMedida(unidadMedidakg)
//					.build();
//			queso.setSucursal(sucursal1);
//			sucursal1.getArticulos().add(queso);
//
//			queso.getImagenes().add(imagen3);
//			queso.setCategoria(lacteos);
//			lacteos.getArticulos().add(queso);
//			//se crea la imagen de harina
//			Imagen imagen2 = Imagen.builder()
//					.url("https://i.blogs.es/95d4c3/harina-trigo-tipos/1366_2000.jpg")
//					.build();
//
//			Imagen imagen5 = Imagen.builder()
//					.url("https://atencion24.ar/wp-content/uploads/2022/11/2b53f55e-5057-4548-90fa-cae1a589fe12.jpg")
//					.build();
//
//
//			ArticuloInsumo harina= ArticuloInsumo.builder()
//					.denominacion("Harina")
//					.precioVenta(500.0)
//					.precioCompra(100.0)
//					.stockActual(50.0)
//					.stockMaximo(100.0)
//					.esParaElaborar(true)
//					.unidadMedida(unidadMedidakg)
//
//					.build();
//			harina.setSucursal(sucursal1);
//			sucursal1.getArticulos().add(harina);
//			harina.getImagenes().add(imagen2);
//			harinas.getArticulos().add(harina);
//			harina.setCategoria(harinas);
//
//
//			//se crea la imagen pizza napolitana
//			Imagen imagen1 = Imagen.builder()
//					.url("https://mandolina.co/wp-content/uploads/2023/08/pizza-napolitana-1080x550-1.png")
//					.build();
//
//
//			//Se crean aritulos manufacturados
//			ArticuloManufacturado pizzaNapolitana = ArticuloManufacturado.builder()
//					.denominacion("Pizza Napolitana")
//					.descripcion("La mejor pizza italiana con ingredientes de máxima calidad y frescura ")
//					.precioVenta(6500.00)
//					.preparacion("Listado de pasos")
//					.imagenes(new HashSet<>(Set.of(imagen1)))
//					.unidadMedida(unidadMedidaPorciones)
//					.tiempoEstimadoMinutos(8)
//					.build();
//			pizza.getArticulos().add(pizzaNapolitana);
//			pizzaNapolitana.setCategoria(pizza);
//			pizzaNapolitana.setSucursal(sucursal1);
//			sucursal1.getArticulos().add(pizzaNapolitana);
//			ArticuloInsumo coca = ArticuloInsumo.builder()
//					.denominacion("Coca-cola")
//					.precioVenta(2500.00)
//					.precioCompra(1000.00)
//					.stockActual(50.0)
//					.stockMaximo(100.0)
//					.esParaElaborar(false)
//					.imagenes(new HashSet<>(Set.of(imagen5)))
//					.unidadMedida(unidadMedidaLitro)
//					.build();
//			refrescosCola.getArticulos().add(coca);
//			coca.setCategoria(refrescosCola);
//
//			coca.setSucursal(sucursal2);
//			sucursal2.getArticulos().add(coca);
//
//			ArticuloManufacturadoDetalle artManufacDetalleQueso = ArticuloManufacturadoDetalle.builder()
//					.articuloInsumo(queso)
//					.cantidad(0.5)
//					.build();
//			ArticuloManufacturadoDetalle artManufacDetalleHarina = ArticuloManufacturadoDetalle.builder()
//					.articuloInsumo(harina)
//					.cantidad(0.3)
//					.build();
//			pizzaNapolitana.setArticuloManufacturadoDetalles(Set.of(artManufacDetalleHarina, artManufacDetalleQueso));
//			artManufacDetalleQueso.setArticuloManufacturado(pizzaNapolitana);
//			artManufacDetalleHarina.setArticuloManufacturado(pizzaNapolitana);
////Como agregar los art de una sub cat a la categoria padre
//
//
//
//			// Se agregan las categorias a la sucursal
//			sucursal1.setCategorias(new HashSet<>(Set.of(lacteos,harinas,pizza,bebidas, refrescosCola)));
//			sucursal2.setCategorias(new HashSet<>(Set.of(lacteos,harinas,pizza,bebidas, refrescosCola)));
//
//
//			//se crea la imagen cliente
//			Imagen imagen4 = Imagen.builder()
//					.url("https://cdn-icons-png.flaticon.com/512/4814/4814852.png")
//					.build();
//
//			Usuario usuario1 = Usuario.builder()
//					.auth0Id("aaaa")
//					.username("gonzaPrueba")
//					.email("juanperez@gmail.com")
//					.rol(Rol.Cliente)
//					.build();
//			Usuario usuario2 = Usuario.builder()
//					.auth0Id("admin")
//					.username("admin")
//					.email("admin@gmail.com")
//					.rol(Rol.Admin)
//					.build();
//			Empleado empleado=Empleado.builder()
//					.apellido("Bleuss")
//					.nombre("Albert")
//					.email("Bleu@gmail.com")
//					.telefono("+54228282")
//					.usuario(usuario2)
//					.build();
//
//			//Se crea factura
//			Factura factura1 = Factura.builder()
//					.fechaFacturacion(LocalDate.of(2024,4,23))
//					.mpPaymentId(1)
//					.mpMerchantOrderId(1)
//					.mpPreferenceId("MP-12322")
//					.mpPaymentType("Visa")
//					.formaPago(FormaPago.Efectivo)
//					.totalVenta(1400.21)
//					.build();
//
//
//			//Se crea detalle de pedido
//
//			DetallePedido detallePedido = DetallePedido.builder()
//					.cantidad(1)
//					.subTotal(6500.00)
//					.articulo(pizzaNapolitana)
//					.build();
//
//			DetallePedido detallePedido2 = DetallePedido.builder()
//					.cantidad(2)
//					.subTotal(5000.00)
//					.articulo(coca)
//					.build();
//
//			//Se crea pedido para el cliente1
//			Pedido pedido1 = Pedido.builder()
//					.horaEstimadaFinalizacion(LocalTime.of(11,16,10))
//					.total(11500.00)
//					.totalCosto(1000.10)
//					.estado(Estado.Preparacion)
//					.tipoEnvio(TipoEnvio.TakeAway)
//					.formaPago(FormaPago.MercadoPago)
//					.fechaPedido(LocalDate.of(2024,4,23))
//					.factura(factura1)
//					.domicilio(domicilioCliente)
//					.sucursal(sucursal1)
//					.detallePedidos(new HashSet<>(Set.of(detallePedido,detallePedido2)))
//					.build();
//
//			detallePedido.setPedido(pedido1);
//			detallePedido2.setPedido(pedido1);
//
//
//			//Se crea cliente
//			Cliente cliente1 = Cliente.builder()
//					.nombre("Juan")
//					.apellido("Perez")
//					.telefono("2615854785")
//					.fechaNacimiento(LocalDate.of(1990,11,12))
//					.imagenes(Set.of(imagen4))
//					.usuario(usuario1)
//					.pedidos(new HashSet<>(Set.of(pedido1)))
//					.domicilios(new HashSet<>(Set.of(domicilioCliente)))
//					.build();
//
//			//se agrega el cliente al domicilio
//			domicilioCliente.setClientes(new HashSet<>(Set.of(cliente1)));
//			pedido1.setCliente(cliente1);
//
//			//Se crea imagen para la promocion
//			Imagen imagenPromocion = Imagen.builder()
//					.url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSa5KnhV_vPs_Ww8QFMruBYkK0srngvkuEm1aQWLq1sSg&s")
//
//					.build();
//			//Se crea promocion para pizza
//
//			Promocion promocionPizza = Promocion.builder()
//					.imagenes(new HashSet<>(Set.of(imagenPromocion)))
//					.denominacion("Promocion 2 x 1 en pizza napolitana")
//					.descripcionDescuento("Si llevas dos pizzas napolitana de 8 porciones llevate una gratis ")
//					.fechaDesde(LocalDate.now())
//					.fechaHasta(LocalDate.now().minusWeeks(5))
//					.horaDesde(LocalTime.of(0,0))
//					.horaHasta(LocalTime.of(0,0))
//					.tipoPromocion(TipoPromocion.Promocion)
//					.precioPromocional(3000.0)
//					.build();
//
//			PromocionDetalle promocionDetallePizzaNapo = PromocionDetalle.builder()
//					.cantidad(2)
//					.articulo(pizzaNapolitana)
//					.build();
//			promocionDetallePizzaNapo.setPromocion(promocionPizza);
//			promocionPizza.getDetallesPromocion().add(promocionDetallePizzaNapo);
//			detallePedido.setPromociones(new HashSet<>(Set.of(promocionPizza)));
//			sucursal1.getPromociones().add(promocionPizza);
//
//			pedido1.setEmpleado(empleado);
//
//			clienteRepository.save(cliente1);
//		};
//}



}







