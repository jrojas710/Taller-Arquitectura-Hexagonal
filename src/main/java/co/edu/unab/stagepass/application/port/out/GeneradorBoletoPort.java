package co.edu.unab.stagepass.application.port.out;

// Puerto secundario para la parte criptografica y grafica de los boletos
public interface GeneradorBoletoPort {

    String generarCodigoRedencion();

    // hash de los datos de la orden usando la clave secreta del evento
    String generarFirma(String datos, String eventoId);

    // representacion grafica (QR) del boleto
    String generarQr(String contenido);
}
