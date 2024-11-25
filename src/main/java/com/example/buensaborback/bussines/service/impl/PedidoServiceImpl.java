package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.*;
import com.example.buensaborback.domain.entities.*;
import com.example.buensaborback.domain.entities.enums.Estado;
import com.example.buensaborback.domain.entities.enums.TipoEnvio;
import com.example.buensaborback.presentation.advice.exception.InsufficientStock;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import com.example.buensaborback.repositories.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class PedidoServiceImpl implements IPedidoService {
    private final PedidoRepository pedidoRepository;
    private final IUsuarioService usuarioService;
    private final IArtManufacturadoService artManufacturadoService;
    private final IArticuloInsumoService artInsumoService;
    private final IClienteService clienteService;
    private final IFacturaService facturaService;
    private final ISucursalService sucursalService;
    private final IDomicilioService domicilioService;
@Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository, UsuarioServiceImpl usuarioService, ArtManufacturadoServiceImpl artManufacturadoService, ClienteServiceImpl clienteServiceImpl, ArticuloInsumoServiceImpl artInsumoService, FacturaServiceImpl facturaService, ISucursalService sucursalService, IDomicilioService domicilioService) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioService = usuarioService;
        this.artManufacturadoService = artManufacturadoService;
        this.clienteService = clienteServiceImpl;
        this.artInsumoService = artInsumoService;
        this.facturaService = facturaService;
        this.sucursalService = sucursalService;
    this.domicilioService = domicilioService;
}
    @Override
    public Pedido getPedidoById(Long id){
        return this.pedidoRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Pedido con ID %d no encontrado", id)));
    }

    @Override
    public boolean existsPedidoById(Long id){
        return this.pedidoRepository.existsById(id);
    }
  @Transactional
    public Pedido save(Pedido pedido) {
        LocalTime now = LocalTime.now();
        int tiempoPreparacion=0;
        pedido.setEstado(Estado.EnProceso);
        Domicilio domicilio =domicilioService.getDomicilioById(pedido.getDomicilio().getId());
        pedido.setDomicilio(domicilio);
        Sucursal sucursal = sucursalService.getSucursalById(pedido.getSucursal().getId());
        pedido.setSucursal(sucursal);
        Usuario usuarioOp = usuarioService.getUsuarioByUsername(pedido.getCliente().getUsuario().getUsername());
            pedido.setCliente(usuarioOp.getCliente());
            for (DetallePedido detalle : pedido.getDetallePedidos()) {
                //Preguntar si existe, sino Falla
                Articulo articulo = getArticulo(detalle);
                //Revisar cantidades actuales de los insumos
                if(articulo instanceof ArticuloManufacturado){
                    ((ArticuloManufacturado) articulo).getArticuloManufacturadoDetalles().forEach(detalleManufacturado -> {      //Cantidad que necesito para 1      //Cantidad que pide
                        double stockRestante =  detalleManufacturado.getArticuloInsumo().getStockActual() - detalleManufacturado.getCantidad() * detalle.getCantidad();
                        if(stockRestante < 0) {
                            throw new InsufficientStock("No hay suficiente cantidad de ");
                        }

                        //Hay que guardar el nuevo stock
                        detalleManufacturado.getArticuloInsumo().setStockActual(stockRestante);
                    });
                    tiempoPreparacion+=((ArticuloManufacturado) articulo).getTiempoEstimadoMinutos();
                }

                pedido.setTotalCosto(calcularCostoTotal(pedido.getDetallePedidos()));
                detalle.setArticulo(articulo);
                detalle.setPedido(pedido);
            }
            if (pedido.getTipoEnvio() == TipoEnvio.Delivery) {
                tiempoPreparacion += 10;
            }
        LocalTime tiempoEstimado = now.plusMinutes(tiempoPreparacion).withSecond(0).withNano(0);

        pedido.setHoraEstimadaFinalizacion(tiempoEstimado);
            return pedidoRepository.save(pedido);

    }

    private Double calcularCostoTotal(Set<DetallePedido> detallesPedido ) {
        double total = 0.0;
        for (DetallePedido detallePedido : detallesPedido) {
            Articulo articulo = getArticulo(detallePedido);

            if (articulo instanceof ArticuloManufacturado) {
                double subTotalManufacturados = ((ArticuloManufacturado) articulo)
                        .getArticuloManufacturadoDetalles()
                        .stream()
                        .mapToDouble(detalle -> detalle.getArticuloInsumo().getPrecioCompra() * detalle.getCantidad()*detallePedido.getCantidad())
                        .sum();
                total += subTotalManufacturados;

            } else if (articulo instanceof ArticuloInsumo) {
                total += ((ArticuloInsumo) articulo).getPrecioCompra() * detallePedido.getCantidad();
            }
        }
        return total;
    }

    private Articulo getArticulo(DetallePedido detallePedido){
        if(artManufacturadoService.existsArticuloManufacturadoById(detallePedido.getArticulo().getId())){
            return artManufacturadoService.getArticuloManufacturadoById(detallePedido.getArticulo().getId());
        }else{
           return artInsumoService.getArticuloInsumoById(detallePedido.getArticulo().getId());
        }
    }

    @Override
    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public List<Pedido> getAllByFecha(LocalDate fecha, Long idSucursal) {
        Sucursal sucursal = sucursalService.getSucursalById(idSucursal);
        return pedidoRepository.findByFechaPedidoAndSucursal(fecha, sucursal);
    }

    @Override
    public Pedido delete(Long id) {
        Pedido pedido = this.getPedidoById(id);
        pedido.setAlta(pedido.isAlta());
        return pedidoRepository.save(pedido);
    }

    @Override
    public List<Pedido> getAllByCliente(String user){

        Usuario usuario =this.usuarioService.getUsuarioByUsername(user);
        return this.pedidoRepository.findByAltaTrueAndCliente(usuario.getCliente());
    }
    public List<Object[]> findTopProducts(LocalDate startDate, LocalDate endDate, Long idSucursal) {
        return pedidoRepository.findTopProductsBySucursal(startDate, endDate, idSucursal);
    }

    public List<Pedido> findPedidosBetweenDates(LocalDate startDate, LocalDate endDate, Long idSucursal) {
        return pedidoRepository.findBySucursal_IdAndFechaPedidoBetween(startDate, endDate, idSucursal);

    }
    public List<Pedido> findByEstado(Estado estado, Long idSucursal){
        Sucursal sucursal = sucursalService.getSucursalById(idSucursal);
        return pedidoRepository.findByEstadoAndSucursal(estado, sucursal);
    }

    public Pedido actualizarEstado(Long id, Estado estado){
        Pedido pedido = this.getPedidoById(id);
        pedido.setEstado(estado);
       if(estado == Estado.Entregado) {
           Factura factura = facturaService.crearFactura(pedido);
           pedido.setFactura(factura);
           Pedido pedidoFactura=pedidoRepository.save(pedido);
           facturaService.enviarFacturaPorEmail(pedidoFactura.getCliente().getUsuario().getEmail(),factura);
           return pedidoFactura;
       }

        return  pedidoRepository.save(pedido);
    }

    @Override
    public Pedido findByFactura(Factura factura){
        return  pedidoRepository.findByFactura(factura);
    }


}
