package main_package;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import objects.Appointment;
import objects.Contact;
import utils.database_operation;
import utils.time_convert;

import java.time.LocalDate;

import java.time.LocalTime;

public class contacts_controller {
    @FXML private TextField appointment_timeframe;

    @FXML private TableView<Contact> contacts_table;
    @FXML private TableView<Appointment> appointments_table;

    @FXML private TableColumn<Contact, Integer> contact_id_column;
    @FXML private TableColumn<Contact, String> contact_name_column;
    @FXML private TableColumn<Contact, String> contact_email_column;

    @FXML private TableColumn<Appointment, Integer> appointment_id_column;
    @FXML private TableColumn<Appointment, String> appointment_title_column;
    @FXML private TableColumn<Appointment, String> appointment_type_column;
    @FXML private TableColumn<Appointment, String> appointment_description_column;
    @FXML private TableColumn<Appointment, LocalDate> appointment_date_column;
    @FXML private TableColumn<Appointment, LocalTime> appointment_start_column;
    @FXML private TableColumn<Appointment, LocalTime> appointment_end_column;
    @FXML private TableColumn<Appointment, Integer> appointment_customerId_column;

    // Use the same timeframe as the main window.
    public static String time_view = main_controller.time_view;
    public LocalDate timeframe_selected = main_controller.timeframe_selected;

    /**
     * Show all contacts
     * Show the selected timeframe from the main window.
     */
    public void initialize(){
        appointments_table.setPlaceholder(new Label("Select a contact to view their scheduled appointments."));
        populateContacts();
        if(time_view.equals("MONTH")){
            appointment_timeframe.setText(time_convert.formatMonthNicely(timeframe_selected));
        }
        else if(time_view.equals("WEEK")){
            appointment_timeframe.setText(time_convert.formatWeekNicely(timeframe_selected));
        }
        else{
            appointment_timeframe.setText("ALL APPOINTMENTS");
        }
    }

    /**
     * Display all contacts from the database in the GUI.
     */
    public void populateContacts(){
        ObservableList<Contact> all_contacts = database_operation.getAllContacts();
        contacts_table.setPlaceholder(new Label("No contacts found"));
        contact_id_column.setCellValueFactory(new PropertyValueFactory<>("ID"));
        contact_name_column.setCellValueFactory(new PropertyValueFactory<>("Name"));
        contact_email_column.setCellValueFactory(new PropertyValueFactory<>("Email"));
        contacts_table.getItems().setAll(all_contacts);
    }

    /**
     * When a contact is selected, get all the appointments for that contact and display them.
     *
     * Lambda: Don't show appointments if they are not for the selected contact.
     */
    public void populateAppointments(){
        // Get the selected contact.
        Contact contact_selected = contacts_table.getSelectionModel().getSelectedItem();

        // Select the correct timeframe.
        ObservableList<Appointment> appointments;
        if(time_view.equals("MONTH")) {
            appointments = database_operation.getAppointmentsByMonth(timeframe_selected);
        }
        else if (time_view.equals("WEEK")) {
            appointments = database_operation.getAppointmentsByWeek(time_convert.getStartOfWeek(timeframe_selected), time_convert.getEndOfWeek(timeframe_selected));
        }
        else {
            appointments = database_operation.getAllAppointments();
        }

        // Lambda: Don't show appointments if they are not for the selected contact.
        appointments.removeIf(appointment -> appointment.getContact_id() != contact_selected.getID());

        // Show a different placeholder if there are no appointments for the selected contact.
        if (appointments.size() > 0) {
            appointments_table.setPlaceholder(new Label("No appointments found"));
            appointment_id_column.setCellValueFactory(new PropertyValueFactory<>("Id"));
            appointment_customerId_column.setCellValueFactory(new PropertyValueFactory<>("Customer_id"));
            appointment_title_column.setCellValueFactory(new PropertyValueFactory<>("Title"));
            appointment_description_column.setCellValueFactory(new PropertyValueFactory<>("Description"));
            appointment_type_column.setCellValueFactory(new PropertyValueFactory<>("Type"));
            appointment_date_column.setCellValueFactory(new PropertyValueFactory<>("Date"));
            appointment_start_column.setCellValueFactory(new PropertyValueFactory<>("StartTimeLocal"));
            appointment_end_column.setCellValueFactory(new PropertyValueFactory<>("EndTimeLocal"));
            appointments_table.getItems().setAll(appointments);
        }
        else {
            String message = "There are no appointments for the contact " + contact_selected.getName() + ".";
            appointments_table.getItems().clear();
            appointments_table.setPlaceholder(new Label(message));
        }
    }
}
