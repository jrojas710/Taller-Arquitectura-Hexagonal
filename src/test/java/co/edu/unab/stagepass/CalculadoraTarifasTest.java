package co.edu.unab.stagepass;

import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.service.CalculadoraTarifas;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraTarifasTest {

    CalculadoraTarifas calculadora = new CalculadoraTarifas();

    List<Asiento> asientos(int cantidad) {
        List<Asiento> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            lista.add(new Asiento("A" + i, "R1", "VIP-" + i, new BigDecimal("100000"), Asiento.Estado.DISPONIBLE));
        }
        return lista;
    }

    @Test
    void dosBoletosTarifa10SinDescuento() {
        CalculadoraTarifas.Liquidacion l = calculadora.calcular(asientos(2));
        assertEquals(new BigDecimal("20000.00"), l.tarifa());
        assertEquals(new BigDecimal("0.00"), l.descuento());
        assertEquals(new BigDecimal("220000.00"), l.total());
    }

    @Test
    void tresBoletosTarifa8Descuento5() {
        CalculadoraTarifas.Liquidacion l = calculadora.calcular(asientos(3));
        assertEquals(new BigDecimal("24000.00"), l.tarifa());
        assertEquals(new BigDecimal("15000.00"), l.descuento());
        assertEquals(new BigDecimal("309000.00"), l.total());
    }

    @Test
    void seisBoletosCorporativo() {
        CalculadoraTarifas.Liquidacion l = calculadora.calcular(asientos(6));
        assertEquals(new BigDecimal("30000.00"), l.tarifa());
        assertEquals(new BigDecimal("60000.00"), l.descuento());
        assertEquals(new BigDecimal("570000.00"), l.total());
    }
}
