package com.far.basesales.Adapters.Models;

public class PercentRowModel {
    String percent, description, cantidad, monto;
    public PercentRowModel(String percent, String description, String cantidad, String monto){
        this.percent = percent;this.description = description; this.cantidad = cantidad; this.monto = monto;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }
}
