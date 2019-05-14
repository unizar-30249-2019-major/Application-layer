package com.major.aplicacion.dtos;

public class SpaceInfoDto {

    private boolean proyector;
    private int pizarra;
    private boolean pantalla;
    private int ordenadores;
    private int sillas;
    private int mesas;

    public boolean isProyector() {
        return proyector;
    }

    public void setProyector(boolean proyector) {
        this.proyector = proyector;
    }

    public int getPizarra() {
        return pizarra;
    }

    public void setPizarra(int pizarra) {
        this.pizarra = pizarra;
    }

    public boolean isPantalla() {
        return pantalla;
    }

    public void setPantalla(boolean pantalla) {
        this.pantalla = pantalla;
    }

    public int getOrdenadores() {
        return ordenadores;
    }

    public void setOrdenadores(int ordenadores) {
        this.ordenadores = ordenadores;
    }

    public int getSillas() {
        return sillas;
    }

    public void setSillas(int sillas) {
        this.sillas = sillas;
    }

    public int getMesas() {
        return mesas;
    }

    public void setMesas(int mesas) {
        this.mesas = mesas;
    }
}
