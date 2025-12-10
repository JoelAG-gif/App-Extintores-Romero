package com.romero.extintores;

public class Servicio {
    private int id;
    private String clientName;
    private String clientEmail;
    private String tipo;
    private String capacidad;
    private String ubicacion;
    private String estado;
    private String fechaServicio;
    private String tecnicoName;

    public Servicio() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCapacidad() { return capacidad; }
    public void setCapacidad(String capacidad) { this.capacidad = capacidad; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaServicio() { return fechaServicio; }
    public void setFechaServicio(String fechaServicio) { this.fechaServicio = fechaServicio; }

    public String getTecnicoName() { return tecnicoName; }
    public void setTecnicoName(String tecnicoName) { this.tecnicoName = tecnicoName; }
}