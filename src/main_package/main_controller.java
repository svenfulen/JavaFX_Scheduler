package main_package;

import java.time.LocalDate;
import java.time.LocalTime;


import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import objects.Appointment;
import utils.database_operation;
import utils.time_convert;
import utils.ui_popups;

public class main_controller {
    @FXML private TableView<Appointment> appointments_table;
    @FXML private TableColumn<Appointment, Integer> app_id_column;
    @FXML private TableColumn<Appointment, Integer> cust_id_column;
    @FXML private TableColumn<Appointment, String> app_title_column;
    @FXML private TableColumn<Appointment, String> app_desc_column;
    @FXML private TableColumn<Appointment, String> app_location_column;
    @FXML private TableColumn<Appointment, String> app_contact_column;
    @FXML private TableColumn<Appointment, String> app_type_column;
    @FXML private TableColumn<Appointment, LocalDate> app_date_column;
    @FXML private TableColumn<Appointment, LocalTime> app_start_column;
    @FXML private TableColumn<Appointment, LocalTime> app_end_column;
    @FXML private RadioButton month_radio;
    @FXML private RadioButton week_radio;

    @FXML private TextField date_display_field;

    /**
     * Used globally throughout the application
     * if true shows appointments by month, else shows appointments by week
     */
    public static String time_view = "MONTH";

    /**
     * Used globally throughout the application
     * the month or week selected (current month/week by default)
     */
    public static LocalDate timeframe_selected = LocalDate.now();

    /**
     * Display alerts
     * Select the default view of the current month
     * Display the timeframe in the GUI
     */
    public void initialize(){
        getAlerts();
        selectMonthView();
        date_display_field.setText(time_convert.formatMonthNicely(timeframe_selected));
    }

    /**
     * Look through all appointments and find if there are any upcoming appointments within 15 minutes.
     * Alert the user if there are any upcoming appointments within 15 minutes.
     */
    public void getAlerts(){
        ObservableList<Appointment> appointments_within_week = database_operation.getAppointmentsByWeek(time_convert.getStartOfWeek(LocalDate.now()), time_convert.getEndOfWeek(LocalDate.now()));
        boolean upcoming_appointments = false;
        for(Appointment appointment : appointments_within_week) {
            if(time_convert.isWithin15Minutes(appointment.getStartDateTimeLocal())){
                upcoming_appointments = true;
                ui_popups.infoMessage("You have an upcoming appointment at " + appointment.getStartDateTimeLocal() + "[ID:" + appointment.getId() + "] ");
            }
        }
        if(!upcoming_appointments){
            ui_popups.infoMessage("You have no upcoming appointments within 15 minutes.");
        }
    }

    /**
     * Add 1 month to the time period currently selected.
     */
    public void incrementMonth(){ timeframe_selected = timeframe_selected.plusMonths(1); }

    /**
     * Add 1 week to the time period currently selected.
     */
    public void incrementWeek(){ timeframe_selected = timeframe_selected.plusWeeks(1); }

    /**
     * Subtract 1 month from the time period currently selected.
     */
    public void decrementMonth() { timeframe_selected = timeframe_selected.minusMonths(1); }

    /**
     * Subtract 1 week from the time period currently selected.
     */
    public void decrementWeek() { timeframe_selected = timeframe_selected.minusWeeks(1); }

    /**
     * Increment the date by 1 week or 1 month,
     * Display the time period currently selected.
     */
    public void incrementDate(){
        if(time_view.equals("MONTH")) {
            incrementMonth();
            date_display_field.setText(time_convert.formatMonthNicely(timeframe_selected));
        }
        else if (time_view.equals("WEEK")) {
            incrementWeek();
            date_display_field.setText(time_convert.formatWeekNicely(timeframe_selected));
        }
        populateAppointments();

    }

    /**
     * Decrement the date by 1 week or 1 month,
     * Display the time period currently selected.
     */
    public void decrementDate(){
        if(time_view.equals("MONTH")) {
            decrementMonth();
            date_display_field.setText(time_convert.formatMonthNicely(timeframe_selected));
        }
        else if (time_view.equals("WEEK")){
            decrementWeek();
            date_display_field.setText(time_convert.formatWeekNicely(timeframe_selected));
        }
        populateAppointments();

    }

    /**
     * Set the GUI to view appointments by month.
     * Set the time period selected to the current month.
     */
    public void selectMonthView(){
        month_radio.setSelected(true);
        week_radio.setSelected(false);
        time_view = "MONTH";
        timeframe_selected = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
        date_display_field.setText(time_convert.formatMonthNicely(timeframe_selected));
        populateAppointments();
    }

    /**
     * Set the GUI to view appointments by week.
     * Set the time period selected to the current week.
     */
    public void selectWeekView(){
        month_radio.setSelected(false);
        week_radio.setSelected(true);
        time_view = "WEEK";
        timeframe_selected = time_convert.getStartOfWeek(LocalDate.now());
        date_display_field.setText(time_convert.formatWeekNicely(timeframe_selected));
        populateAppointments();
    }

    /**
     * Set the GUI to show all appointments.
     * Set the time period selected to ALL.
     */
    public void showAllAppointments(){
        month_radio.setSelected(false);
        week_radio.setSelected(false);
        time_view = "ALL";
        timeframe_selected = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
        date_display_field.setText("All Appointments");
        populateAppointments();
    }

    /**
     * Populate the table with appointments within the selected time period.
     */
    public void populateAppointments(){
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
        appointments_table.setPlaceholder(new Label("No appointments found"));
        app_id_column.setCellValueFactory(new PropertyValueFactory<>("Id"));
        cust_id_column.setCellValueFactory(new PropertyValueFactory<>("Customer_id"));
        app_title_column.setCellValueFactory(new PropertyValueFactory<>("Title"));
        app_desc_column.setCellValueFactory(new PropertyValueFactory<>("Description"));
        app_location_column.setCellValueFactory(new PropertyValueFactory<>("Location"));
        app_type_column.setCellValueFactory(new PropertyValueFactory<>("Type"));
        app_contact_column.setCellValueFactory(new PropertyValueFactory<>("ContactName"));
        app_date_column.setCellValueFactory(new PropertyValueFactory<>("Date"));
        app_start_column.setCellValueFactory(new PropertyValueFactory<>("StartTimeLocal"));
        app_end_column.setCellValueFactory(new PropertyValueFactory<>("EndTimeLocal"));
        appointments_table.getItems().setAll(appointments);
    }

    /**
     * Open the reports menu.
     * @throws Exception JavaFX exception
     */
    public void open_Reports() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../gui/reports.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Reports");
        primaryStage.setScene(new Scene(root, 325, 435));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Open the Customers menu.
     * @throws Exception JavaFX exception
     */
    public void open_customerTable() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../gui/customers_table.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Customers");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Open the Add Appointment menu.
     * @throws Exception JavaFX exception
     *
     *  Lambda - When the Add Appointment window is closed, refresh the Appointments table.
     */
    public void open_AddAppointment() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../gui/add_appointment.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("New Appointment");
        primaryStage.setScene(new Scene(root, 515, 340));
        primaryStage.setResizable(false);
        primaryStage.show();
        // Lambda - When the Add Appointment window is closed, refresh the Appointments table.
        primaryStage.setOnHiding( event -> populateAppointments());
    }

    /**
     * Open the Edit Appointment menu.
     * @throws Exception JavaFX exception
     *
     * // Lambda - When the Edit Appointment window is closed, refresh the Appointments table.
     */
    public void open_modifyAppointment() throws Exception {
        // Get the selected item from the appointments table
        Appointment selected_appointment = appointments_table.getSelectionModel().getSelectedItem();

        // If there is no selected item, do not open the Edit appointment menu, instead display a message.
        if(selected_appointment != null){
            modify_appointment_controller.selected_appointment = selected_appointment;
            Parent root = FXMLLoader.load(getClass().getResource("../gui/modify_appointment.fxml"));
            Stage primaryStage = new Stage();
            primaryStage.setTitle("New Appointment");
            primaryStage.setScene(new Scene(root, 515, 340));
            primaryStage.setResizable(false);
            primaryStage.show();
            // Lambda - When the Edit Appointment window is closed, refresh the Appointments table.
            primaryStage.setOnHiding( event -> populateAppointments());
        }
        else {
            utils.ui_popups.infoMessage("Please select an appointment.");
        }
    }

    /**
     * Delete an appointment in the database.
     * Allows a user to cancel appointments.
     * @see database_operation
     */
    public void delete_Appointment() {
        Appointment selected = appointments_table.getSelectionModel().getSelectedItem();
        if(selected != null) {
            if (database_operation.delete_appointment(selected)) {
                utils.ui_popups.infoMessage("Appointment ID  " + selected.getId() + " - " + selected.getType() + " was successfully cancelled.");
                populateAppointments();
            }
        }
        else {
            ui_popups.errorMessage("Please select an Appointment.");
        }

    }

}
