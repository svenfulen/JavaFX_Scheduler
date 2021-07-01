package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ui_popups {

    /**
     * Generates a popup window with an OK button and an error message.
     * @param errorText The error message to display.
     */
    public static void errorMessage(String errorText){
        Alert error = new Alert(Alert.AlertType.ERROR, errorText, ButtonType.OK);
        error.showAndWait();
        //if(error.getResult() == ButtonType.OK){}
    }

    /**
     * Generates a popup window with an OK button and an information message.
     * @param infoText The message to display.
     */
    public static void infoMessage(String infoText){
        Alert info = new Alert(Alert.AlertType.INFORMATION, infoText, ButtonType.OK);
        info.showAndWait();
    }
}
