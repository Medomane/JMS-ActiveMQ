import helpers.Func;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

public class JMSChat extends Application {
    MessageProducer producer;
    Session session;
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
        topBox.getChildren().add(labelCode);
        topBox.getChildren().add(fieldCode);
        topBox.getChildren().add(labelHost);
        topBox.getChildren().add(fieldHost);
        topBox.getChildren().add(labelPort);
        topBox.getChildren().add(fieldPort);
        topBox.getChildren().add(btnConnect);
        bp.setTop(topBox);
        //---------------------------------------------
        //Center
        var fieldWidth = 350;
        var gp = new GridPane();
        gp.setPadding(new Insets(10));
        gp.setAlignment(Pos.TOP_LEFT);
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setMaxWidth(fieldWidth+150);

        var labelTo = new Label("To : ");
        var fieldTo = new TextField();
        fieldTo.setMaxWidth(fieldWidth);

        var labelMessage = new Label("Message : ");
        var fieldMessage = new TextArea();
        fieldMessage.setMaxWidth(fieldWidth);
        fieldMessage.setPrefRowCount(2);

        var labelImage = new Label("Image : ");
        var images = new File(getClass().getResource("/images").toURI()).list();
        ObservableList<String> list = FXCollections.observableArrayList(images);
        var fieldImage = new ComboBox<>(list);
        fieldImage.setMinWidth(fieldWidth);
        fieldImage.getSelectionModel().select(0);

        var btnSend = new Button("Send");

        gp.add(labelTo,0,0);
        gp.add(fieldTo,1,0);
        gp.add(labelMessage,0,1);
        gp.add(fieldMessage,1,1);
        gp.add(labelImage,0,2);
        gp.add(fieldImage,1,2);
        gp.add(btnSend,2,2);

        TitledPane centerPane = new TitledPane("Message : ", gp);
        centerPane.setPadding(new Insets(5));
        centerPane.setCollapsible(false);
        centerPane.setDisable(true);
        bp.setCenter(centerPane);
        //---------------------------------------------
        //Bottom
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setSpacing(10);

        ObservableList<String> msgList = FXCollections.observableArrayList();
        var listField = new ListView<>(msgList);
        bottomBox.getChildren().add(listField);
        listField.setMaxHeight(200);
        listField.setMaxWidth(200);

        var selectedImage = new Image(getClass().getResource("/images").toURI()+"/"+fieldImage.getSelectionModel().getSelectedItem());
        ImageView iv = new ImageView(selectedImage);
        iv.setFitHeight(200);
        iv.setFitWidth(200);
        bottomBox.getChildren().add(iv);

        TitledPane bottomPane = new TitledPane("Result", bottomBox);
        bottomPane.setPadding(new Insets(5));
        bottomPane.setCollapsible(false);
        bp.setBottom(bottomPane);
        //---------------------------------------------

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
                    session= connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
                    Destination destination= session.createTopic("enset.chat");
                    MessageConsumer consumer = session.createConsumer(destination,"code='"+fieldCode.getText()+"'");
                    producer = session.createProducer(destination);
                    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                    consumer.setMessageListener(msg -> {
                        try{
                            if(msg instanceof TextMessage){
                                var tm = (TextMessage)msg;
                                System.out.println("Reception de message : " +tm.getText());
                                msgList.add(tm.getText());
                            }
                            else if(msg instanceof StreamMessage){
                                var sm = (StreamMessage)msg;
                                System.out.println("Reception de d'une image : " +sm.readString());
                                int size = sm.readInt();
                                var data = new byte[size];
                                sm.readBytes(data);
                                var bis = new ByteArrayInputStream(data);
                                var img = new Image(bis);
                                iv.setImage(img);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    topBox.setDisable(true);
                    centerPane.setDisable(false);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        btnSend.setOnAction(click -> {
            fieldTo.getStyleClass().removeAll("text-error");
            fieldMessage.getStyleClass().removeAll("text-error");
            if(Func.isNull(fieldTo.getText())){
                fieldTo.requestFocus();
                fieldTo.getStyleClass().add("text-error");
            }
            else if(Func.isNull(fieldMessage.getText())){
                fieldMessage.requestFocus();
                fieldMessage.getStyleClass().add("text-error");
            }
            else{
                try {
                    var sMsg = session.createStreamMessage();
                    sMsg.setStringProperty("code",fieldTo.getText());
                    var f = new File(getClass().getResource("/images/"+fieldImage.getSelectionModel().getSelectedItem()).toURI());
                    byte[] fis = new FileInputStream(f).readAllBytes();
                    sMsg.writeString(fieldImage.getSelectionModel().getSelectedItem());
                    sMsg.writeInt(fis.length);
                    sMsg.writeBytes(fis);
                    producer.send(sMsg);

                    var msg = session.createTextMessage();
                    msg.setText(fieldMessage.getText());
                    msg.setStringProperty("code",fieldTo.getText());
                    producer.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        fieldImage.getSelectionModel().selectedItemProperty().addListener((observableValue, oldItem, newItem) -> {
            try {
                iv.setImage(new Image(getClass().getResource("/images").toURI()+"/"+newItem));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Scene scene = new Scene(bp,720,500);
        scene.getStylesheets().add("/css.css");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/icon.png"));
        stage.show();
    }
}
