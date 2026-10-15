package co.edu.unab.stagepass.infrastructure.adapter.out.mongo;

import co.edu.unab.stagepass.application.port.out.BoletoRepository;
import co.edu.unab.stagepass.domain.model.Boleto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Adaptador secundario: boletos emitidos en MongoDB
@Component
public class MongoBoletoAdapter implements BoletoRepository {

    private final BoletoMongoRepository repo;

    public MongoBoletoAdapter(BoletoMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public void guardarTodos(List<Boleto> boletos) {
        repo.saveAll(boletos.stream().map(BoletoDocument::desdeDominio).toList());
    }

    @Override
    public List<Boleto> buscarPorOrden(String ordenId) {
        return repo.findByOrdenId(ordenId).stream().map(BoletoDocument::aDominio).toList();
    }

    @Override
    public Optional<Boleto> buscarPorCodigo(String codigoRedencion) {
        return repo.findByCodigoRedencion(codigoRedencion).map(BoletoDocument::aDominio);
    }
}
