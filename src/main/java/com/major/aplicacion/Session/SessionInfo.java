package com.major.aplicacion.Session;

import com.major.aplicacion.dtos.PersonaEinaDto;

public class SessionInfo {
    private String token;
    private PersonaEinaDto.Rol rol;
    private long id;

    public SessionInfo(String token, PersonaEinaDto.Rol rol, long id) {
        this.token = token;
        this.rol = rol;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public PersonaEinaDto.Rol getRol() {
        return rol;
    }

    public long getId() {
        return id;
    }
}
