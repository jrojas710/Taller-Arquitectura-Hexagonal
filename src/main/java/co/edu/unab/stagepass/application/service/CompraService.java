package co.edu.unab.stagepass.application.service;

import co.edu.unab.stagepass.application.port.in.CompraUseCase;
import co.edu.unab.stagepass.application.port.out.OrdenRepository;
import co.edu.unab.stagepass.application.port.out.PasarelaPagoPort;
import co.edu.unab.stagepass.application.port.out.RecintoRepository;
import co.edu.unab.stagepass.domain.exception.NoEncontradoException;
import co.edu.unab.stagepass.domain.exception.ReglaNegocioException;
import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.model.Cliente;
import co.edu.unab.stagepass.domain.model.Evento;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;
import co.edu.unab.stagepass.domain.service.CalculadoraTarifas;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class CompraService implements CompraUseCase {

    public static final int MAX_BOLETAS_POR_EVENTO = 6;

    private final RecintoRepository recintoRepository;
    private final OrdenRepository ordenRepository;
    private final List<PasarelaPagoPort> pasarelas;
    private final CalculadoraTarifas calculadora;
    private final Clock reloj;

    public CompraService(RecintoRepository recintoRepository, OrdenRepository ordenRepository,
                         List<PasarelaPagoPort> pasarelas, CalculadoraTarifas calculadora, Clock reloj) {
        this.recintoRepository = recintoRepository;
        this.ordenRepository = ordenRepository;
        this.pasarelas = pasarelas;
        this.calculadora = calculadora;
        this.reloj = reloj;
    }

    @Override
    public Orden iniciarCompra(String eventoId, Cliente cliente, List<String> asientoIds, MetodoPago metodoPago) {
        if (asientoIds == null || asientoIds.isEmpty()) {
            throw new ReglaNegocioException("Debe seleccionar al menos un asiento");
        }
        if (new HashSet<>(asientoIds).size() != asientoIds.size()) {
            throw new ReglaNegocioException("Hay asientos repetidos en la seleccion");
        }

        Evento evento = buscarEvento(eventoId);
        if (!evento.estaEnVenta()) {
            throw new ReglaNegocioException("El evento no esta a la venta (estado " + evento.getEstado() + ")");
        }

        List<Asiento> asientos = recintoRepository.buscarAsientos(asientoIds);
        if (asientos.size() != asientoIds.size()) {
            throw new NoEncontradoException("Uno o mas asientos no existen");
        }

        LocalDateTime ahora = LocalDateTime.now(reloj);
        List<Orden> ordenesActivas = ordenesActivas(eventoId, ahora);

        // validar que cada asiento sea del recinto y este libre
        for (Asiento asiento : asientos) {
            if (!asiento.getRecintoId().equals(evento.getRecintoId())) {
                throw new ReglaNegocioException("El asiento " + asiento.getCodigo() + " no pertenece al recinto del evento");
            }
            boolean ocupado = asiento.getEstado() != Asiento.Estado.DISPONIBLE
                    || ordenesActivas.stream().anyMatch(o -> o.tieneAsiento(asiento.getId()));
            if (ocupado) {
                throw new ReglaNegocioException("El asiento " + asiento.getCodigo() + " no esta disponible");
            }
        }

        // limite de 6 boletas por cliente en el mismo evento
        int boletasDelCliente = ordenesActivas.stream()
                .filter(o -> o.getCliente().getId().equals(cliente.getId()))
                .mapToInt(o -> o.getAsientos().size())
                .sum();
        if (boletasDelCliente + asientos.size() > MAX_BOLETAS_POR_EVENTO) {
            throw new ReglaNegocioException("Un cliente no puede tener mas de " + MAX_BOLETAS_POR_EVENTO
                    + " boletas para el mismo evento (ya tiene " + boletasDelCliente + ")");
        }

        CalculadoraTarifas.Liquidacion liq = calculadora.calcular(asientos);

        // bloqueo temporal de 10 minutos
        asientos.forEach(a -> a.setEstado(Asiento.Estado.BLOQUEADO));
        Orden orden = new Orden(UUID.randomUUID().toString(), eventoId, cliente, asientos,
                liq.subtotal(), liq.tarifa(), liq.descuento(), liq.total(),
                ahora, ahora.plusMinutes(Orden.MINUTOS_BLOQUEO), metodoPago, Orden.Estado.PENDIENTE_PAGO, null);

        String referencia = buscarPasarela(metodoPago).registrarPago(orden);
        orden.setReferenciaPago(referencia);

        ordenRepository.guardar(orden);
        return orden;
    }

    @Override
    public List<Asiento> consultarAsientos(String eventoId) {
        Evento evento = buscarEvento(eventoId);
        LocalDateTime ahora = LocalDateTime.now(reloj);
        List<Orden> activas = ordenesActivas(eventoId, ahora);
        List<Asiento> asientos = recintoRepository.buscarAsientosDeRecinto(evento.getRecintoId());

        // el estado real sale de la base legacy + las reservas activas en Mongo
        for (Asiento asiento : asientos) {
            if (asiento.getEstado() != Asiento.Estado.DISPONIBLE) {
                continue;
            }
            for (Orden orden : activas) {
                if (orden.tieneAsiento(asiento.getId())) {
                    asiento.setEstado(orden.getEstado() == Orden.Estado.APROBADA
                            ? Asiento.Estado.VENDIDO : Asiento.Estado.BLOQUEADO);
                }
            }
        }
        return asientos;
    }

    @Override
    public Orden consultarOrden(String ordenId) {
        return ordenRepository.buscarPorId(ordenId)
                .orElseThrow(() -> new NoEncontradoException("No existe la orden " + ordenId));
    }

    private Evento buscarEvento(String eventoId) {
        return recintoRepository.buscarEvento(eventoId)
                .orElseThrow(() -> new NoEncontradoException("No existe el evento " + eventoId));
    }

    private List<Orden> ordenesActivas(String eventoId, LocalDateTime ahora) {
        return ordenRepository.buscarPorEvento(eventoId).stream()
                .filter(o -> o.ocupaAsientos(ahora))
                .toList();
    }

    private PasarelaPagoPort buscarPasarela(MetodoPago metodo) {
        return pasarelas.stream()
                .filter(p -> p.getMetodo() == metodo)
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException("No hay pasarela para " + metodo));
    }
}
