package com.major.aplicacion.controller;

import com.major.aplicacion.Session.SessionInfo;
import com.major.aplicacion.dtos.LoginDto;
import com.major.aplicacion.dtos.UserDto;
import com.major.aplicacion.messages.BrokerResponse;
import com.major.aplicacion.messages.MessageBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class UserController {

    private MessageBroker messageBroker = new MessageBroker();
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController() throws Exception {
    }

    @RequestMapping(value = "/user")
    public ResponseEntity createUser(@CookieValue(value = "token", required = false) String token, @RequestBody UserDto userDto) throws IOException, InterruptedException {
        logger.info("POST\t/user request received");

        List<String> errors = userDto.checkErrorsNew();

        if (userDto.getRol() == null) {
            userDto.setRol(UserDto.Rol.ESTUDIANTE);
        }

        if ((token != null && messageBroker.getRol(token) != UserDto.Rol.ADMIN) || // Request create user from not admin user logged
                (token == null || messageBroker.getRol(token) != UserDto.Rol.ADMIN) && // not logged user or not admin user creates not student user
                        userDto.getRol() != UserDto.Rol.ESTUDIANTE) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (errors.size() != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.toArray());
        }

        BrokerResponse response = messageBroker.createNewUser(userDto);

        return ResponseEntity.status(response.getStatus()).build();
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginDto loginDto) throws IOException, InterruptedException, ExecutionException {
        logger.info("POST\t/user/login request received");

        BrokerResponse response = messageBroker.login(loginDto.getUsername(), loginDto.getPassword());

        if (response.getStatus() == 200) {

            SessionInfo responseBody = (SessionInfo) response.getBody();
            HttpCookie cookie = ResponseCookie
                    .from("token", responseBody.getToken())
                    .maxAge(Duration.ofMinutes(60))
                    .path("/")
                    .build();
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString()).body(responseBody.getRol());
        } else {
            return ResponseEntity.status(response.getStatus()).build();
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity getUserById(@PathVariable long id) {

        return null;
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@PathVariable long id) {

        return null;
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateUser(@PathVariable long id, @RequestBody UserDto userDto) {

        return null;
    }

    @RequestMapping(value = "/user/{id}/bookings", method = RequestMethod.GET)
    public ResponseEntity getUserBookingsById(@PathVariable long id){

        return null;
    }
}
