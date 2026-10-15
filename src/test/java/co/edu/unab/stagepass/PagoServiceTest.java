package co.edu.unab.stagepass;

import co.edu.unab.stagepass.application.service.BoletoService;
import co.edu.unab.stagepass.application.service.CompraService;
import co.edu.unab.stagepass.application.service.PagoService;
import co.edu.unab.stagepass.domain.model.Boleto;
import co.edu.unab.stagepass.domain.model.Cliente;
import co.edu.unab.stagepass.domain.model.EstadoPago;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;
import co.edu.unab.stagepass.domain.service.CalculadoraTarifas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PagoServiceTest {

    static final Instant AHORA = Instant.parse("2026-11-01T15:00:00Z");

    Fakes.OrdenesEnMemoria ordenes = new Fakes.OrdenesEnMemoria();
    Fakes.BoletosEnMemoria boletos = new Fakes.BoletosEnMemoria();
    Fakes.PasarelaFalsa pasarela = new Fakes.PasarelaFalsa();
    Fakes.GeneradorFalso generador = new Fakes.GeneradorFalso();
    int facturas = 0;
    int notificaciones = 0;

    Orden orden;

    @BeforeEach
    void setUp() {
        CompraService compra = new CompraService(new Fakes.RecintosEnMemoria(), ordenes, List.of(pasarela),
                new CalculadoraTarifas(), reloj(AHORA));
        Cliente ana = new Cliente("C1", "Ana", "ana@correo.co", "3001234567", "Colombiana");
        orden = compra.iniciarCompra("E1", ana, List.of("A1", "A2"), MetodoPago.WOMPI);
    }

    Clock reloj(Instant instante) {
        return Clock.fixed(instante, ZoneId.of("America/Bogota"));
    }

    PagoService pagosEn(Instant instante) {
        return new PagoService(ordenes, boletos, List.of(pasarela), generador, o -> "F" + (++facturas),
                List.of((cliente, b) -> notificaciones++), reloj(instante));
    }

    @Test
    void pagoAprobadoEmiteUnBoletoPorAsiento() {
        Orden resultado = pagosEn(AHORA.plusSeconds(120)).procesarNotificacion(MetodoPago.WOMPI, orden.getReferenciaPago());

        assertEquals(Orden.Estado.APROBADA, resultado.getEstado());
        assertEquals(2, boletos.boletos.size());
        assertEquals(1, facturas);
        assertEquals(1, notificaciones);
    }

    @Test
    void pagoRechazadoNoEmiteBoletos() {
        pasarela.respuesta = EstadoPago.RECHAZADO;
        Orden resultado = pagosEn(AHORA.plusSeconds(120)).procesarNotificacion(MetodoPago.WOMPI, orden.getReferenciaPago());

        assertEquals(Orden.Estado.RECHAZADA, resultado.getEstado());
        assertTrue(boletos.boletos.isEmpty());
    }

    @Test
    void pagoQueLlegaDespuesDe10MinutosExpiraLaOrden() {
        Orden resultado = pagosEn(AHORA.plusSeconds(15 * 60)).procesarNotificacion(MetodoPago.WOMPI, orden.getReferenciaPago());

        assertEquals(Orden.Estado.EXPIRADA, resultado.getEstado());
        assertTrue(boletos.boletos.isEmpty());
    }

    @Test
    void laTareaProgramadaLiberaLasOrdenesVencidas() {
        assertEquals(0, pagosEn(AHORA.plusSeconds(5 * 60)).liberarReservasVencidas());
        assertEquals(1, pagosEn(AHORA.plusSeconds(10 * 60)).liberarReservasVencidas());
        assertEquals(Orden.Estado.EXPIRADA, orden.getEstado());
    }

    @Test
    void validarBoleto() {
        pagosEn(AHORA.plusSeconds(60)).procesarNotificacion(MetodoPago.WOMPI, orden.getReferenciaPago());
        BoletoService boletoService = new BoletoService(boletos, ordenes, generador);
        Boleto boleto = boletos.boletos.get(0);

        assertTrue(boletoService.validarBoleto(boleto.getCodigoRedencion()));
        assertFalse(boletoService.validarBoleto("CODIGO-FALSO"));
    }
}
