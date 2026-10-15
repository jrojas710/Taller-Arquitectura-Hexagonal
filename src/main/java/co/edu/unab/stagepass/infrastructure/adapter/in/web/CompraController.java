package co.edu.unab.stagepass.infrastructure.adapter.in.web;

import co.edu.unab.stagepass.application.port.in.BoletoUseCase;
import co.edu.unab.stagepass.application.port.in.CompraUseCase;
import co.edu.unab.stagepass.domain.model.Asiento;
import co.edu.unab.stagepass.domain.model.Boleto;
import co.edu.unab.stagepass.domain.model.Cliente;
import co.edu.unab.stagepass.domain.model.MetodoPago;
import co.edu.unab.stagepass.domain.model.Orden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// Adaptador primario: recibe las peticiones del navegador / app movil
@RestController
@RequestMapping("/api")
public class CompraController {

    public record CompraRequest(String eventoId, String clienteId, String nombre, String correo, String telefono,
                                String nacionalidad, List<String> asientoIds, String metodoPago) {
    }

    public record OrdenResponse(Orden orden, List<Boleto> boletos) {
    }

    private final CompraUseCase compraUseCase;
    private final BoletoUseCase boletoUseCase;

    public CompraController(CompraUseCase compraUseCase, BoletoUseCase boletoUseCase) {
        this.compraUseCase = compraUseCase;
        this.boletoUseCase = boletoUseCase;
    }

    @GetMapping("/eventos/{eventoId}/asientos")
    public List<Asiento> asientos(@PathVariable String eventoId) {
        return compraUseCase.consultarAsientos(eventoId);
    }

    @PostMapping("/ordenes")
    public ResponseEntity<Orden> iniciarCompra(@RequestBody CompraRequest req) {
        Cliente cliente = new Cliente(req.clienteId(), req.nombre(), req.correo(), req.telefono(), req.nacionalidad());
        MetodoPago metodo = MetodoPago.valueOf(String.valueOf(req.metodoPago()).toUpperCase());
        Orden orden = compraUseCase.iniciarCompra(req.eventoId(), cliente, req.asientoIds(), metodo);
        return ResponseEntity.status(HttpStatus.CREATED).body(orden);
    }

    @GetMapping("/ordenes/{ordenId}")
    public OrdenResponse consultarOrden(@PathVariable String ordenId) {
        return new OrdenResponse(compraUseCase.consultarOrden(ordenId), boletoUseCase.boletosDeOrden(ordenId));
    }

    @PostMapping("/boletos/validar")
    public Map<String, Object> validarBoleto(@RequestBody Map<String, String> body) {
        String codigo = body.get("codigoRedencion");
        return Map.of("codigoRedencion", String.valueOf(codigo), "valido", boletoUseCase.validarBoleto(codigo));
    }
}
