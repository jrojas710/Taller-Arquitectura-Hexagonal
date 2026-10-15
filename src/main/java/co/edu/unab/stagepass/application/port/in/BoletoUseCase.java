package co.edu.unab.stagepass.application.port.in;

import co.edu.unab.stagepass.domain.model.Boleto;

import java.util.List;

// Puerto primario: consulta y validacion de boletos
public interface BoletoUseCase {

    List<Boleto> boletosDeOrden(String ordenId);

    boolean validarBoleto(String codigoRedencion);
}
