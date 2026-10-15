package co.edu.unab.stagepass.infrastructure.config;

import co.edu.unab.stagepass.application.port.out.BoletoRepository;
import co.edu.unab.stagepass.application.port.out.FacturacionPort;
import co.edu.unab.stagepass.application.port.out.GeneradorBoletoPort;
import co.edu.unab.stagepass.application.port.out.NotificacionPort;
import co.edu.unab.stagepass.application.port.out.OrdenRepository;
import co.edu.unab.stagepass.application.port.out.PasarelaPagoPort;
import co.edu.unab.stagepass.application.port.out.RecintoRepository;
import co.edu.unab.stagepass.application.service.BoletoService;
import co.edu.unab.stagepass.application.service.CompraService;
import co.edu.unab.stagepass.application.service.PagoService;
import co.edu.unab.stagepass.domain.service.CalculadoraTarifas;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

// Las clases del nucleo no tienen anotaciones de Spring, aca se crean y se les inyectan los adaptadores
@Configuration
public class BeanConfig {

    @Bean
    public Clock reloj() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public CalculadoraTarifas calculadoraTarifas() {
        return new CalculadoraTarifas();
    }

    @Bean
    public CompraService compraService(RecintoRepository recintoRepository, OrdenRepository ordenRepository,
                                       List<PasarelaPagoPort> pasarelas, CalculadoraTarifas calculadora, Clock reloj) {
        return new CompraService(recintoRepository, ordenRepository, pasarelas, calculadora, reloj);
    }

    @Bean
    public PagoService pagoService(OrdenRepository ordenRepository, BoletoRepository boletoRepository,
                                   List<PasarelaPagoPort> pasarelas, GeneradorBoletoPort generadorBoleto,
                                   FacturacionPort facturacion, List<NotificacionPort> notificadores, Clock reloj) {
        return new PagoService(ordenRepository, boletoRepository, pasarelas, generadorBoleto, facturacion,
                notificadores, reloj);
    }

    @Bean
    public BoletoService boletoService(BoletoRepository boletoRepository, OrdenRepository ordenRepository,
                                       GeneradorBoletoPort generadorBoleto) {
        return new BoletoService(boletoRepository, ordenRepository, generadorBoleto);
    }
}
