package com.flor.qr;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Formato de fecha para el log
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            System.out.println("--- INICIANDO SISTEMA CON LOG DE SEGUIMIENTO ---");

            List<Cliente> clientes = ExcelReader.leerClientes(Config.get("path.excel"));
            EmailSender sender = new EmailSender();
            String rutaLog = Config.get("path.log");

            // Abrimos el archivo de log en modo "append" (añadir al final)
            try (PrintWriter logWriter = new PrintWriter(new FileWriter(rutaLog, true))) {
                
                // Si el archivo es nuevo, escribimos el encabezado
                logWriter.println("Fecha,Referencia,Nombre,Correo,Estado,Observaciones");

                for (int i = 0; i < clientes.size(); i++) {
                    Cliente c = clientes.get(i);
                    String fechaActual = dtf.format(LocalDateTime.now());
                    
                    try {
                        // Intentamos enviar
                        sender.enviarCorreoAdjunto(c, Config.get("path.pdfs"));
                        
                        // Si no hubo excepción, registramos éxito
                        logWriter.println(fechaActual + "," + c.getReferencia() + "," + c.getNombre() + "," + c.getCorreo() + ",ENVIADO,OK");
                        System.out.println("✅ Log actualizado para: " + c.getReferencia());

                    } catch (Exception e) {
                        // Si hubo error, registramos el motivo
                        logWriter.println(fechaActual + "," + c.getReferencia() + "," + c.getNombre() + "," + c.getCorreo() + ",ERROR," + e.getMessage());
                        System.err.println("❌ Error registrado en log para: " + c.getReferencia());
                    }

                    // Flush asegura que se escriba en el disco de inmediato
                    logWriter.flush();

                    // Pausa de seguridad de 15 segundos para Microsoft 365
                    if (i < clientes.size() - 1) Thread.sleep(Integer.parseInt(Config.get("pausa.milisegundos")));
                }
            }

            System.out.println("--- PROCESO TERMINADO. REVISE EL ARCHIVO CSV PARA SEGUIMIENTO ---");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}