package co.edu.unab.stagepass.application.port.out;

import co.edu.unab.stagepass.domain.model.Orden;

import java.util.List;
import java.util.Optional;

// Puerto secundario: reservas / ordenes (MongoDB)
public interface OrdenRepository {

    void guardar(Orden orden);

    Optional<Orden> buscarPorId(String ordenId);

    Optional<Orden> buscarPorReferenciaPago(String referenciaPago);

    List<Orden> buscarPorEvento(String eventoId);

    List<Orden> buscarPendientes();
}
