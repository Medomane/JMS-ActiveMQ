package jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Scanner;

public class Consumer {
    public static void main(String[] args) throws JMSException {
        /*Scanner scanner = new Scanner(System.in);
        System.out.println("Code : ");
        String code = scanner.nextLine();*/
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session= connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        //Destination destination= session.createQueue("enset.queue");
        Destination destination= session.createTopic("enset.topic");
        MessageConsumer consumer = session.createConsumer(destination/*,"code='"+code+"'"*/);
        consumer.setMessageListener(
            message -> {
                if(message instanceof TextMessage){
                    var tm = (TextMessage)message;
                    try {
                        System.out.println("Reception de message : " +tm.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        );
    }
}
