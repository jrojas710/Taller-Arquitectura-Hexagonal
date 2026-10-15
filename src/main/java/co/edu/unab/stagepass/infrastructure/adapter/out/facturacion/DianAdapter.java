package co.edu.unab.stagepass.infrastructure.adapter.out.facturacion;

import co.edu.unab.stagepass.application.port.out.FacturacionPort;
import co.edu.unab.stagepass.domain.model.Orden;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

// MOCK de facturacion electronica DIAN
@Component
public class DianAdapter implements FacturacionPort {

    private final AtomicInteger consecutivo = new AtomicInteger(1000);

    @Override
    public String emitirFactura(Orden orden) {
        String numero = "SETP" + consecutivo.incrementAndGet();
        System.out.println("[MOCK DIAN] factura " + numero + " por $" + orden.getTotal() + " (orden " + orden.getId() + ")");
        return numero;
    }
}
