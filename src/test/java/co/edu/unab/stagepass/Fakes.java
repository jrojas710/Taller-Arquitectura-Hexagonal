package co.edu.unab.stagepass;

import co.edu.unab.stagepass.application.port.out.BoletoRepository;
import co.edu.unab.stagepass.application.port.out.GeneradorBoletoPort;
import co.edu.unab.stagepass.application.port.out.OrdenRepository;
import co.edu.unab.stagepass.application.port.out.PasarelaPagoPort;
import co.edu.unab.stagepass.application.port.out.RecintoRepository;
import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.model.Boleto;
import co.edu.unab.stagepass.domain.model.EstadoPago;
import co.edu.unab.stagepass.domain.model.Evento;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Implementaciones en memoria de los puertos para probar el nucleo sin MySQL ni Mongo
public class Fakes {

    public static class RecintosEnMemoria implements RecintoRepository {
        Map<String, Evento> eventos = new HashMap<>();
        Map<String, Asiento> asientos = new HashMap<>();

        public RecintosEnMemoria() {
            eventos.put("E1", new Evento("E1", "Noche de Rock", Evento.Tipo.CONCIERTO, "R1",
                    LocalDateTime.of(2026, 12, 5, 20, 0), Evento.Estado.PROGRAMADO));
            eventos.put("E4", new Evento("E4", "Cancelado", Evento.Tipo.CONCIERTO, "R1",
                    LocalDateTime.of(2026, 12, 12, 21, 0), Evento.Estado.CANCELADO));
            for (int i = 1; i <= 10; i++) {
                asientos.put("A" + i, new Asiento("A" + i, "R1", "VIP-A" + i, new BigDecimal("100000"), Asiento.Estado.DISPONIBLE));
            }
            asientos.put("B1", new Asiento("B1", "R2", "Platea-A1", new BigDecimal("50000"), Asiento.Estado.DISPONIBLE));
        }

        // devuelve copias, como si viniera de la base de datos
        private Asiento copia(Asiento a) {
            return new Asiento(a.getId(), a.getRecintoId(), a.getCodigo(), a.getPrecioBase(), a.getEstado());
        }

        public Optional<Evento> buscarEvento(String eventoId) {
            return Optional.ofNullable(eventos.get(eventoId));
        }

        public List<Asiento> buscarAsientosDeRecinto(String recintoId) {
            return asientos.values().stream().filter(a -> a.getRecintoId().equals(recintoId)).map(this::copia).toList();
        }

        public List<Asiento> buscarAsientos(List<String> ids) {
            return ids.stream().filter(asientos::containsKey).map(id -> copia(asientos.get(id))).toList();
        }
    }

    public static class OrdenesEnMemoria implements OrdenRepository {
        Map<String, Orden> ordenes = new HashMap<>();

        public void guardar(Orden orden) { ordenes.put(orden.getId(), orden); }

        public Optional<Orden> buscarPorId(String id) { return Optional.ofNullable(ordenes.get(id)); }

        public Optional<Orden> buscarPorReferenciaPago(String ref) {
            return ordenes.values().stream().filter(o -> ref.equals(o.getReferenciaPago())).findFirst();
        }

        public List<Orden> buscarPorEvento(String eventoId) {
            return ordenes.values().stream().filter(o -> o.getEventoId().equals(eventoId)).toList();
        }

        public List<Orden> buscarPendientes() {
            return ordenes.values().stream().filter(o -> o.getEstado() == Orden.Estado.PENDIENTE_PAGO).toList();
        }
    }

    public static class BoletosEnMemoria implements BoletoRepository {
        public List<Boleto> boletos = new ArrayList<>();

        public void guardarTodos(List<Boleto> nuevos) { boletos.addAll(nuevos); }

        public List<Boleto> buscarPorOrden(String ordenId) {
            return boletos.stream().filter(b -> b.getOrdenId().equals(ordenId)).toList();
        }

        public Optional<Boleto> buscarPorCodigo(String codigo) {
            return boletos.stream().filter(b -> b.getCodigoRedencion().equals(codigo)).findFirst();
        }
    }

    public static class PasarelaFalsa implements PasarelaPagoPort {
        public EstadoPago respuesta = EstadoPago.APROBADO;

        public MetodoPago getMetodo() { return MetodoPago.WOMPI; }

        public String registrarPago(Orden orden) { return "REF-" + orden.getId(); }

        public EstadoPago consultarEstado(String referencia) { return respuesta; }
    }

    public static class GeneradorFalso implements GeneradorBoletoPort {
        int contador = 0;

        public String generarCodigoRedencion() { return "COD" + (++contador); }

        public String generarFirma(String datos, String eventoId) { return Integer.toHexString((datos + eventoId).hashCode()); }

        public String generarQr(String contenido) { return "qr:" + contenido; }
    }
}
