package co.edu.unab.stagepass.infrastructure.adapter.out.seguridad;

import co.edu.unab.stagepass.application.port.out.GeneradorBoletoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

// Firma de los boletos con SHA-256 (viene en el JDK, no necesita librerias)
// La clave del evento = clave secreta del sistema + id del evento
@Component
public class GeneradorBoletoAdapter implements GeneradorBoletoPort {

    private final String claveSecreta;

    public GeneradorBoletoAdapter(@Value("${stagepass.clave-secreta}") String claveSecreta) {
        this.claveSecreta = claveSecreta;
    }

    @Override
    public String generarCodigoRedencion() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    @Override
    public String generarFirma(String datos, String eventoId) {
        String claveEvento = claveSecreta + "-" + eventoId;
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha.digest((datos + "|" + claveEvento).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Para el QR usamos el servicio publico de qrserver: devuelve la URL de la imagen
    @Override
    public String generarQr(String contenido) {
        return "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data="
                + URLEncoder.encode(contenido, StandardCharsets.UTF_8);
    }
}
