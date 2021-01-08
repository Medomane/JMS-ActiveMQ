package helpers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Notify {
    public String title ;
    public String header ;
    public String message;
    public Alert.AlertType type ;
    public Notify(String title, String header, String message, Alert.AlertType type){
        this.title = title;
        this.header = header;
        this.message = message;
        this.type = type;
    }
    public void alert(){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(header);
        alert.showAndWait();
    }
    public boolean confirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(header);
        Optional<ButtonType> option = alert.showAndWait();
        return option.get() == ButtonType.OK;
    }

    public static void Show(String title, String header, String message, Alert.AlertType type){
        Notify not = new Notify(title,header,message,type);
        not.alert();
    }
    public static boolean Confirm(String title, String header, String message){
        Notify not = new Notify(title,header,message, Alert.AlertType.CONFIRMATION);
        return not.confirm();
    }
}
