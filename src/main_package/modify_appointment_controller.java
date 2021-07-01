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

public class modify_appointment_controller {
    @FXML private TextField id_field;
    @FXML private TextField title_field;
    @FXML private TextArea description_field;
    @FXML private TextField location_field;
    @FXML private TextField type_field;

    @FXML private ChoiceBox<database_operation.User> user_select;
    @FXML private ChoiceBox<Customer> customer_select;
    @FXML private ChoiceBox<Contact> contact_select;

    @FXML private DatePicker date_select;
    @FXML private Spinner<String> start_spinner_hours;
    @FXML private Spinner<String> start_spinner_minutes;
    @FXML private Spinner<String> end_spinner_hours;
    @FXML private Spinner<String> end_spinner_minutes;


    @FXML private Button cancel_button;

    static Appointment selected_appointment;

    /**
     * Populate the form with data from the selected appointment.
     *
     * Lambda - select the customer for this appointment
     * Lambda - select the contact for this appointment
     * Lambda - select the user for this appointment
     */
    public void initialize(){
        // Add options to the form
        initSelections();

        // Fill the text fields with the selected appointment data
        id_field.setText(String.valueOf(selected_appointment.getId()));
        title_field.setText(selected_appointment.getTitle());
        description_field.setText(selected_appointment.getDescription());
        location_field.setText(selected_appointment.getLocation());
        type_field.setText(selected_appointment.getType());
        location_field.setText(selected_appointment.getLocation());

        ObservableList<Customer> customers_list = customer_select.getItems();
        //Lambda - select the customer for this appointment
        customers_list.forEach(customer -> {
            if(customer.getId() == selected_appointment.getCustomer_id()) {
                customer_select.getSelectionModel().select(customer);
            }
        });

        ObservableList<Contact> contacts_list = contact_select.getItems();
        //Lambda - select the contact for this appointment
        contacts_list.forEach(contact -> {
            if(contact.getID() == selected_appointment.getContact_id()){
                contact_select.getSelectionModel().select(contact);
            }
        });

        ObservableList<database_operation.User> users_list = user_select.getItems();
        //Lambda - select the user for this appointment
        users_list.forEach(user -> {
            if(user.getId() == selected_appointment.getUser_id()){
                user_select.getSelectionModel().select(user);
            }
        });

        // Fill the date for the selected appointment
        date_select.setValue(selected_appointment.getDate());

        // Fill the times for the selected appointment
        int start_time_h = selected_appointment.getStartTimeLocal().getHour();
        int start_time_m = selected_appointment.getStartTimeLocal().getMinute();
        int end_time_h = selected_appointment.getEndTimeLocal().getHour();
        int end_time_m = selected_appointment.getEndTimeLocal().getMinute();

        // Format as 00:00 string values
        String start_time_h_str = String.valueOf(start_time_h);
        String start_time_m_str = String.valueOf(start_time_m);
        String end_time_h_str = String.valueOf(end_time_h);
        String end_time_m_str = String.valueOf(end_time_m);
        if(start_time_h < 10){ start_time_h_str = "0" + start_time_h_str; }
        if(start_time_m < 10){ start_time_m_str = "0" + start_time_m_str; }
        if(end_time_h < 10){ end_time_h_str = "0" + end_time_h_str; }
        if(end_time_m < 10){ end_time_m_str = "0" + end_time_m_str; }

        start_spinner_hours.getValueFactory().setValue(start_time_h_str);
        start_spinner_minutes.getValueFactory().setValue(start_time_m_str);
        end_spinner_hours.getValueFactory().setValue(end_time_h_str);
        end_spinner_minutes.getValueFactory().setValue(end_time_m_str);

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

        //add customer and contact selections
        customer_select.setItems(database_operation.getAllCustomers());
        contact_select.setItems(database_operation.getAllContacts());
        user_select.setItems(database_operation.getUsers());

    }

    /**
     * Retrieve all of the fields from the form and create an object to submit to the database.
     */
    public void addAppointment(){
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

        // Only modify the appointment if the form is filled correctly. else, display an error message
        if(canSubmit) {
            // Convert start and end time to LocalTime objects
            LocalTime start_time = LocalTime.of(start_hours, start_minutes);
            LocalTime end_time = LocalTime.of(end_hours, end_minutes);

            // Create LocalDateTime objects
            LocalDateTime start_ldt = LocalDateTime.of(date_selected, start_time);
            LocalDateTime end_ldt = LocalDateTime.of(date_selected, end_time);

            // Create an Appointment object
            Appointment new_appointment = new Appointment(contact_id, customer_id, Main.user_id, title, description, location, type, start_ldt, end_ldt);

            // Check if the appointment overlaps with any other existing appointments.
            String overlaps = database_operation.checkOverlaps(new_appointment, selected_appointment.getId());

            // Add the appointment to the database if the appointment is within business hours and does not overlap with other appointments.
            if (utils.time_convert.isWithinBusinessHours(start_ldt, end_ldt)) {
                if (overlaps.equals("false")) {
                    database_operation.update_appointment(new_appointment, selected_appointment.getId());
                    cancelButton();
                } else {
                    ui_popups.errorMessage(overlaps);
                }
            } else {
                ui_popups.errorMessage("The selected appointment time is outside of business hours in EST.");
            }
        }
        else{
            utils.ui_popups.errorMessage(problem);
        }

    }



}
