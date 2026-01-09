package com.flor.qr;

public class Cliente {
    // Campos marcados como 'final' para que no cambien una vez creados
    private final String nombre;
    private final String correo;
    private final String referencia;

    // Constructor completo para inicializar los datos
    public Cliente(String nombre, String correo, String referencia) {
        this.nombre = nombre;
        this.correo = correo;
        this.referencia = referencia;
    }

    //Métodos Getter (para poder leer los datos desde otras clases)
    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getReferencia() {
        return referencia;
    }

    //Método toString (muy útil para depurar y ver los datos en consola)
    @Override
    public String toString() {
        return "Cliente{" +
                "nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", referencia='" + referencia + '\'' +
                '}';
    }
}