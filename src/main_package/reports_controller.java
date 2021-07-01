package main_package;

import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import objects.Appointment;
import utils.database_operation;
import utils.time_convert;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class reports_controller {
    @FXML private TableView<AppointmentType> appointments_table;
    @FXML private TableColumn<AppointmentType, String> appointment_type_column;
    @FXML private TableColumn<AppointmentType, Integer> appointment_number_column;

    @FXML private Label number_appointments;
    @FXML private Label number_appointment_types;
    @FXML private Label timeframe_label;

    public static String time_view = main_controller.time_view;
    public static LocalDate timeframe_selected = main_controller.timeframe_selected;

    /**
     * Stores an Appointment type, and can store the number of that type of appointment
     */
    public static class AppointmentType{
        String type;
        int number;

        public AppointmentType(String type, int number_of_type) {
            this.type = type;
            this.number = number_of_type;
        }

        public String getType() {return this.type;}
        public int getNumber() {return this.number;}

    }

    /**
     * Show the timeframe selected, show all the reports data
     */
    public void initialize(){
        // Update the timeframe / view
        timeframe_selected = main_controller.timeframe_selected;
        time_view = main_controller.time_view;

        // Display everything in the UI.
        sortAppointmentTypeCounts();
    }

    /**
     * Show all the reports
     *
     * Lambda - Count # occurrences of each item and add them to a HashMap.
     */
    public void sortAppointmentTypeCounts(){
        // Get all appointments from the database within the selected timeframe.
        ObservableList<Appointment> appointments;
        if(time_view.equals("MONTH")) {
            appointments = database_operation.getAppointmentsByMonth(timeframe_selected);
            timeframe_label.setText(time_convert.formatMonthNicely(timeframe_selected));
        }
        else if (time_view.equals("WEEK")){
            appointments = database_operation.getAppointmentsByWeek(time_convert.getStartOfWeek(timeframe_selected), time_convert.getEndOfWeek(timeframe_selected));
            timeframe_label.setText(time_convert.formatWeekNicely(timeframe_selected));
        }
        else {
            appointments = database_operation.getAllAppointments();
            timeframe_label.setText("ALL APPOINTMENTS");
        }

        // Show the number of appointments in the UI.
        number_appointments.setText(String.valueOf(appointments.size()));

        // Sort appointments if there are appointments in the given timeframe.
        if (appointments.size() > 1){
            // Create a List for the appointment type of each appointment.
            List<String> appointment_types = new ArrayList<>();

            // Add each appointment type to the List.
            for(Appointment appointment : appointments) {
                appointment_types.add(appointment.getType());
            }

            // Convert the list into a String[] array.
            String[] itemsArray = new String[appointment_types.size()];
            itemsArray = appointment_types.toArray(itemsArray);

            // Lambda - Count # occurrences of each item and add them to a HashMap.
            Map<String, Long> types_sorted = Arrays.stream(itemsArray)
                    .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

            // Convert the map into an ObservableList that can be added to a table.
            ObservableList<AppointmentType> sorted_types_observable = FXCollections.observableArrayList();
            for (Map.Entry<String, Long> entry : types_sorted.entrySet()) {
                String key = entry.getKey();
                Long value = entry.getValue();
                sorted_types_observable.add(new AppointmentType(key, value.intValue()));
            }

            // Show the appointment types sorted in the table.
            appointments_table.setPlaceholder(new Label("No appointment types found"));
            appointment_type_column.setCellValueFactory(new PropertyValueFactory<>("Type"));
            appointment_number_column.setCellValueFactory(new PropertyValueFactory<>("Number"));
            appointments_table.getItems().setAll(sorted_types_observable);

            // Display the number of different appointment types in the UI.
            number_appointment_types.setText(String.valueOf(sorted_types_observable.size()));
        }

    }

    /**
     * Open the Schedules window where appointment schedules for each Contact can be viewed
     * @throws Exception JavaFX exception
     */
    public void open_Schedules() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../gui/contacts.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Schedules");
        primaryStage.setScene(new Scene(root, 975, 425));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Open a window with instructions for using the Reports feature.
     * @throws Exception JavaFX exception
     */
    public void open_Help() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../gui/reports_help.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Help");
        primaryStage.setScene(new Scene(root, 610, 335));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}
