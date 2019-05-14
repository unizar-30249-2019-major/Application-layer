package com.major.aplicacion.dtos;

public class DataSpace {

    private String edificio;
    private int planta;

    public DataSpace(){

    }
    public DataSpace(String edificio, int planta) {
        this.edificio = edificio;
        this.planta = planta;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public int getPlanta() {
        return planta;
    }

    public void setPlanta(int planta) {
        this.planta = planta;
    }
}
