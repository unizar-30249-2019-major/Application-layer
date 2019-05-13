package com.major.aplicacion.controller;

import com.major.aplicacion.Session.Cache;
import com.major.aplicacion.Session.SessionInfo;
import com.major.aplicacion.dtos.BookingDto;
import com.major.aplicacion.dtos.UserDto;
import com.major.aplicacion.messages.BrokerResponse;
import com.major.aplicacion.messages.MessageBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class BookingController {

    @Autowired
    private MessageBroker messageBroker;

    private static Logger logger = LoggerFactory.getLogger(BookingController.class);

    private Boolean isStudent(String token) {
        return token != null && Cache.getItem(token).isPresent() && ((SessionInfo) Cache.getItem(token).get()).getRol() == UserDto.Rol.ESTUDIANTE;
    }

    private Boolean isPas(String token) {
        return token != null && Cache.getItem(token).isPresent() && ((SessionInfo) Cache.getItem(token).get()).getRol() == UserDto.Rol.PAS;
    }

    private Boolean isAdmin(String token) {
        return token != null && Cache.getItem(token).isPresent() && ((SessionInfo) Cache.getItem(token).get()).getRol() == UserDto.Rol.ADMIN;
    }

    private long getSessionUserID(String token) {
        return ((SessionInfo) Cache.getItem(token).get()).getId();
    }

    @RequestMapping(value = "/booking", method = RequestMethod.POST)
    public ResponseEntity createBooking(@CookieValue(value = "token", required = false) String token, @RequestBody BookingDto bookingDto) throws IOException {
        logger.info("POST\t/booking request received");

        if (token == null || !Cache.containsToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (isStudent(token) && bookingDto.isIsPeriodic()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!isAdmin(token) && bookingDto.isEspecial()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if(bookingDto.getPeriod() == null || bookingDto.getSpaces() == null || bookingDto.getSpaces().size() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (bookingDto.getPeriod().getstartDate().compareTo(bookingDto.getPeriod().getEndDate()) > 0 ||
                (bookingDto.isIsPeriodic() && bookingDto.getFinalDate().compareTo(bookingDto.getPeriod().getEndDate()) < 0)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        BrokerResponse response = messageBroker.createNewBooking(getSessionUserID(token), bookingDto);

        return ResponseEntity.status(response.getStatus()).build();
    }

    @RequestMapping(value = "/booking/{id}", method = RequestMethod.GET)
    public ResponseEntity getBookingById(@CookieValue(value = "token", required = false) String token, @PathVariable long id) throws IOException {
        logger.info("GET\t/booking/{id} request received");

        if (token == null || !Cache.containsToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse response = messageBroker.getUserIDOfBooking(id);
        if (response.getStatus() != 200) {
            return ResponseEntity.status(response.getStatus()).build();
        }

        if (!isAdmin(token) && getSessionUserID(token) != (long) response.getBody()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BrokerResponse responseBooking = messageBroker.getBookingById(id);

        if (responseBooking.getStatus() != 200) {
            return ResponseEntity.status(responseBooking.getStatus()).build();
        }

        return ResponseEntity.status(responseBooking.getStatus()).body(responseBooking.getBody());
    }

    @RequestMapping(value = "/booking/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteBooking(@CookieValue(value = "token", required = false) String token, @PathVariable long id) {
        logger.info("DELETE\t/booking/{id} request received");

        if (token == null || !Cache.containsToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse response = messageBroker.getUserIDOfBooking(id);
        if (response.getStatus() != 200) {
            return ResponseEntity.status(response.getStatus()).build();
        }

        if (!isAdmin(token) && getSessionUserID(token) != (long) response.getBody()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BrokerResponse status = messageBroker.deleteBookingById(id);

        return ResponseEntity.status(status.getStatus()).build();
    }

    @RequestMapping(value = "/booking/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateUser(@CookieValue(value = "token", required = false) String token, @PathVariable long id, @RequestBody BookingDto bookingDto) throws IOException {
        logger.info("PUT\t/booking/{id} request received");

        if (token == null || !Cache.containsToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (isStudent(token) && bookingDto.isIsPeriodic()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!isAdmin(token) && bookingDto.isEspecial()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (bookingDto.getPeriod().getstartDate().compareTo(bookingDto.getPeriod().getEndDate()) > 0 ||
                (bookingDto.isIsPeriodic() && bookingDto.getFinalDate().compareTo(bookingDto.getPeriod().getEndDate()) < 0)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        BrokerResponse response = messageBroker.getUserIDOfBooking(id);
        if (response.getStatus() != 200) {
            return ResponseEntity.status(response.getStatus()).build();
        }

        if (!isAdmin(token) && getSessionUserID(token) != (long) response.getBody()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BrokerResponse status = messageBroker.putBookingById(id, bookingDto);

        return ResponseEntity.status(status.getStatus()).build();
    }

    @RequestMapping(value = "/booking/{id}/validate", method = RequestMethod.PUT)
    public ResponseEntity validateBooking(@CookieValue(value = "token", required = false) String token, @PathVariable long id) {
        logger.info("PUT\t/bookings/{id}/validate request received");

        if (token == null || !isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse status = messageBroker.validateBookingById(id);

        return ResponseEntity.status(status.getStatus()).build();
    }

    @RequestMapping(value = "/booking/{id}/cancel", method = RequestMethod.PUT)
    public ResponseEntity cancelBooking(@CookieValue(value = "token", required = false) String token, @PathVariable long id) {
        logger.info("PUT\t/bookings/{id}/validate request received");

        if (token == null || !isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse status = messageBroker.cancelBookingById(id);

        return ResponseEntity.status(status.getStatus()).build();
    }

    @RequestMapping(value = "/bookings/all", method = RequestMethod.GET)
    public ResponseEntity getAllBookings(@CookieValue(value = "token", required = false) String token) throws IOException {
        logger.info("GET\t/bookings request received");

        if (token == null || !isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse response = messageBroker.fetchAllBookings();

        if(response.getStatus() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(response.getBody());
        }

        return ResponseEntity.status(response.getStatus()).build();
    }

    @RequestMapping(value = "/bookings/pending", method = RequestMethod.GET)
    public ResponseEntity getPendingBookings(@CookieValue(value = "token", required = false) String token) throws IOException {
        logger.info("GET\t/bookings request received");

        if (token == null || !isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse response = messageBroker.fetchPendingBookings();

        if(response.getStatus() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(response.getBody());
        }

        return ResponseEntity.status(response.getStatus()).build();
    }
}
