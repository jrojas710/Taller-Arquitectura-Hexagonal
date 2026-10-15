package co.edu.unab.stagepass.infrastructure.adapter.out.mongo;

import co.edu.unab.stagepass.application.port.out.OrdenRepository;
import co.edu.unab.stagepass.domain.model.Orden;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Adaptador secundario: implementa el puerto OrdenRepository usando MongoDB
@Component
public class MongoOrdenAdapter implements OrdenRepository {

    private final OrdenMongoRepository repo;

    public MongoOrdenAdapter(OrdenMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public void guardar(Orden orden) {
        repo.save(OrdenDocument.desdeDominio(orden));
    }

    @Override
    public Optional<Orden> buscarPorId(String ordenId) {
        return repo.findById(ordenId).map(OrdenDocument::aDominio);
    }

    @Override
    public Optional<Orden> buscarPorReferenciaPago(String referenciaPago) {
        return repo.findByReferenciaPago(referenciaPago).map(OrdenDocument::aDominio);
    }

    @Override
    public List<Orden> buscarPorEvento(String eventoId) {
        return repo.findByEventoId(eventoId).stream().map(OrdenDocument::aDominio).toList();
    }

    @Override
    public List<Orden> buscarPendientes() {
        return repo.findByEstado(Orden.Estado.PENDIENTE_PAGO.name()).stream().map(OrdenDocument::aDominio).toList();
    }
}
