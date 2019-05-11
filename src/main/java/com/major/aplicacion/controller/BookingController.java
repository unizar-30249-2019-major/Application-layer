package com.major.aplicacion.controller;

import com.major.aplicacion.Cache;
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
public class BookingController {

    @Autowired
    private MessageBroker messageBroker;

    private static Logger logger = LoggerFactory.getLogger(BookingController.class);

    private Boolean isStudent(String token) {
        return token != null && ((SessionInfo) Cache.getItem(token).get()).getRol() == UserDto.Rol.PAS;
    }

    private Boolean isPas(String token) {
        return token != null && ((SessionInfo) Cache.getItem(token).get()).getRol() == UserDto.Rol.ADMIN;
    }

    private Boolean isAdmin(String token) {
        return token != null && ((SessionInfo) Cache.getItem(token).get()).getRol() == UserDto.Rol.ADMIN;
    }

    private long getSessionUserID(String token) {
        return ((SessionInfo) Cache.getItem(token).get()).getId();
    }

    @RequestMapping(value = "/booking", method = RequestMethod.POST)
    public ResponseEntity createBooking(@CookieValue(value = "token", required = false) String token) throws IOException, InterruptedException {
        logger.info("POST\t/booking request received");
        //TODO


        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @RequestMapping(value = "/booking/{id}", method = RequestMethod.GET)
    public ResponseEntity getBookingById(@CookieValue(value = "token", required = false) String token, @PathVariable long id) throws IOException, InterruptedException {
        logger.info("GET\t/booking/{id} request received");

        if(token == null || !Cache.containsToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse response = messageBroker.getUserIDOfBooking(id);
        if(response.getStatus() != 200) {
            return ResponseEntity.status(response.getStatus()).build();
        }

        if(!isAdmin(token) && getSessionUserID(token) != (long) response.getBody()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BrokerResponse responseBooking = messageBroker.getBookingById(id);

        if(responseBooking.getStatus() != 200) {
            return ResponseEntity.status(responseBooking.getStatus()).build();
        }

        return ResponseEntity.status(responseBooking.getStatus()).body(responseBooking.getBody());
    }

    @RequestMapping(value = "/booking/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteBooking(@CookieValue(value = "token", required = false) String token, @PathVariable long id) throws IOException, InterruptedException {
        logger.info("DELETE\t/booking/{id} request received");
        //TODO
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @RequestMapping(value = "/booking/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateUser(@CookieValue(value = "token", required = false) String token, @PathVariable long id, @RequestBody UserDto userDto) throws IOException, InterruptedException {
        logger.info("PUT\t/booking/{id} request received");
        //TODO
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @RequestMapping(value = "/bookings", method = RequestMethod.GET)
    public ResponseEntity getAllBookings(@CookieValue(value = "token", required = false) String token) throws IOException, InterruptedException {
        logger.info("GET\t/bookings request received");
        //TODO
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
