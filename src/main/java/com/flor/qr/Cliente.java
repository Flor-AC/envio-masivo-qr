package com.flor.qr;

public class Cliente {
    private String nombre;
    private String correo;
    private String referencia;

    public Cliente(String nombre, String correo, String referencia) {
        this.nombre = nombre;
        this.correo = correo;
        this.referencia = referencia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getReferencia() {
        return referencia;
    }
}
