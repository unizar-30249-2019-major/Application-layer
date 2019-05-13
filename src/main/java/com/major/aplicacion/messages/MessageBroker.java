package com.major.aplicacion.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.major.aplicacion.Session.Cache;
import com.major.aplicacion.Session.SessionInfo;
import com.major.aplicacion.dtos.BookingDto;
import com.major.aplicacion.dtos.BookingDtoReturn;
import com.major.aplicacion.dtos.LoginDto;
import com.major.aplicacion.dtos.UserDto;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


/**
 * Adapted from <https://github.com/UNIZAR-62227-TMDAD/messaging>
 */
@Component
public class MessageBroker {

    private static final String REQUEST_QUEUE = "REQUESTS";
    private static final String RESPONSE_QUEUE = "RESPONSES";


    private static final String USER = "user";

    @SuppressWarnings("SpellCheckingInspection")
    private static final String PASSWORD = "bitnami";

    private static final String AMQP_URL = "amqp://localhost";

    private Channel channel;
    private QueueingConsumer consumer;

    private static Logger logger = LoggerFactory.getLogger(MessageBroker.class);

    public MessageBroker() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(USER);
        factory.setPassword(PASSWORD);

        String amqpURL = AMQP_URL;

        try {
            factory.setUri(amqpURL);
        } catch (Exception e) {
            logger.error(" [*] AMQP broker not found in " + amqpURL);
            System.exit(-1);
        }
        logger.info("AMQP broker found in " + amqpURL);

        Connection connection = factory.newConnection();

        channel = connection.createChannel();
        channel.queueDeclare(REQUEST_QUEUE, false, false, false, null);
        channel.queueDeclare(RESPONSE_QUEUE, false, false, false, null);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(RESPONSE_QUEUE, true, consumer);

    }

    private String messageBuilder(String operation, String[] args) {
        String SPLITTER = ";";
        StringBuilder message = new StringBuilder();

        message.append(RESPONSE_QUEUE).append(SPLITTER);
        message.append(operation);
        if (args.length > 0) {
            message.append(SPLITTER).append(String.join("::", args));
        }


        return message.toString();
    }

    private String[] publishAndGetResponse(String message) {
        try {
            channel.basicPublish("", REQUEST_QUEUE, null, message.getBytes());

            // Wait for response
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            return new String(delivery.getBody()).split(";");

        } catch (Exception e) {
            logger.error("publishAndGetResponse", e);
            return new String[]{"500;null"};
        }
    }

    //
    // --- Broker messages
    //

    public BrokerResponse login(LoginDto loginDto) throws IOException {

        String message = messageBuilder("login", new String[]{new ObjectMapper().writeValueAsString(loginDto)});

        String[] response = publishAndGetResponse(message);

        String[] responseValues = response[1].split("::");
        if (!response[0].equals("200")) {
            return new BrokerResponse(Integer.parseInt(response[0]), null);
        }

        Cache.push(new SessionInfo(responseValues[0], UserDto.Rol.valueOf(responseValues[1]), Long.parseLong(responseValues[2])));

        return new BrokerResponse(Integer.parseInt(response[0]), new SessionInfo(responseValues[0], UserDto.Rol.valueOf(responseValues[1]), Long.parseLong(responseValues[2])));
    }

    public BrokerResponse createNewUser(UserDto userDto) throws IOException {

        String message = messageBuilder("createNewUser",
                new String[]{new ObjectMapper().writeValueAsString(userDto)});

        String[] response = publishAndGetResponse(message);

        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }

    public BrokerResponse fetchUserByID(long id) throws IOException {
        String message = messageBuilder("fetchUserByID", new String[]{Long.toString(id)});

        String[] response = publishAndGetResponse(message);

        ObjectMapper mapper = new ObjectMapper();

        return new BrokerResponse(Integer.parseInt(response[0]), mapper.readValue(response[1], UserDto.class));
    }

    public BrokerResponse deleteUserByID(long id) {
        String message = messageBuilder("deleteUserByID", new String[]{Long.toString(id)});

        String[] response = publishAndGetResponse(message);


        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }

    public BrokerResponse updateUserByID(long id, UserDto userDto) throws IOException {
        String message = messageBuilder("updateUserByID", new String[]{Long.toString(id), new ObjectMapper().writeValueAsString(userDto)});

        String[] response = publishAndGetResponse(message);


        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }

    public BrokerResponse fetchUserBookingsByID(long id) throws IOException {
        String message = messageBuilder("fetchUserBookingsByID", new String[]{Long.toString(id)});

        String[] response = publishAndGetResponse(message);

        return new BrokerResponse(Integer.parseInt(response[0]), new ObjectMapper().readValue(response[1], new TypeReference<List<BookingDtoReturn>>() {
        }));
    }

    public BrokerResponse getUserIDOfBooking(long id) {
        String message = messageBuilder("getUserIDOfBooking", new String[]{Long.toString(id)});

        String[] response = publishAndGetResponse(message);
        if (Integer.parseInt(response[0]) == 200) {
            return new BrokerResponse(Integer.parseInt(response[0]), Long.parseLong(response[1]));
        }
        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }

    public BrokerResponse getBookingById(long id) throws IOException {
        String message = messageBuilder("getBookingById", new String[]{Long.toString(id)});

        String[] response = publishAndGetResponse(message);

        return new BrokerResponse(Integer.parseInt(response[0]), new ObjectMapper().readValue(response[1], BookingDtoReturn.class));
    }

    public BrokerResponse createNewBooking(long id, BookingDto bookingDto) throws IOException {
        String message = messageBuilder((bookingDto.isIsPeriodic() ? "createNewPeriodicBooking" : "createNewBooking"), new String[]{Long.toString(id), new ObjectMapper().writeValueAsString(bookingDto)});

        String[] response = publishAndGetResponse(message);

        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }

    public BrokerResponse deleteBookingById(long id) {
        String message = messageBuilder("deleteBookingById", new String[]{Long.toString(id)});

        String[] response = publishAndGetResponse(message);

        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }

    public BrokerResponse putBookingById(long id, BookingDto bookingDto) throws IOException {
        String message = messageBuilder("putBookingById", new String[]{Long.toString(id), new ObjectMapper().writeValueAsString(bookingDto)});

        String[] response = publishAndGetResponse(message);

        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }

    public BrokerResponse fetchAllBookings() throws IOException {
        String message = messageBuilder("fetchAllBookings", new String[]{});

        String[] response = publishAndGetResponse(message);

        if (Integer.parseInt(response[0]) == 200) {
            return new BrokerResponse(Integer.parseInt(response[0]), new ObjectMapper().readValue(response[1], new TypeReference<List<BookingDtoReturn>>() {
            }));
        }
        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }

    public BrokerResponse fetchPendingBookings() throws IOException {
        String message = messageBuilder("fetchPendingBookings", new String[]{});

        String[] response = publishAndGetResponse(message);

        if (Integer.parseInt(response[0]) == 200) {
            return new BrokerResponse(Integer.parseInt(response[0]), new ObjectMapper().readValue(response[1], new TypeReference<List<BookingDtoReturn>>() {
            }));
        }
        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }

    public BrokerResponse validateBookingById(long id) {
        String message = messageBuilder("validateBookingById", new String[]{Long.toString(id)});

        String[] response = publishAndGetResponse(message);

        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }

    public BrokerResponse cancelBookingById(long id) {
        String message = messageBuilder("cancelBookingById", new String[]{Long.toString(id)});

        String[] response = publishAndGetResponse(message);

        return new BrokerResponse(Integer.parseInt(response[0]), null);
    }
}
