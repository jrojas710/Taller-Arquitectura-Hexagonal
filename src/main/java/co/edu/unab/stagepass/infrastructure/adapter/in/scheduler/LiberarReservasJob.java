package co.edu.unab.stagepass.infrastructure.adapter.in.scheduler;

import co.edu.unab.stagepass.application.port.in.PagoUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Adaptador primario: cada minuto libera los asientos de las ordenes que pasaron los 10 minutos sin pago
@Component
public class LiberarReservasJob {

    private final PagoUseCase pagoUseCase;

    public LiberarReservasJob(PagoUseCase pagoUseCase) {
        this.pagoUseCase = pagoUseCase;
    }

    @Scheduled(fixedRate = 60000)
    public void ejecutar() {
        int liberadas = pagoUseCase.liberarReservasVencidas();
        if (liberadas > 0) {
            System.out.println("Ordenes expiradas: " + liberadas);
        }
    }
}
