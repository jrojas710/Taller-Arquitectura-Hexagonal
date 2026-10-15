package co.edu.unab.stagepass.infrastructure.adapter.out.pago;

import co.edu.unab.stagepass.application.port.out.PasarelaPagoPort;
import co.edu.unab.stagepass.domain.model.EstadoPago;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

// MOCK de PayPal. PayPal no maneja pesos, entonces el adaptador convierte a dolares
@Component
public class PayPalAdapter implements PasarelaPagoPort {

    private static final BigDecimal TASA_COP_USD = new BigDecimal("4000");

    @Override
    public MetodoPago getMetodo() {
        return MetodoPago.PAYPAL;
    }

    @Override
    public String registrarPago(Orden orden) {
        BigDecimal usd = orden.getTotal().divide(TASA_COP_USD, 2, RoundingMode.HALF_UP);
        String referencia = "PAYPAL-" + orden.getId();
        System.out.println("[MOCK PAYPAL] orden " + referencia + " por USD " + usd);
        return referencia;
    }

    @Override
    public EstadoPago consultarEstado(String referenciaPago) {
        return referenciaPago.startsWith("PAYPAL-") ? EstadoPago.APROBADO : EstadoPago.RECHAZADO;
    }
}
