package co.edu.unab.stagepass;

import co.edu.unab.stagepass.application.service.CompraService;
import co.edu.unab.stagepass.domain.exception.ReglaNegocioException;
import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.model.Cliente;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompraServiceTest {

    static final Instant AHORA = Instant.parse("2026-11-01T15:00:00Z");

    Fakes.RecintosEnMemoria recintos;
    Fakes.OrdenesEnMemoria ordenes;
    CompraService compra;

    Cliente ana = new Cliente("C1", "Ana", "ana@correo.co", "3001234567", "Colombiana");
    Cliente luis = new Cliente("C2", "Luis", "luis@correo.co", "3009876543", "Colombiana");

    @BeforeEach
    void setUp() {
        recintos = new Fakes.RecintosEnMemoria();
        ordenes = new Fakes.OrdenesEnMemoria();
        compra = servicioEn(AHORA);
    }

    // permite "adelantar el reloj" para probar el bloqueo de 10 minutos
    CompraService servicioEn(Instant instante) {
        Clock reloj = Clock.fixed(instante, ZoneId.of("America/Bogota"));
        return new CompraService(recintos, ordenes, List.of(new Fakes.PasarelaFalsa()), new CalculadoraTarifas(), reloj);
    }

    @Test
    void compraQuedaPendienteConBloqueoDe10Minutos() {
        Orden orden = compra.iniciarCompra("E1", ana, List.of("A1", "A2", "A3"), MetodoPago.WOMPI);

        assertEquals(Orden.Estado.PENDIENTE_PAGO, orden.getEstado());
        assertEquals(orden.getFechaCreacion().plusMinutes(10), orden.getExpiraEn());
        assertEquals("REF-" + orden.getId(), orden.getReferenciaPago());
        assertEquals(Asiento.Estado.BLOQUEADO, orden.getAsientos().get(0).getEstado());
    }

    @Test
    void noSePuedeComprarUnAsientoBloqueado() {
        compra.iniciarCompra("E1", ana, List.of("A1"), MetodoPago.WOMPI);
        assertThrows(ReglaNegocioException.class,
                () -> compra.iniciarCompra("E1", luis, List.of("A1"), MetodoPago.WOMPI));
    }

    @Test
    void despuesDe10MinutosElAsientoSeLibera() {
        compra.iniciarCompra("E1", ana, List.of("A1"), MetodoPago.WOMPI);
        CompraService once = servicioEn(AHORA.plusSeconds(11 * 60));
        Orden orden = once.iniciarCompra("E1", luis, List.of("A1"), MetodoPago.WOMPI);
        assertEquals(Orden.Estado.PENDIENTE_PAGO, orden.getEstado());
    }

    @Test
    void maximo6BoletasPorClienteEnUnEvento() {
        compra.iniciarCompra("E1", ana, List.of("A1", "A2", "A3", "A4"), MetodoPago.WOMPI);
        assertThrows(ReglaNegocioException.class,
                () -> compra.iniciarCompra("E1", ana, List.of("A5", "A6", "A7"), MetodoPago.WOMPI));
        // con 2 mas llega justo a 6, eso si se puede
        compra.iniciarCompra("E1", ana, List.of("A5", "A6"), MetodoPago.WOMPI);
    }

    @Test
    void noSeVendeSiElEventoEstaCancelado() {
        assertThrows(ReglaNegocioException.class,
                () -> compra.iniciarCompra("E4", ana, List.of("A1"), MetodoPago.WOMPI));
    }

    @Test
    void elAsientoDebeSerDelRecintoDelEvento() {
        assertThrows(ReglaNegocioException.class,
                () -> compra.iniciarCompra("E1", ana, List.of("B1"), MetodoPago.WOMPI));
    }

    @Test
    void elMapaDeAsientosMuestraLosBloqueados() {
        compra.iniciarCompra("E1", ana, List.of("A1"), MetodoPago.WOMPI);
        Asiento a1 = compra.consultarAsientos("E1").stream().filter(a -> a.getId().equals("A1")).findFirst().get();
        assertEquals(Asiento.Estado.BLOQUEADO, a1.getEstado());
    }
}
