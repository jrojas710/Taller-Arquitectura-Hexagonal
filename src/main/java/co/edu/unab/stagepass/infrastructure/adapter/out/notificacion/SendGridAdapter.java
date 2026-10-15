package co.edu.unab.stagepass.infrastructure.adapter.out.notificacion;

import co.edu.unab.stagepass.application.port.out.NotificacionPort;
import co.edu.unab.stagepass.domain.model.Boleto;
import co.edu.unab.stagepass.domain.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.List;

// MOCK: solo imprime el correo que se mandaria con Sendgrid
@Component
public class SendGridAdapter implements NotificacionPort {

    @Override
    public void enviarBoletos(Cliente cliente, List<Boleto> boletos) {
        StringBuilder sb = new StringBuilder();
        for (Boleto b : boletos) {
            sb.append("\n   - ").append(b.getAsientoCodigo()).append(" codigo ").append(b.getCodigoRedencion());
        }
        System.out.println("[MOCK SENDGRID] correo a " + cliente.getCorreo() + " con tus boletas:" + sb);
    }
}
