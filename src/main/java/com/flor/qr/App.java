package com.flor.qr;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class App {

    public static void main(String[] args) {
        // Formato de fecha para el archivo de seguimiento (Log)
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            System.out.println("--- INICIANDO SISTEMA RECICLATÓN CANCÚN 2026 ---");

            // 1. VALIDACIÓN DE SEGURIDAD: Verificar carga de propiedades
            if (Config.get("path.excel") == null) {
                System.err.println("❌ ERROR CRÍTICO: No se pudo leer el archivo de configuración.");
                System.err.println("Asegúrate de que el archivo en resources se llame 'config.properties' (con 'ig').");
                return;
            }

            // 2. CARGA DE DATOS Y HERRAMIENTAS
            List<Cliente> clientes = ExcelReader.leerClientes(Config.get("path.excel"));
            EmailSender sender = new EmailSender();
            String rutaLog = Config.get("path.log");

            if (clientes.isEmpty()) {
                System.out.println("⚠️ No hay clientes activos para procesar en esta ejecución.");
                return;
            }

            System.out.println("📊 Clientes activos encontrados: " + clientes.size());

            // 3. PROCESO DE ENVÍO CON SEGUIMIENTO (LOG)
            // Abrimos el archivo CSV en modo 'append' para no borrar registros previos
            try (PrintWriter logWriter = new PrintWriter(new FileWriter(rutaLog, true))) {

                // Si el proceso es nuevo, puedes identificarlo en el archivo
                logWriter.println("--- Nueva Sesión de Envío: " + dtf.format(LocalDateTime.now()) + " ---");
                logWriter.println("Fecha,Referencia,Nombre,Correo,Estado,Observaciones");

                for (int i = 0; i < clientes.size(); i++) {
                    Cliente c = clientes.get(i);
                    String fechaActual = dtf.format(LocalDateTime.now());

                    System.out.print("[" + (i + 1) + "/" + clientes.size() + "] Enviando a: " + c.getNombre() + "... ");

                    try {
                        // Llamada al método corregido (asegúrate que en EmailSender se llame así)
                        sender.enviarCorreoAdjunto(c, Config.get("path.pdfs"));

                        // Registro de éxito en el CSV
                        logWriter.println(fechaActual + "," + c.getReferencia() + "," + c.getNombre() + "," + c.getCorreo() + ",ENVIADO,OK");
                        System.out.println("✅");

                    } catch (Exception e) {
                        // Registro de error en el CSV para seguimiento posterior
                        logWriter.println(fechaActual + "," + c.getReferencia() + "," + c.getNombre() + "," + c.getCorreo() + ",ERROR," + e.getMessage());
                        System.err.println("❌ (Error registrado en Log)");
                    }

                    // Forzar escritura en disco para no perder datos si el programa se detiene
                    logWriter.flush();

                    // 4. PAUSA DE SEGURIDAD: Límite de ráfaga de Microsoft 365
                    // Mantenemos 15 segundos (4 correos por minuto) para evitar bloqueos.
                    if (i < clientes.size() - 1) {
                        // El .trim() limpia cualquier espacio oculto antes de convertirlo en número
                        Thread.sleep(Integer.parseInt(Config.get("pausa.milisegundos").trim()));
                    }
                }
            }

            System.out.println("\n--- ✅ PROCESO FINALIZADO EXITOSAMENTE ---");
            System.out.println("Revise el archivo de seguimiento en: " + rutaLog);

        } catch (Exception e) {
            System.err.println("❌ ERROR GENERAL DEL SISTEMA:");
            e.printStackTrace();
        }
    }
}
