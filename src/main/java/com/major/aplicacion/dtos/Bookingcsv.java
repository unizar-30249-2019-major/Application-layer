package com.major.aplicacion.dtos;

import java.util.ArrayList;
import java.util.List;

public class Bookingcsv {
    private String reason;
    private List<Period> periods;
    private List<Integer> spaces;

    public Bookingcsv() {
        this.spaces = new ArrayList<>();
        this.periods = new ArrayList<>();
    }

    public Bookingcsv(String reason) {
        this.reason = reason;
        this.periods = new ArrayList<>();
        this.spaces = new ArrayList<>();
    }

    public void addSpace(int value) {
        spaces.add(value);
    }

    public void addPeriod(Period period) {
        this.periods.add(period);
    }

    public String getReason() {
        return reason;
    }

    public List<Integer> getSpaces() {
        return spaces;
    }

    public List<Period> getPeriods() {
        return periods;
    }
}