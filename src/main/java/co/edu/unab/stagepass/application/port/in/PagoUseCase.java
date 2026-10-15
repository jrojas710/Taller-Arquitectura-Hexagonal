package co.edu.unab.stagepass.application.port.in;

import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;

// Puerto primario: lo usan el webhook de las pasarelas y la tarea programada
public interface PagoUseCase {

    Orden procesarNotificacion(MetodoPago metodoPago, String referenciaPago);

    int liberarReservasVencidas();
}
