package com.romero.extintores;

public class Extintor {
    private int id;
    private String tipo;
    private String capacidad;
    private String ubicacion;
    private String fechaVencimiento;
    private int diasRestantes;
    private boolean alerta;
    private boolean vencido;
    private String imagenUrl;

    public Extintor() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCapacidad() { return capacidad; }
    public void setCapacidad(String capacidad) { this.capacidad = capacidad; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public int getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(int diasRestantes) { this.diasRestantes = diasRestantes; }

    public boolean isAlerta() { return alerta; }
    public void setAlerta(boolean alerta) { this.alerta = alerta; }

    public boolean isVencido() { return vencido; }
    public void setVencido(boolean vencido) { this.vencido = vencido; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}