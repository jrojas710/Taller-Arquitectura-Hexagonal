package co.edu.unab.stagepass.infrastructure.adapter.out.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrdenMongoRepository extends MongoRepository<OrdenDocument, String> {

    Optional<OrdenDocument> findByReferenciaPago(String referenciaPago);

    List<OrdenDocument> findByEventoId(String eventoId);

    List<OrdenDocument> findByEstado(String estado);
}
