package co.edu.unab.stagepass.application.port.out;

import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.model.Evento;

import java.util.List;
import java.util.Optional;

// Puerto secundario: consulta de eventos, recintos y asientos (base legacy MySQL, solo lectura)
public interface RecintoRepository {

    Optional<Evento> buscarEvento(String eventoId);

    List<Asiento> buscarAsientosDeRecinto(String recintoId);

    List<Asiento> buscarAsientos(List<String> asientoIds);
}
