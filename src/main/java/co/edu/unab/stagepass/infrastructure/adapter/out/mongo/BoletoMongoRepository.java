package co.edu.unab.stagepass.infrastructure.adapter.out.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BoletoMongoRepository extends MongoRepository<BoletoDocument, String> {

    List<BoletoDocument> findByOrdenId(String ordenId);

    Optional<BoletoDocument> findByCodigoRedencion(String codigoRedencion);
}
