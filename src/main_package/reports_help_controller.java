package main_package;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class reports_help_controller {
    @FXML private Button done_button;

    /**
     * Close the Stage containing the cancel button
     */
    public void cancelButton(){
        Stage stage = (Stage) done_button.getScene().getWindow();
        stage.close();
    }
}
