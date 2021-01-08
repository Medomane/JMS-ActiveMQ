package jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Scanner;

public class Producer {
    public static void main(String[] args) throws JMSException {
        /*Scanner scanner = new Scanner(System.in);
        System.out.println("Vers : ");
        String code = scanner.nextLine();*/
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session= connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        //Destination destination= session.createQueue("enset.queue");
        Destination destination= session.createTopic("enset.topic");
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        String text= "Hello world!";
        TextMessage message = session.createTextMessage(text);
        //message.setStringProperty("code",code);
        producer.send(message);
        System.out.println("Envoi du message");
        session.close();
        connection.close();
    }
}
