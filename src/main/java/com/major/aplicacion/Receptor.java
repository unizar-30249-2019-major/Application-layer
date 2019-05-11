package com.major.aplicacion;

import com.major.aplicacion.dtos.UserDto;
import com.major.aplicacion.messages.MessageBroker;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

/**
 * Adapted from <https://github.com/UNIZAR-62227-TMDAD/messaging>
 *
 */
public class Receptor {

	private static final String REQUEST_QUEUE = "REQUESTS";
	private static final String RESPONSE_QUEUE = "RESPONSES";


	private static final String USER = "user";

	@SuppressWarnings("SpellCheckingInspection")
	private static final String PASSWORD = "bitnami";

	private static final String AMQP_URL = "amqp://localhost";

	private static Logger logger = LoggerFactory.getLogger(MessageBroker.class);


	public static void main(String[] argv) throws Exception {

		Channel channel;
		QueueingConsumer consumer;


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


		channel.basicConsume(REQUEST_QUEUE, true, consumer);


		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			String[] messageParts = message.split(";");
			logger.info("Message received: \n\tResponse queue: " + messageParts[0] +
					"\n\tFunction: " + messageParts[1] +
					"\n\tArguments: " + (messageParts.length == 3 ? messageParts[2] : "No args"));

			String retMessage;
			switch(messageParts[1]) {
				case "login":
					retMessage = "200" + ";" + UUID.randomUUID().toString() + "::" + UserDto.Rol.values()[new Random().nextInt(3)] + "::" + "154";
					break;
				case "createNewUser":
					retMessage = "201";
					break;
				case "fetchUserByID":
					//retMessage = "404;null";
					retMessage = "200" + ";" + "{\"id\": 15879,\"firstName\": \"Jorge\", \"lastName\": \"Rambla\", \"nameUser\": \"yorch\", \"email\": \"718911@unizar.es\"}";
					break;
				case "deleteUserByID":
					retMessage = "404;null";
					break;
				case "updateUserByID":
					retMessage = "405;null";
					break;
				case "fetchUserBookingsByID":
					retMessage = "200" + ";" + "[ { \"id\": 2, \"isPeriodic\": false, \"reason\": \"charla tfg9\", \"period\": [ { \"startDate\": \"2019-04-09T18:00:00.000+0000\", \"endDate\": \"2019-04-09T20:00:00.000+0000\" } ], \"state\": \"inicial\", \"active\": true, \"periodRep\": null, \"finalDate\": null}, { \"id\": 3, \"isPeriodic\": false, \"reason\": \"charla tfg9\", \"period\": [ { \"startDate\": \"2019-04-09T18:00:00.000+0000\", \"endDate\": \"2019-04-09T20:00:00.000+0000\" } ], \"state\": \"inicial\", \"active\": true, \"periodRep\": null, \"finalDate\": null} ]";
					break;
				case "getUserIDOfBooking":
					retMessage = "200;155";
					break;
				case "getBookingById":
					retMessage = "200" + ";" + "{ \"id\": 2, \"isPeriodic\": false, \"reason\": \"charla tfg9\", \"period\": [ { \"startDate\": \"2019-04-09T18:00:00.000+0000\", \"endDate\": \"2019-04-09T20:00:00.000+0000\" } ], \"state\": \"inicial\", \"active\": true, \"periodRep\": null, \"finalDate\": null}";
					break;
				default:
					retMessage = "500;null";
					logger.error("Message not recognized");
					break;
			}

			// Envio respuesta
			logger.info(" Message send:\n\t" + "ID queue: " + RESPONSE_QUEUE + "\n\tMessage: " + retMessage);
			channel.basicPublish("", messageParts[0], null, retMessage.getBytes());

		}
	}
}
