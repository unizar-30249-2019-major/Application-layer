package com.major.aplicacion.dtos;

public class SpaceTimetableDto {

    private Period period;
    private String state;

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
