package co.edu.unab.stagepass.infrastructure.adapter.out.pago;

import co.edu.unab.stagepass.application.port.out.PasarelaPagoPort;
import co.edu.unab.stagepass.domain.model.EstadoPago;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;
import org.springframework.stereotype.Component;

// MOCK de PSE (debito bancario)
@Component
public class PseAdapter implements PasarelaPagoPort {

    @Override
    public MetodoPago getMetodo() {
        return MetodoPago.PSE;
    }

    @Override
    public String registrarPago(Orden orden) {
        String referencia = "PSE-" + orden.getId();
        System.out.println("[MOCK PSE] transaccion " + referencia + " por $" + orden.getTotal());
        return referencia;
    }

    @Override
    public EstadoPago consultarEstado(String referenciaPago) {
        return referenciaPago.startsWith("PSE-") ? EstadoPago.APROBADO : EstadoPago.RECHAZADO;
    }
}
