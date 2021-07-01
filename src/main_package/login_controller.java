package main_package;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import utils.database_connection;
import utils.database_query;
import utils.ui_popups;
import utils.translate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

public class login_controller {

    @FXML private Button login_button;
    @FXML private PasswordField password_field;
    @FXML private TextField username_field;
    @FXML private Label username_label;
    @FXML private Label password_label;
    @FXML private Label locale_label;

    /**
     * Determine the language for the login form and display the language as French if it is the system language.
     */
    public void initialize(){
        ResourceBundle rb_fr = ResourceBundle.getBundle("resource_bundles/main_fr", Locale.getDefault());

        // Get the default language from the JVM and translate the UI to french if needed
        if(Locale.getDefault().getLanguage().equals("fr")) {
            locale_label.setText("FR");
            translate.translateText(login_button, rb_fr);
            translate.translateText(username_label, rb_fr);
            translate.translateText(password_label, rb_fr);
        }
    }

    /**
     * Open the main window (this function is called upon successful login)
     * @throws Exception JavaFX exception
     */
    public void loginSuccess() throws Exception {
        utils.log.record_login(true);
        Parent root = FXMLLoader.load(getClass().getResource("../gui/main.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Scheduler");
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Use the username and password entered and attempt to log in.
     * Display an error message if login fails.
     * @throws Exception JavaFX Exception
     */
    public void submitLoginForm() throws Exception{
        ResourceBundle rb_fr = ResourceBundle.getBundle("resource_bundles/main_fr", Locale.getDefault());

        // set up database connection
        Connection conn = database_connection.getConnection();
        database_query.setStatement(conn);
        Statement statement = database_query.getStatement();

        // attempt login using form data
        String password = password_field.getText();
        String username = username_field.getText();
        String query = "SELECT User_ID, User_Name, Password FROM users WHERE User_Name = \"" + username + "\" AND Password = \"" + password + "\"";
        statement.execute(query);
        // find matching credentials in the database
        String matchingUsername = null;
        int matchingUserId = 0;
        ResultSet loginsFound = statement.getResultSet();
        while(loginsFound.next()){
            matchingUsername = loginsFound.getString("User_Name");
            matchingUserId = loginsFound.getInt("User_ID");
        }

        // Log in if the credentials are valid
        if (matchingUsername == null) {
            utils.log.record_login(false);
            if(Locale.getDefault().getLanguage().equals("fr")) {
                ui_popups.errorMessage(rb_fr.getString("LoginError"));
            }
            else {
                ui_popups.errorMessage("You have entered an invalid username or password.");
            }
        }
        else {
            // Store user_name and user_id in the Main class to be accessed by the other parts of the application
            System.out.println("Login success, user: " + matchingUsername);
            Main.user_name = matchingUsername;
            Main.user_id = matchingUserId;
            loginSuccess();
            Main.hideLogin();
        }

    }

}
