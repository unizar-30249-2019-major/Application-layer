package com.major.aplicacion;

import com.major.aplicacion.dtos.UserDto;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import java.util.Random;
import java.util.UUID;

/**
 * Adaptado de <https://github.com/UNIZAR-62227-TMDAD/messaging>
 *
 */
public class Receptor {

	public static void main(String[] argv) throws Exception {
		// Conexión al broker RabbitMQ broker (prueba en la URL de
		// la variable de entorno que se llame como diga ENV_AMQPURL_NAME
		// o sino en localhost)
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("user");
		factory.setPassword("bitnami");
		String amqpURL = "amqp://localhost";
		try {
			factory.setUri(amqpURL);
		} catch (Exception e) {
			System.out.println(" [*] AQMP broker not found in " + amqpURL);
			System.exit(-1);
		}
		System.out.println(" [*] AQMP broker found in " + amqpURL);
		Connection connection = factory.newConnection();
		// Con un solo canal
		Channel channel = connection.createChannel();
		channel.queueDeclare("REQUESTS", false, false, false, null);
		System.out.println(" [*] Esperando peticion");

		// El objeto consumer guardará los mensajes que lleguen
		// a la cola QUEUE_NAME hasta que los usemos
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("REQUESTS", true, consumer);





		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			String[] messageParts = message.split(";");
			System.out.println(" [x] Recibido '" + messageParts[1] + "' -- ID queue: " + messageParts[0]);
			String retMessage = "";
			switch(messageParts[1]) {
				case "login":
					retMessage = "200" + ";" + UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
					break;
				case "getRol":
					retMessage = "200" + ";" + UserDto.Rol.values()[new Random().nextInt(3)];
				default:
					break;
			}
			Thread.sleep(1000L);
			// Envio respuesta
			System.out.println(" [x] Respondo " + " -- ID queue: " + retMessage);
			channel.queueDeclare(messageParts[0], false, false, false, null);
			channel.basicPublish("", messageParts[0], null, retMessage.getBytes());

		}
	}
}
