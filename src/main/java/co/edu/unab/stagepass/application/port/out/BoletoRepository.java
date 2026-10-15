package co.edu.unab.stagepass.application.port.out;

import co.edu.unab.stagepass.domain.model.Boleto;

import java.util.List;
import java.util.Optional;

// Puerto secundario: boletos emitidos (MongoDB)
public interface BoletoRepository {

    void guardarTodos(List<Boleto> boletos);

    List<Boleto> buscarPorOrden(String ordenId);

    Optional<Boleto> buscarPorCodigo(String codigoRedencion);
}
