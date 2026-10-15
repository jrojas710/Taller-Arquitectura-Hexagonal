package co.edu.unab.stagepass.application.port.out;

import co.edu.unab.stagepass.domain.model.Boleto;
import co.edu.unab.stagepass.domain.model.Cliente;

import java.util.List;

// Puerto secundario para avisar al cliente (WhatsApp, correo con Sendgrid)
public interface NotificacionPort {

    void enviarBoletos(Cliente cliente, List<Boleto> boletos);
}
