package com.major.aplicacion.Session;

import com.major.aplicacion.dtos.UserDto;

public class SessionInfo {
    private String token;
    private UserDto.Rol rol;
    private long id;

    public SessionInfo(String token, UserDto.Rol rol, long id) {
        this.token = token;
        this.rol = rol;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public UserDto.Rol getRol() {
        return rol;
    }

    public long getId() {
        return id;
    }
}
