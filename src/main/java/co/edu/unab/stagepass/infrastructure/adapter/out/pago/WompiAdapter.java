package co.edu.unab.stagepass.infrastructure.adapter.out.pago;

import co.edu.unab.stagepass.application.port.out.PasarelaPagoPort;
import co.edu.unab.stagepass.domain.model.EstadoPago;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;
import org.springframework.stereotype.Component;

// MOCK de Wompi. En produccion aca se llamaria al API (POST /v1/transactions y GET /v1/transactions/{id})
@Component
public class WompiAdapter implements PasarelaPagoPort {

    @Override
    public MetodoPago getMetodo() {
        return MetodoPago.WOMPI;
    }

    @Override
    public String registrarPago(Orden orden) {
        // Wompi recibe el valor en centavos
        long centavos = orden.getTotal().movePointRight(2).longValue();
        String referencia = "WOMPI-" + orden.getId();
        System.out.println("[MOCK WOMPI] transaccion " + referencia + " por " + centavos + " centavos COP");
        return referencia;
    }

    @Override
    public EstadoPago consultarEstado(String referenciaPago) {
        // simulacion: las referencias creadas por este adaptador salen aprobadas
        return referenciaPago.startsWith("WOMPI-") ? EstadoPago.APROBADO : EstadoPago.RECHAZADO;
    }
}
