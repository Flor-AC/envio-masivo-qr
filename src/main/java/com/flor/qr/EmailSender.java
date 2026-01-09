package com.flor.qr;

import java.io.File;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailSender {

    //Jalamos las credenciales de nuestro path
    private final String remitente = System.getenv("MAIL_USER");
    private final String password = System.getenv("MAIL_PASS");

    public void enviarCorreoAdjunto(Cliente cliente, String rutaCarpetaPDFs) {
        //Si no configuraste el entrono, el programa se detiene
        if (remitente == null || password == null) {
            System.err.println("ERROR: No se detectaron las variables MAIL_USER o MAIL_PASS");
            return;
        }

        //Configuración SMTP (carga desde config.properties)
        Properties props = new Properties();
        props.put("mail.smtp.auth", Config.get("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", Config.get("mail.smtp.starttls.enable"));
        props.put("mail.smtp.host", Config.get("mail.smtp.host"));
        props.put("mail.smtp.port", Config.get("mail.smtp.port"));

        //Sesión y autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, password);
            }
        });

        try {
            //Creación de mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(cliente.getCorreo()));
            message.setSubject(Config.get("mail.asunto"));

            //Cuerpo del mensaje
            MimeBodyPart cuerpoHtml = new MimeBodyPart();
            String contenidoHtml
                    = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 700px;'>"
                    + "<p>Estimado(a) <b>" + cliente.getNombre() + "</b></p>"
                    + "<p>Reciba un cordial saludo.</p>"
                    + "<p>En el marco del <b>Reciclatón Cancún 2026</b>, la Dirección General de Ecología del Municipio de Benito Juárez implementa un nuevo proceso digital para el registro y trazabilidad de los residuos, con el objetivo de fortalecer la transparencia, el control y la generación de información confiable en materia ambiental.</p>"
                    + "<p>Como parte de este proceso, cada empresa / establecimiento contará con un <b>Código QR único</b>, mediante el cual se registrará de manera digital cada llegada de residuos a los centros de acopio autorizados durante las jornadas del Reciclatón.</p>"
                    + "<p>El uso del Código QR permitirá:</p>"
                    + "<ul>"
                    + "<li>Registrar de forma ordenada cada entrega de residuos</li>"
                    + "<li>Garantizar la trazabilidad del material recibido</li>"
                    + "<li>Contar con información estadística confiable para la toma de decisiones</li>"
                    + "</ul>"
                    + "<p>Adjunto a este correo encontrará su Código QR asignado, el cual deberá ser presentado en cada entrega de residuos, ya sea en formato digital o impreso, para que el personal operativo realice el registro correspondiente.</p>"
                    + "<p>Le solicitamos atentamente:</p>"
                    + "<ul>"
                    + "<li>Descargar y conservar su Código QR</li>"
                    + "<li>Presentarlo en cada visita a los centros de acopio</li>"
                    + "<li>Verificar que la información registrada corresponda a su empresa</li>"
                    + "</ul>"
                    + "<p><b>LINK DE DESCARGA</b><br>"
                    + "<a href='https://drive.google.com/file/d/16HRedMJK_v-DmmL1iTdJqECdSu9-smMd/view?usp=drive_link' style='color: #007bff;'>Ver/Descargar Código QR General</a></p>"
                    + "<p>Para cualquier duda, aclaración o apoyo relacionado con el uso del Código QR o el proceso de registro, ponemos a su disposición los canales de contacto de la Dirección General de Ecología.</p>"
                    + "<p>Agradecemos su colaboración y compromiso con una gestión de residuos responsable, ordenada y alineada a los principios de la economía circular.</p>"
                    + "<p>Atentamente,<br><br>---------------------<br><b>Dirección General de Ecología</b><br>"
                    + "Municipio de Benito Juárez<br>Reciclatón Cancún 2026<br>9988812800 Ext. 3100</p>"
                    + "<br><br>"
                    + "<div style='font-size: 11px; color: #666; border-top: 1px solid #ccc; padding-top: 10px;'>"
                    + "<p><b>AVISO DE PRIVACIDAD SIMPLIFICADO</b><br>"
                    + "En cumplimiento a la Ley General de Protección de Datos Personales en Posesión de Sujetos Obligados y la Ley de Protección de Datos Personales en Posesión de Sujetos Obligados para el Estado de Quintana Roo, y el Reglamento de Protección de Datos Personales en Posesión de Sujetos Obligados para el Municipio Benito Juárez, Quintana Roo, la Dirección General de Ecología del Municipio de Benito Juárez, Quintana Roo, en su calidad de sujeto obligado informa que es el responsable del tratamiento de sus Datos Personales que nos proporcione, los cuales serán debidamente protegidos de conformidad con lo dispuestos en los citados ordenamientos y demás que resulten aplicables. Podrá consultar nuestro aviso de privacidad integral en el sitio web del H. Ayuntamiento de Benito Juárez, Quintana Roo: www.cancun.gob.mx/transparencia/privacidad/ o bien, de manera presencial en la carpeta de Avisos de Privacidad de la Dirección General de Ecología.</p>"
                    + "<p>Av. Nader No. 8 SM. 5 MZA. 03, Plaza Centro, Planta Baja, C.P. 77500, Benito Juárez, Quintana Roo. Teléfono 881 2800 Ext 3100</p>"
                    + "</div>"
                    + "</body></html>";

            cuerpoHtml.setContent(contenidoHtml, "text/html; charset=utf-8");

            // Manejo de archivo adjunto
            MimeBodyPart adjunto = new MimeBodyPart();
            File archivoPdf = new File(rutaCarpetaPDFs, cliente.getReferencia() + ".pdf");

            if (archivoPdf.exists()) {
                adjunto.attachFile(archivoPdf);
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(cuerpoHtml);
                multipart.addBodyPart(adjunto);
                message.setContent(multipart);

                // Envío con Microsoft 365
                Transport.send(message);
                System.out.println("✅ Enviado: " + cliente.getCorreo() + " [" + cliente.getReferencia() + "]");
            } else {
                System.err.println("❌ ERROR: No se encontró el PDF para: " + cliente.getReferencia());
            }
        } catch (Exception e) {
            System.err.println("❌ Error enviando a " + cliente.getCorreo() + ": " + e.getMessage());
        }
    }

}
