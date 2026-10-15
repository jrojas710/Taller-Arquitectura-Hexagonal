package co.edu.unab.stagepass.infrastructure.adapter.in.web;

import co.edu.unab.stagepass.application.port.in.PagoUseCase;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Adaptador primario: endpoints para las notificaciones asincronas de las pasarelas.
// Cada pasarela manda un JSON diferente, aca se saca la referencia y se llama al mismo puerto.
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final PagoUseCase pagoUseCase;

    public WebhookController(PagoUseCase pagoUseCase) {
        this.pagoUseCase = pagoUseCase;
    }

    // {"event":"transaction.updated","data":{"transaction":{"reference":"...","status":"APPROVED"}}}
    @PostMapping("/wompi")
    @SuppressWarnings("unchecked")
    public Map<String, String> wompi(@RequestBody Map<String, Object> body) {
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        Map<String, Object> transaccion = (Map<String, Object>) data.get("transaction");
        String referencia = (String) transaccion.get("reference");
        return respuesta(pagoUseCase.procesarNotificacion(MetodoPago.WOMPI, referencia));
    }

    // {"event_type":"PAYMENT.CAPTURE.COMPLETED","resource":{"id":"...","custom_id":"..."}}
    @PostMapping("/paypal")
    @SuppressWarnings("unchecked")
    public Map<String, String> paypal(@RequestBody Map<String, Object> body) {
        Map<String, Object> resource = (Map<String, Object>) body.get("resource");
        String referencia = (String) resource.get("custom_id");
        return respuesta(pagoUseCase.procesarNotificacion(MetodoPago.PAYPAL, referencia));
    }

    // {"referencia":"...","estado":"OK"}
    @PostMapping("/pse")
    public Map<String, String> pse(@RequestBody Map<String, Object> body) {
        String referencia = (String) body.get("referencia");
        return respuesta(pagoUseCase.procesarNotificacion(MetodoPago.PSE, referencia));
    }

    private Map<String, String> respuesta(Orden orden) {
        return Map.of("ordenId", orden.getId(), "estado", orden.getEstado().name());
    }
}
