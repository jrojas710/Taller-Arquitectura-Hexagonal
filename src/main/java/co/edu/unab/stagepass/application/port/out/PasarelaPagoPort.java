package co.edu.unab.stagepass.application.port.out;

import co.edu.unab.stagepass.domain.model.EstadoPago;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;

// Puerto comun para todas las pasarelas (PayPal, Wompi, PSE)
public interface PasarelaPagoPort {

    MetodoPago getMetodo();

    // registra el pago y devuelve la referencia con la que la pasarela va a notificar
    String registrarPago(Orden orden);

    // consulta a la pasarela el estado real del pago (no se confia solo en el webhook)
    EstadoPago consultarEstado(String referenciaPago);
}
