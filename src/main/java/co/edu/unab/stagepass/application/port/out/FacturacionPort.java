package co.edu.unab.stagepass.application.port.out;

import co.edu.unab.stagepass.domain.model.Orden;

// Puerto secundario para la factura electronica (DIAN). Devuelve el numero de factura.
public interface FacturacionPort {

    String emitirFactura(Orden orden);
}
