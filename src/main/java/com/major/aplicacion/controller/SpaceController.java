package com.major.aplicacion.controller;

import com.major.aplicacion.Session.Cache;
import com.major.aplicacion.Session.SessionInfo;
import com.major.aplicacion.dtos.SpaceInfoDto;
import com.major.aplicacion.dtos.UserDto;
import com.major.aplicacion.messages.BrokerResponse;
import com.major.aplicacion.messages.MessageBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class SpaceController {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private MessageBroker messageBroker;

    private static Logger logger = LoggerFactory.getLogger(SpaceController.class);

    private Boolean isAdmin(String token) {
        return token != null && Cache.getItem(token).isPresent() && ((SessionInfo) Cache.getItem(token).get()).getRol() == UserDto.Rol.ADMIN;
    }

    @RequestMapping(value = "/spaces", method = RequestMethod.GET)
    public ResponseEntity getAllSpaces(@CookieValue(value = "token", required = false) String token) throws IOException {
        logger.info("GET\t/spaces request received");

        if (token == null || !Cache.containsToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse response = messageBroker.fetchAllSpaces();

        if(response.getStatus() == 200) {
            return ResponseEntity.status(response.getStatus()).body(response.getBody());
        }

        return ResponseEntity.status(response.getStatus()).build();
    }

    @RequestMapping(value = "/space/{id}", method = RequestMethod.GET)
    public ResponseEntity getSpaceById(@CookieValue(value = "token", required = false) String token, @PathVariable long id) throws IOException {
        logger.info("GET\t/space/{id} request received");

        if (token == null || !Cache.containsToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse response = messageBroker.fetchSpaceByID(id);

        if(response.getStatus() == 200) {
            return ResponseEntity.status(response.getStatus()).body(response.getBody());
        }

        return ResponseEntity.status(response.getStatus()).build();
    }

    @RequestMapping(value = "/space/{id}", method = RequestMethod.PUT)
    public ResponseEntity putSpaceById(@CookieValue(value = "token", required = false) String token, @PathVariable long id, SpaceInfoDto spaceInfoDto) throws IOException {
        logger.info("GET\t/space/{id} request received");

        if (token == null || !Cache.containsToken(token) || !isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse response = messageBroker.putSpaceByID(id, spaceInfoDto);

        return ResponseEntity.status(response.getStatus()).build();
    }

    @RequestMapping(value = "/space/{id}/bookings", method = RequestMethod.GET)
    public ResponseEntity getSpaceBookingsById(@CookieValue(value = "token", required = false) String token, @PathVariable long id) throws IOException {
        logger.info("GET\t/space/{id}/bookings request received");

        if (token == null || !Cache.containsToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BrokerResponse response = messageBroker.fetchSpaceBookingsByID(id);

        if(response.getStatus() == 200) {
            return ResponseEntity.status(response.getStatus()).body(response.getBody());
        }

        return ResponseEntity.status(response.getStatus()).build();

    }

}
