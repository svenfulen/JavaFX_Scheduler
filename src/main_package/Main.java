package main_package;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.database_connection;

/**
 * C195 - Software II project
 * Scheduler App
 * @author Sven Fulenchek
 * @version 1.1
 */
public class Main extends Application {

    /**
     * Stores the Stage object of the login form.  This is used later to close the login form.
     */
    public static Stage loginStage = null;

    /**
     * Stores the username of the user currently logged in.
     */
    public static String user_name = "";

    /**
     * Stores the user ID of the user currently logged in.
     */
    public static int user_id;


    /**
     * Open the login window when the application starts.
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../gui/login.fxml"));
        loginStage = primaryStage;
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 250, 150));
        primaryStage.show();
    }

    /**
     * Close the login window.
     */
    public static void hideLogin() {
        loginStage.hide();
    }

    /**
     * Run the application
     * Start the database connection
     * Close the database connection when the application is over
     * @param args ...
     */
    public static void main(String[] args) {
        database_connection.startConnection();
        launch(args);
        database_connection.closeConnection();
    }
}
