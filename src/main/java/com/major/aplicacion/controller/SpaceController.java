package com.major.aplicacion.controller;

import com.major.aplicacion.dtos.SpaceInfoDto;
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

    @Autowired
    private MessageBroker messageBroker;

    private static Logger logger = LoggerFactory.getLogger(SpaceController.class);

    @RequestMapping(value = "/spaces", method = RequestMethod.GET)
    public ResponseEntity getAllSpaces(@CookieValue(value = "token", required = false) String token) throws IOException {
        logger.info("GET\t/spaces request received");
        // TODO

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @RequestMapping(value = "/space/{id}", method = RequestMethod.GET)
    public ResponseEntity getSpaceById(@CookieValue(value = "token", required = false) String token, @PathVariable long id) throws IOException {
        logger.info("GET\t/space/{id} request received");
        // TODO

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @RequestMapping(value = "/space/{id}", method = RequestMethod.PUT)
    public ResponseEntity putSpaceById(@CookieValue(value = "token", required = false) String token, @PathVariable long id, SpaceInfoDto spaceInfoDto) throws IOException {
        logger.info("GET\t/space/{id} request received");
        // TODO

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @RequestMapping(value = "/space/{id}/bookings", method = RequestMethod.GET)
    public ResponseEntity getSpaceBookingsById(@CookieValue(value = "token", required = false) String token, @PathVariable long id) throws IOException {
        logger.info("GET\t/space/{id}/bookings request received");
        // TODO

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
