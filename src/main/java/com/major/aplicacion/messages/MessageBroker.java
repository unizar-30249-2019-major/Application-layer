package com.major.aplicacion.messages;

import com.major.aplicacion.Session.SessionInfo;
import com.major.aplicacion.dtos.UserDto;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Adapted from <https://github.com/UNIZAR-62227-TMDAD/messaging>
 *
 */
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

	public MessageBroker() throws Exception{
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

	}

	private String messageBuilder(String operation, String[] args) {
		String SPLITTER = ";";
		StringBuilder message = new StringBuilder();

		message.append(RESPONSE_QUEUE).append(SPLITTER);
		message.append(operation);
		if(args.length > 0) {
			message.append(SPLITTER).append(arrayToStringComma(args));
		}


		return message.toString();
	}

	private String arrayToStringComma(String[] array) {
		StringBuilder string = new StringBuilder();

		for (String element : array) {
			string.append(element).append(",");
		}
		return string.deleteCharAt(string.length() - 1).toString();
	}

	private String[] publishAndGetResponse(String message) throws IOException, InterruptedException {
		channel.basicPublish("", REQUEST_QUEUE, null, message.getBytes());

		// Wait for response
		channel.basicConsume(RESPONSE_QUEUE, true, consumer);
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();

		return new String(delivery.getBody()).split(";");
	}

	public BrokerResponse login(String user, String password) throws IOException, InterruptedException {

		String message = messageBuilder("login", new String[]{user, password});

		String[] response = publishAndGetResponse(message);

		String[] responseValues = response[1].split(",");

		return new BrokerResponse(Integer.parseInt(response[0]), new SessionInfo(responseValues[0], UserDto.Rol.values()[Integer.parseInt(responseValues[1])], Long.parseLong(responseValues[2])));
	}

	//@Cacheable("tokenRolCache")
	public UserDto.Rol getRol(String token) throws IOException, InterruptedException {
		String message = messageBuilder("getRol", new String[]{token});

		String[] response = publishAndGetResponse(message);

		return UserDto.Rol.values()[Integer.parseInt(response[1])];
	}

	public BrokerResponse createNewUser(UserDto userDto) throws IOException, InterruptedException {

		String message = messageBuilder("createNewUser",
				new String[]{userDto.getFirstName(), userDto.getLastName(), userDto.getNameUser(),
						userDto.getEmail(), userDto.getPassword(), userDto.getRol().toString()});

		String[] response = publishAndGetResponse(message);

		return new BrokerResponse(Integer.parseInt(response[0]), null);
	}
}
