package com.major.aplicacion.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class UserDto {

    public enum Rol {
        ESTUDIANTE, PAS, ADMIN
    }

    private long id;
    private String firstName;
    private String lastName;
    private Rol rol;
    private String userName;
    private String email;
    private String password;
    @JsonIgnore
    private List<BookingDtoReturn> bookings;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Rol getRol() {
        return rol;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    public void setRol(String rol) {
        switch (rol.toLowerCase()) {
            case "estudiante":
                this.rol = Rol.ESTUDIANTE;
                break;
            case "admin":
                this.rol = Rol.ADMIN;
                break;
            case "pas":
                this.rol = Rol.PAS;
                break;
        }
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<BookingDtoReturn> getBookings() {
        return bookings;
    }
    public void setBookings(List<BookingDtoReturn> bookings) {
        this.bookings = bookings;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> checkErrorsNew() {
        List<String> errors = new ArrayList<>();

        if(email == null || email.split("@").length != 2 || !email.split("@")[1].equalsIgnoreCase("unizar.es")){
            errors.add("Email is not valid, should be @unizar.es");
        }

        if(password == null || password.length() < 8 || password.length() > 20) {
            errors.add("Password must be between 8 and 20 characters");
        }

        if(userName == null || userName.length() < 4 || userName.length() > 20) {
            errors.add("nameUser must be between 4 and 20 characters");
        }

        if(firstName == null || firstName.length() < 2 || firstName.length() > 20) {
            errors.add("firstName must be between 2 and 20 characters");
        }

        if(lastName == null || lastName.length() < 2 || lastName.length() > 20) {
            errors.add("lastName must be between 2 and 20 characters");
        }

        return errors;
    }
}