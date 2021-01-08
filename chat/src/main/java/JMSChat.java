import helpers.Func;
import helpers.Notify;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JMSChat extends Application {
    public static void main(String[] args) {
        Application.launch(JMSChat.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("JMS Chat application");
        var bp = new BorderPane();

        //TOP
        HBox topBox = new HBox();
        topBox.setSpacing(10);
        topBox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        topBox.setMinHeight(50);
        topBox.setAlignment(Pos.CENTER);
        var labelCode = new Label("Code : ");
        var fieldCode = new TextField();
        var labelHost = new Label("Host : ");
        var fieldHost = new TextField("localhost");
        var labelPort = new Label("Port : ");
        var fieldPort = new TextField("61616");
        var btnConnect = new Button("Connection");
        btnConnect.setOnAction(click -> {
            fieldCode.getStyleClass().removeAll("text-error");
            fieldHost.getStyleClass().removeAll("text-error");
            fieldPort.getStyleClass().removeAll("text-error");
            if(Func.isNull(fieldCode.getText())){
                fieldCode.requestFocus();
                fieldCode.getStyleClass().add("text-error");
            }
            else if(Func.isNull(fieldHost.getText())){
                fieldHost.requestFocus();
                fieldHost.getStyleClass().add("text-error");
            }
            else if(Func.isNull(fieldPort.getText())){
                fieldPort.requestFocus();
                fieldPort.getStyleClass().add("text-error");
            }
            else{
                try{
                    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://"+fieldHost.getText()+":"+fieldPort.getText());
                    Connection connection = connectionFactory.createConnection();
                    connection.start();
                    Session session= connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
                    Destination destination= session.createTopic("enset.chat");
                    MessageConsumer consumer = session.createConsumer(destination/*,"code='"+fieldCode.getText()+"'"*/);
                    consumer.setMessageListener(msg -> {
                        try{
                            if(msg instanceof TextMessage){
                                var tm = (TextMessage)msg;
                                System.out.println("Reception de message : " +tm.getText());
                            }
                            else if(msg instanceof StreamMessage){

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                catch(Exception ex){
                    Notify.Show("Information","Error",ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
        topBox.getChildren().add(labelCode);
        topBox.getChildren().add(fieldCode);
        topBox.getChildren().add(labelHost);
        topBox.getChildren().add(fieldHost);
        topBox.getChildren().add(labelPort);
        topBox.getChildren().add(fieldPort);
        topBox.getChildren().add(btnConnect);
        bp.setTop(topBox);
        //---------------------------------------------




        Scene scene = new Scene(bp,800,500);
        scene.getStylesheets().add("/css.css");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/icon.png"));
        stage.show();
    }
}
