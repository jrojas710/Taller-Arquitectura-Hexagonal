package co.edu.unab.stagepass.application.service;

import co.edu.unab.stagepass.application.port.in.BoletoUseCase;
import co.edu.unab.stagepass.application.port.out.BoletoRepository;
import co.edu.unab.stagepass.application.port.out.GeneradorBoletoPort;
import co.edu.unab.stagepass.application.port.out.OrdenRepository;
import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.model.Boleto;
import co.edu.unab.stagepass.domain.model.Orden;

import java.util.List;
import java.util.Optional;

public class BoletoService implements BoletoUseCase {

    private final BoletoRepository boletoRepository;
    private final OrdenRepository ordenRepository;
    private final GeneradorBoletoPort generadorBoleto;

    public BoletoService(BoletoRepository boletoRepository, OrdenRepository ordenRepository,
                         GeneradorBoletoPort generadorBoleto) {
        this.boletoRepository = boletoRepository;
        this.ordenRepository = ordenRepository;
        this.generadorBoleto = generadorBoleto;
    }

    @Override
    public List<Boleto> boletosDeOrden(String ordenId) {
        return boletoRepository.buscarPorOrden(ordenId);
    }

    // se recalcula la firma y se compara con la guardada en el boleto
    @Override
    public boolean validarBoleto(String codigoRedencion) {
        Optional<Boleto> boleto = boletoRepository.buscarPorCodigo(codigoRedencion);
        if (boleto.isEmpty()) {
            return false;
        }
        Optional<Orden> orden = ordenRepository.buscarPorId(boleto.get().getOrdenId());
        if (orden.isEmpty() || orden.get().getEstado() != Orden.Estado.APROBADA) {
            return false;
        }
        Optional<Asiento> asiento = orden.get().getAsientos().stream()
                .filter(a -> a.getId().equals(boleto.get().getAsientoId()))
                .findFirst();
        if (asiento.isEmpty()) {
            return false;
        }
        String firmaEsperada = generadorBoleto.generarFirma(
                Boleto.datosParaFirma(orden.get(), asiento.get(), codigoRedencion), orden.get().getEventoId());
        return firmaEsperada.equals(boleto.get().getFirmaHash());
    }
}
