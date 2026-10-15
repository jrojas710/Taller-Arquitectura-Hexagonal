package co.edu.unab.stagepass.domain.service;

import co.edu.unab.stagepass.domain.model.Asiento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Regla de descuento por volumen y tarifa de emision:
 *  1 a 2 boletos  -> tarifa 10%, descuento 0%
 *  3 a 5 boletos  -> tarifa 8%,  descuento 5%
 *  mas de 5       -> tarifa 5%,  descuento 10%
 * total = subtotal + tarifa - descuento
 */
public class CalculadoraTarifas {

    public record Liquidacion(BigDecimal subtotal, BigDecimal tarifa, BigDecimal descuento, BigDecimal total) {
    }

    public Liquidacion calcular(List<Asiento> asientos) {
        int cantidad = asientos.size();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Asiento a : asientos) {
            subtotal = subtotal.add(a.getPrecioBase());
        }

        BigDecimal porcTarifa;
        BigDecimal porcDescuento;
        if (cantidad <= 2) {
            porcTarifa = new BigDecimal("0.10");
            porcDescuento = BigDecimal.ZERO;
        } else if (cantidad <= 5) {
            porcTarifa = new BigDecimal("0.08");
            porcDescuento = new BigDecimal("0.05");
        } else {
            porcTarifa = new BigDecimal("0.05");
            porcDescuento = new BigDecimal("0.10");
        }

        BigDecimal tarifa = redondear(subtotal.multiply(porcTarifa));
        BigDecimal descuento = redondear(subtotal.multiply(porcDescuento));
        BigDecimal total = redondear(subtotal.add(tarifa).subtract(descuento));
        return new Liquidacion(redondear(subtotal), tarifa, descuento, total);
    }

    private BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
