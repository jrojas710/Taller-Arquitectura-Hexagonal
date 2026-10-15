package co.edu.unab.stagepass.infrastructure.adapter.in.web;

import co.edu.unab.stagepass.domain.exception.NoEncontradoException;
import co.edu.unab.stagepass.domain.exception.ReglaNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// Convierte las excepciones del dominio a respuestas HTTP
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(NoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> reglaNegocio(ReglaNegocioException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    // JSON mal armado, metodo de pago que no existe, etc.
    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class, ClassCastException.class})
    public ResponseEntity<Map<String, String>> peticionInvalida(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Peticion invalida: " + e.getMessage()));
    }
}
