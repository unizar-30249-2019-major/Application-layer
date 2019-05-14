package com.major.aplicacion.dtos;

import java.util.List;

public class SpaceDto {

    private String id;
    private int gid;
    private String layer;
    private String subclasses;
    private String extendeden;
    private String linetype;
    private String entityhand;
    private String text;
    private double area;
    private double perimeter;
    private DataSpace dataSpace;
    private Materials materials;

    private List<BookingDtoReturn> bookings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getSubclasses() {
        return subclasses;
    }

    public void setSubclasses(String subclasses) {
        this.subclasses = subclasses;
    }

    public String getExtendeden() {
        return extendeden;
    }

    public void setExtendeden(String extendeden) {
        this.extendeden = extendeden;
    }

    public String getLinetype() {
        return linetype;
    }

    public void setLinetype(String linetype) {
        this.linetype = linetype;
    }

    public String getEntityhand() {
        return entityhand;
    }

    public void setEntityhand(String entityhand) {
        this.entityhand = entityhand;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(double perimeter) {
        this.perimeter = perimeter;
    }

    public DataSpace getDataSpace() {
        return dataSpace;
    }

    public void setDataSpace(DataSpace dataSpace) {
        this.dataSpace = dataSpace;
    }

    public List<BookingDtoReturn> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingDtoReturn> bookings) {
        this.bookings = bookings;
    }

    public Materials getMaterials() {
        return materials;
    }

    public void setMaterials(Materials materials) {
        this.materials = materials;
    }
}
