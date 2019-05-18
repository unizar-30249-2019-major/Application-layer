package com.major.aplicacion.controller;

import com.major.aplicacion.Session.Cache;
import com.major.aplicacion.Session.SessionInfo;
import com.major.aplicacion.dtos.LoginDto;
import com.major.aplicacion.dtos.UserDto;
import com.major.aplicacion.messages.BrokerResponse;
import com.major.aplicacion.messages.MessageBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private MessageBroker messageBroker;

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private Boolean checkPrivileges(String token, long id_request) {
        if(token == null || !Cache.containsToken(token)) { return false; }

        Optional sessionInfoOptional = Cache.getItem(token);

        if(!sessionInfoOptional.isPresent()) { return false; }

        UserDto.Rol rol = ((SessionInfo) sessionInfoOptional.get()).getRol();
        long id = ((SessionInfo) sessionInfoOptional.get()).getId();


        return rol == UserDto.Rol.ADMIN || id == id_request;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity createUser(@CookieValue(value = "token", required = false) String token, @RequestBody UserDto userDto) throws IOException, InterruptedException {
        logger.info("POST\t/user request received");

        List<String> errors = userDto.checkErrorsNew();

        if (userDto.getRol() == null) {
            userDto.setRol(UserDto.Rol.ESTUDIANTE);
        }

        Optional sessionInfoOptional = Cache.getItem(token);

        if((sessionInfoOptional.isPresent() && ((SessionInfo) sessionInfoOptional.get()).getRol() != UserDto.Rol.ADMIN) || // REQUEST from logged user rol != admin
            (!sessionInfoOptional.isPresent() && userDto.getRol() != UserDto.Rol.ESTUDIANTE)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (errors.size() != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toArray());
        }

        BrokerResponse response = messageBroker.createNewUser(userDto);

        return ResponseEntity.status(response.getStatus()).build();
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginDto loginDto) throws IOException {
        logger.info("POST\t/user/login request received");

        BrokerResponse response = messageBroker.login(loginDto);

        if (response.getStatus() == 200) {

            SessionInfo responseBody = (SessionInfo) response.getBody();
            HttpCookie cookie = ResponseCookie
                    .from("token", responseBody.getToken())
                    .maxAge(Duration.ofMinutes(60))
                    .path("/")
                    .build();
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString()).body("{\n\t\"id\": " + responseBody.getId() + ",\n\t\"rol\": \"" + responseBody.getRol() + "\"\n}");
        } else {
            return ResponseEntity.status(response.getStatus()).build();
        }
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.DELETE)
    public ResponseEntity logout(@CookieValue(value = "token", required = false) String token) {
        logger.info("DELETE\t/user/login request received");

        Cache.pop(token);

        return ResponseEntity.status(201).build();
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity getUserById(@CookieValue(value = "token", required = false) String token, @PathVariable long id) throws IOException, InterruptedException {
        logger.info("GET\t/user/{id} request received");

        if(!checkPrivileges(token, id)) { // If dont have permission
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BrokerResponse response = messageBroker.fetchUserByID(id);

        if(response.getStatus() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(response.getBody());
        }

        return ResponseEntity.status(response.getStatus()).build();
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@CookieValue(value = "token", required = false) String token, @PathVariable long id) throws IOException, InterruptedException {
        logger.info("DELETE\t/user/{id} request received");

        if(!checkPrivileges(token, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Cache.popById(id);
        BrokerResponse response = messageBroker.deleteUserByID(id);

        return ResponseEntity.status(response.getStatus()).build();
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateUser(@CookieValue(value = "token", required = false) String token, @PathVariable long id, @RequestBody UserDto userDto) throws IOException, InterruptedException {
        logger.info("PUT\t/user/{id} request received");

        if(!checkPrivileges(token, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (userDto.getRol() == null) {
            userDto.setRol(UserDto.Rol.ESTUDIANTE);
        }

        List<String> errors = userDto.checkErrorsNew();
        if (errors.size() != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toArray());
        }

        UserDto.Rol rol = ((SessionInfo) Cache.getItem(token).get()).getRol();
        if(rol != UserDto.Rol.ADMIN  && userDto.getRol() != rol) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BrokerResponse response = messageBroker.updateUserByID(id, userDto);

        return ResponseEntity.status(response.getStatus()).build();
    }

    @RequestMapping(value = "/user/{id}/bookings", method = RequestMethod.GET)
    public ResponseEntity getUserBookingsById(@CookieValue(value = "token", required = false) String token, @PathVariable long id) throws IOException, InterruptedException {
        logger.info("GET\t/user/{id}/bookings request received");

        if(!checkPrivileges(token, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BrokerResponse response = messageBroker.fetchUserBookingsByID(id);

        if(response.getStatus() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(response.getBody());
        }

        return ResponseEntity.status(response.getStatus()).build();
    }
}
