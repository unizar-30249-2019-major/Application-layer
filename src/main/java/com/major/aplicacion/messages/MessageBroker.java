package com.major.aplicacion.messages;

import com.major.aplicacion.dtos.UserDto;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Adaptado de <https://github.com/UNIZAR-62227-TMDAD/messaging>
 *
 */
public class MessageBroker {

	private static final String REQUEST_QUEUE = "REQUESTS";


	private static final String USER = "user";

	private static final String PASSWORD = "bitnami";

	private static final String AMQP_URL = "amqp://localhost";

	private Channel channel;

	private static Logger logger = LoggerFactory.getLogger(MessageBroker.class);



	public MessageBroker() throws Exception{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(USER);
		factory.setPassword(PASSWORD);

		String amqpURL = AMQP_URL;

		try {
			factory.setUri(amqpURL);
		} catch (Exception e) {
			logger.error(" [*] AQMP broker not found in " + amqpURL);
			System.exit(-1);
		}
		logger.info("AQMP broker found in " + amqpURL);

		Connection connection = factory.newConnection();

		channel = connection.createChannel();
		channel.queueDeclare(REQUEST_QUEUE, false, false, false, null);

	}

	@Async
	public CompletableFuture<String> genCode() throws Exception {
		String RESPONSE_QUEUE = UUID.randomUUID().toString();
		channel.queueDeclare(RESPONSE_QUEUE, false, false, false, null);

		String message = RESPONSE_QUEUE + ";" +
						"getRandomNumber";
		channel.basicPublish("", "REQUESTS", null, message.getBytes());

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(RESPONSE_QUEUE, true, consumer);
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();

		channel.queueDelete(RESPONSE_QUEUE);

		return CompletableFuture.completedFuture(new String(delivery.getBody()));
	}

	@Async
	public CompletableFuture<String> login(String user, String password) throws IOException, InterruptedException {
		String RESPONSE_QUEUE = UUID.randomUUID().toString();
		channel.queueDeclare(RESPONSE_QUEUE, false, false, false, null);

		// Send message: format -> RESPONSE_QUEUE;OPERATION;ARGS
		String message = RESPONSE_QUEUE + ";" + "login" + ";" + user + "," + password;
		channel.basicPublish("", "REQUESTS", null, message.getBytes());

		// Wait for response
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(RESPONSE_QUEUE, true, consumer);
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();

		String[] response = new String(delivery.getBody()).split(";");

		//Delete response queue
		channel.queueDelete(RESPONSE_QUEUE);

		getRol(response[1]);

		return CompletableFuture.completedFuture(response[1]);
	}

	@Async
	@Cacheable("tokenRolCache")
	public UserDto.Rol getRol(String token) throws IOException, InterruptedException {
		String RESPONSE_QUEUE = UUID.randomUUID().toString();
		channel.queueDeclare(RESPONSE_QUEUE, false, false, false, null);

		// Send message: format -> RESPONSE_QUEUE;OPERATION;ARGS
		String message = RESPONSE_QUEUE + ";" + "getRol" + ";" + token;
		channel.basicPublish("", "REQUESTS", null, message.getBytes());

		// Wait for response
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(RESPONSE_QUEUE, true, consumer);
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();

		String[] response = new String(delivery.getBody()).split(";");

		//Delete response queue
		channel.queueDelete(RESPONSE_QUEUE);
		switch (response[1].toLowerCase()) {
			case "user":
				return UserDto.Rol.USER;
			case "pas":
				return UserDto.Rol.PAS;
			case "admin":
				return UserDto.Rol.ADMIN;
		}
		return UserDto.Rol.ERROR;
	}

}
