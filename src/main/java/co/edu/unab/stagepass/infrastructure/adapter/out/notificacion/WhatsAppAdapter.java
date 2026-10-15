package co.edu.unab.stagepass.infrastructure.adapter.out.notificacion;

import co.edu.unab.stagepass.application.port.out.NotificacionPort;
import co.edu.unab.stagepass.domain.model.Boleto;
import co.edu.unab.stagepass.domain.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.List;

// MOCK: solo imprime el mensaje que se mandaria por WhatsApp
@Component
public class WhatsAppAdapter implements NotificacionPort {

    @Override
    public void enviarBoletos(Cliente cliente, List<Boleto> boletos) {
        System.out.println("[MOCK WHATSAPP] a " + cliente.getTelefono() + ": Hola " + cliente.getNombre()
                + ", tu pago fue aprobado. Tienes " + boletos.size() + " boleta(s).");
    }
}
