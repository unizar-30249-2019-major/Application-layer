package com.major.aplicacion.controller;

import com.major.aplicacion.dtos.LoginDto;
import com.major.aplicacion.dtos.UserDto;
import com.major.aplicacion.messages.MessageBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class MainController {

    private MessageBroker messageBroker = new MessageBroker();
    private static Logger logger = LoggerFactory.getLogger(MainController.class);

    public MainController() throws Exception {
    }


    @RequestMapping(value = "/test/{code}", method = RequestMethod.GET)
    public String test(@PathVariable(value = "code") String code) throws Exception {
        logger.info("/test/{code} request received");
        CompletableFuture<String> genCode = messageBroker.genCode();

        CompletableFuture.allOf(genCode).join();
        logger.info("/test/{code} served");
        return "hola mundo" + code + "-" + new Random().nextInt(4156);// + genCode.get();
    }

    @RequestMapping(value = "/login", method =  RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginDto loginDto) throws IOException, InterruptedException, ExecutionException {
        logger.info("/login request received");

        CompletableFuture<String> token = messageBroker.login(loginDto.getUser(), loginDto.getPassword());

        CompletableFuture.allOf(token);
        HttpCookie cookie = ResponseCookie
                .from("token", token.get())
                .maxAge(Duration.ofMinutes(60))
                .path("/")
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public UserDto.Rol test2(@CookieValue("token") String token) throws IOException, InterruptedException, ExecutionException {
        logger.info("/test/ request received");

        UserDto.Rol nu = messageBroker.getRol(token);
        //CompletableFuture.allOf(nu);

        return nu;//.get();
    }
}
