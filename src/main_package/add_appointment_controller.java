package main_package;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import objects.Appointment;
import objects.Contact;
import objects.Customer;
import utils.database_operation;
import utils.ui_popups;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class add_appointment_controller {
    // Text Inputs
    @FXML private TextField title_field;
    @FXML private TextArea description_field;
    @FXML private TextField location_field;
    @FXML private TextField type_field;

    // Drop-down selections
    @FXML private ChoiceBox<Customer> customer_select;
    @FXML private ChoiceBox<Contact> contact_select;
    @FXML private ChoiceBox<database_operation.User> user_select;

    // Date Selections
    @FXML private DatePicker date_select;
    @FXML private Spinner<String> start_spinner_hours;
    @FXML private Spinner<String> start_spinner_minutes;
    @FXML private Spinner<String> end_spinner_hours;
    @FXML private Spinner<String> end_spinner_minutes;

    // Buttons
    @FXML private Button cancel_button;

    /**
     * Populate selectable items in the menu.
     */
    public void initialize(){
        initSelections();
    }

    /**
     * Closes the Stage that contains the cancel button.
     */
    public void cancelButton(){
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        stage.close();
    }

    /**
     * Set up selectable items in the menu (Hours, Minutes, Customer, Contact)
     */
    private void initSelections(){
        // add hours selections
        ObservableList<String> hours = FXCollections.observableArrayList();
        hours.add("01"); hours.add("02"); hours.add("03"); hours.add("04"); hours.add("05"); hours.add("06");
        hours.add("07"); hours.add("08"); hours.add("09"); hours.add("10"); hours.add("11"); hours.add("12");
        hours.add("13"); hours.add("14"); hours.add("15"); hours.add("16"); hours.add("17"); hours.add("18");
        hours.add("19"); hours.add("20"); hours.add("21"); hours.add("22"); hours.add("23"); hours.add("00");
        start_spinner_hours.setValueFactory( new SpinnerValueFactory.ListSpinnerValueFactory<String>(hours) );
        end_spinner_hours.setValueFactory( new SpinnerValueFactory.ListSpinnerValueFactory<String>(hours) );

        // add minutes selections
        ObservableList<String> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) {
            if (i < 10) { minutes.add("0" + Integer.toString(i)); }
            else { minutes.add(Integer.toString(i)); }
        }
        start_spinner_minutes.setValueFactory( new SpinnerValueFactory.ListSpinnerValueFactory<String>(minutes) );
        end_spinner_minutes.setValueFactory( new SpinnerValueFactory.ListSpinnerValueFactory<String>(minutes) );

        //add customer, user, and contact selections
        customer_select.setItems(database_operation.getAllCustomers());
        contact_select.setItems(database_operation.getAllContacts());
        user_select.setItems(database_operation.getUsers());

    }

    /**
     * Retrieve all of the fields from the form and create an object to submit to the database.
     */
    public void addAppointment() {
        // Get selected values
        String title = title_field.getText();
        String description = description_field.getText();
        String location = location_field.getText();
        String type = type_field.getText();

        // 0 is an invalid ID so the query wouldn't work.
        int contact_id = 0;
        int customer_id = 0;

        // Only get the ID from the selection boxes if there is a selection made.
        if(contact_select.getSelectionModel().getSelectedItem() != null){
            contact_id = contact_select.getSelectionModel().getSelectedItem().getID();
        }
        if(customer_select.getSelectionModel().getSelectedItem() != null){
            customer_id = customer_select.getSelectionModel().getSelectedItem().getId();
        }

        LocalDate date_selected = date_select.getValue();

        // Get all the spinner values and combine them with the date to create start and end LocalDateTime
        int start_hours = Integer.parseInt(start_spinner_hours.getValue());
        int start_minutes = Integer.parseInt(start_spinner_minutes.getValue());
        int end_hours = Integer.parseInt(end_spinner_hours.getValue());
        int end_minutes = Integer.parseInt(end_spinner_minutes.getValue());

        // Check the form for errors
        boolean canSubmit = true;
        String problem = "";

        if(user_select.getSelectionModel().getSelectedItem() == null){
            canSubmit = false;
            problem = "Please select a User.";
        }
        if(contact_select.getSelectionModel().getSelectedItem() == null){
            canSubmit = false;
            problem = "Please select a Contact.";
        }
        if(date_select.getValue() == null || date_selected == null) {
            canSubmit = false;
            problem = "Please select a Date.";
        }
        if(type_field.getText() == null || type.isBlank()){
            canSubmit = false;
            problem = "Please enter an appointment Type.";
        }
        if(location_field.getText() == null || location.isBlank()){
            canSubmit = false;
            problem = "Please enter a Location.";
        }
        if(customer_select.getSelectionModel().getSelectedItem() == null){
            canSubmit = false;
            problem = "Please select a Customer.";
        }
        if(description_field.getText() == null || description.isBlank()){
            canSubmit = false;
            problem = "Please enter a Description.";
        }
        if(title_field.getText() == null || title.isBlank()) {
            canSubmit = false;
            problem = "Please enter a Title.";
        }

        // Attempt to add the appointment only if the form is filled completely, else display an error message
        if (canSubmit) {
            // Convert start and end time to LocalTime objects
            LocalTime start_time = LocalTime.of(start_hours, start_minutes);
            LocalTime end_time = LocalTime.of(end_hours, end_minutes);

            // Create LocalDateTime objects
            LocalDateTime start_ldt = LocalDateTime.of(date_selected, start_time);
            LocalDateTime end_ldt = LocalDateTime.of(date_selected, end_time);

            // Create an Appointment object
            Appointment new_appointment = new Appointment(contact_id, customer_id, Main.user_id, title, description, location, type, start_ldt, end_ldt);

            //Check if the appointment overlaps with any other existing appointments.
            String overlaps = database_operation.checkOverlaps(new_appointment);

            // Add the appointment to the database if the appointment is within business hours and does not overlap with other appointments.
            if (utils.time_convert.isWithinBusinessHours(start_ldt, end_ldt)) {
                if (overlaps.equals("false")) {
                    database_operation.add_appointment(new_appointment);
                    cancelButton();
                } else {
                    ui_popups.errorMessage(overlaps);
                }
            } else {
                System.out.println(start_ldt);
                System.out.println(end_ldt);
                ui_popups.errorMessage("The selected appointment time is outside of business hours in EST.");
            }

        }
        else {
            ui_popups.errorMessage(problem);
        }

    }



}
