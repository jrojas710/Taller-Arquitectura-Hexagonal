package co.edu.unab.stagepass.application.port.in;

import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.model.Cliente;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;

import java.util.List;

// Puerto primario: lo usan los adaptadores web (navegador / app movil)
public interface CompraUseCase {

    Orden iniciarCompra(String eventoId, Cliente cliente, List<String> asientoIds, MetodoPago metodoPago);

    List<Asiento> consultarAsientos(String eventoId);

    Orden consultarOrden(String ordenId);
}
