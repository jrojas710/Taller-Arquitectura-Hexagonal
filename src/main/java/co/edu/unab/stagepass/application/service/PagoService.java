package co.edu.unab.stagepass.application.service;

import co.edu.unab.stagepass.application.port.in.PagoUseCase;
import co.edu.unab.stagepass.application.port.out.BoletoRepository;
import co.edu.unab.stagepass.application.port.out.FacturacionPort;
import co.edu.unab.stagepass.application.port.out.GeneradorBoletoPort;
import co.edu.unab.stagepass.application.port.out.NotificacionPort;
import co.edu.unab.stagepass.application.port.out.OrdenRepository;
import co.edu.unab.stagepass.application.port.out.PasarelaPagoPort;
import co.edu.unab.stagepass.domain.exception.NoEncontradoException;
import co.edu.unab.stagepass.domain.exception.ReglaNegocioException;
import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.model.Boleto;
import co.edu.unab.stagepass.domain.model.EstadoPago;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PagoService implements PagoUseCase {

    private final OrdenRepository ordenRepository;
    private final BoletoRepository boletoRepository;
    private final List<PasarelaPagoPort> pasarelas;
    private final GeneradorBoletoPort generadorBoleto;
    private final FacturacionPort facturacion;
    private final List<NotificacionPort> notificadores;
    private final Clock reloj;

    public PagoService(OrdenRepository ordenRepository, BoletoRepository boletoRepository,
                       List<PasarelaPagoPort> pasarelas, GeneradorBoletoPort generadorBoleto,
                       FacturacionPort facturacion, List<NotificacionPort> notificadores, Clock reloj) {
        this.ordenRepository = ordenRepository;
        this.boletoRepository = boletoRepository;
        this.pasarelas = pasarelas;
        this.generadorBoleto = generadorBoleto;
        this.facturacion = facturacion;
        this.notificadores = notificadores;
        this.reloj = reloj;
    }

    @Override
    public Orden procesarNotificacion(MetodoPago metodoPago, String referenciaPago) {
        Orden orden = ordenRepository.buscarPorReferenciaPago(referenciaPago)
                .orElseThrow(() -> new NoEncontradoException("No hay orden con la referencia " + referenciaPago));

        if (orden.getMetodoPago() != metodoPago) {
            throw new ReglaNegocioException("La notificacion no corresponde a la pasarela de la orden");
        }
        // las pasarelas pueden mandar la misma notificacion varias veces
        if (orden.getEstado() != Orden.Estado.PENDIENTE_PAGO) {
            return orden;
        }

        // verificamos el pago directamente con la pasarela
        EstadoPago estado = buscarPasarela(metodoPago).consultarEstado(referenciaPago);
        LocalDateTime ahora = LocalDateTime.now(reloj);

        if (estado == EstadoPago.PENDIENTE) {
            return orden;
        }
        if (estado == EstadoPago.RECHAZADO) {
            orden.rechazar();
        } else if (orden.estaVencida(ahora)) {
            // el pago llego despues de los 10 minutos: los asientos ya se liberaron
            orden.expirar();
        } else {
            orden.aprobar();
            List<Boleto> boletos = emitirBoletos(orden, ahora);
            boletoRepository.guardarTodos(boletos);
            facturacion.emitirFactura(orden);
            for (NotificacionPort notificador : notificadores) {
                notificador.enviarBoletos(orden.getCliente(), boletos);
            }
        }
        ordenRepository.guardar(orden);
        return orden;
    }

    @Override
    public int liberarReservasVencidas() {
        LocalDateTime ahora = LocalDateTime.now(reloj);
        int liberadas = 0;
        for (Orden orden : ordenRepository.buscarPendientes()) {
            if (orden.estaVencida(ahora)) {
                orden.expirar();
                ordenRepository.guardar(orden);
                liberadas++;
            }
        }
        return liberadas;
    }

    // un boleto por asiento, solo se llama cuando el pago ya fue verificado
    private List<Boleto> emitirBoletos(Orden orden, LocalDateTime ahora) {
        List<Boleto> boletos = new ArrayList<>();
        for (Asiento asiento : orden.getAsientos()) {
            String codigo = generadorBoleto.generarCodigoRedencion();
            String firma = generadorBoleto.generarFirma(Boleto.datosParaFirma(orden, asiento, codigo), orden.getEventoId());
            String qr = generadorBoleto.generarQr(codigo + "|" + firma);
            boletos.add(new Boleto(UUID.randomUUID().toString(), orden.getId(), asiento.getId(), asiento.getCodigo(),
                    codigo, firma, qr, ahora));
        }
        return boletos;
    }

    private PasarelaPagoPort buscarPasarela(MetodoPago metodo) {
        return pasarelas.stream()
                .filter(p -> p.getMetodo() == metodo)
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException("No hay pasarela para " + metodo));
    }
}
