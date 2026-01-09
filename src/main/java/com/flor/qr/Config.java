package com.flor.qr;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    // Agregamos 'final' para que la advertencia desaparezca y sea más seguro
    private static final Properties props = new Properties();

    static {
        // Buscamos el archivo exactamente con el nombre "config.properties"
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("❌ ERROR: No se encontró config.properties en src/main/resources");
            } else {
                props.load(input);
            }
        } catch (Exception ex) {
            System.err.println("❌ ERROR al cargar la configuración: " + ex.getMessage());
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}