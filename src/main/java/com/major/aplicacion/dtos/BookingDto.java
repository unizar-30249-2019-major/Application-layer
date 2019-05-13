package com.major.aplicacion.dtos;


import java.util.Date;
import java.util.List;


public class BookingDto {


    private long id;

    private boolean isPeriodic;

    private String reason;

    private Period period;

    private String periodRep;

    private Date finalDate;

    private boolean Especial;

    private List<Integer> spaces;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isIsPeriodic() {
        return isPeriodic;
    }

    public void setIsPeriodic(boolean isPeriodic) {
        this.isPeriodic = isPeriodic;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period=period;
    }

    public String getPeriodRep() {
        return periodRep;
    }

    public void setPeriodRep(String periodRep) {
        this.periodRep = periodRep;
    }

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    public boolean isEspecial() {
        return Especial;
    }

    public void setEspecial(boolean especial) {
        Especial = especial;
    }

    public List<Integer> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<Integer> spaces) {
        this.spaces = spaces;
    }
}
