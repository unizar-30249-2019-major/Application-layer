package com.major.aplicacion.dtos;

import java.util.Date;

public class Period {


    private Date startDate;

    private Date endDate;

    public Period(){}

    public Period(Date startDate, Date endDate){
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getstartDate() {
        return startDate;
    }

    public void setstartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString(){
        return getstartDate()+ "-" + getEndDate();
    }


}
