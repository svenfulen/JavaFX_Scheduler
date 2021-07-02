package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main_package.Main;
import objects.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;

public class database_operation {

    /**
     * Check if an appointment overlaps any other appointment when creating a new appointment.
     * @param new_appointment Appointment object not containing an Appointment ID attribute.
     * @return "false" (String) if there are no other appointments overlapping.
     */
    public static String checkOverlaps(Appointment new_appointment){
        ObservableList<Appointment> all_appointments = getAllAppointments();
        String Message = "false";

        // Check if the appointment overlaps any other appointments.
        for(Appointment appointment : all_appointments) {
            if (time_convert.overlaps(new_appointment.getStartDateTimeLocal(), new_appointment.getEndDateTimeLocal(), appointment.getStartDateTimeLocal(), appointment.getEndDateTimeLocal())){
                Message = "There is already an existing appointment during this time period for the contact " + appointment.getContactName() + ".";
            }
        }
        return Message;
    }

    /**
     * Check if an appointment overlaps any other appointment when updating an appointment.
     * @param new_appointment Appointment object containing an Appointment ID attribute.
     * @param appointment_id Appointment ID of the appointment being updated.
     * @return "false" (String) if there are no other appointments overlapping.
     *
     * Lambda: Remove the appointment being checked so that it's not checked against itself.
     */
    public static String checkOverlaps(Appointment new_appointment, int appointment_id){
        ObservableList<Appointment> all_appointments = getAllAppointments();
        String Message = "false";

        // Lambda: Remove the appointment being checked so that it's not checked against itself.
        all_appointments.removeIf(appointment -> appointment.getId() == appointment_id);

        // Check if the appointment overlaps any other appointments.
        for(Appointment appointment : all_appointments) {
            if (time_convert.overlaps(new_appointment.getStartDateTimeLocal(), new_appointment.getEndDateTimeLocal(), appointment.getStartDateTimeLocal(), appointment.getEndDateTimeLocal())){
                Message = "There is already an existing appointment during this time period for the contact " + appointment.getContactName() + ".";
            }
        }
        return Message;
    }

    /**
     * Helper function used to create a list of results from a SQL query.
     * This function's purpose is to simplify/reduce code.
     * @param query The SQL query to execute.
     * @return ResultSet containing the results of query
     */
    public static ResultSet getQueryResults(String query){
        ResultSet resultSet = null;
        try{
            Connection conn = database_connection.getConnection();
            database_query.setStatement(conn);
            Statement statement = database_query.getStatement();
            statement.execute(query);
            resultSet = statement.getResultSet();
        }
        catch(SQLException querySqlException){
            querySqlException.printStackTrace();
        }
        return resultSet;
    }

    /**
     * Get all divisions available for a country.
     * @param selected The country to retrieve divisions for.
     * @return ObservableList of Division objects for the Country selected.
     */
    public static ObservableList<Division> getDivisions(Country selected){
        ObservableList<Division> selectedDivisions = FXCollections.observableArrayList();
        try {
            ResultSet countryMatches = getQueryResults("select division_id, division from first_level_divisions where country_id = " + selected.getId());
            while(countryMatches.next()) {
                int newCountryId = selected.getId();
                int newDivisionId = countryMatches.getInt("Division_ID");
                String newDivisionName = countryMatches.getString("Division");
                Division newDivision = new Division(newDivisionName, newDivisionId, newCountryId);
                selectedDivisions.add(newDivision);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return selectedDivisions;
    }

    /**
     * Use an integer representing Division ID to get an integer representing a Country ID.
     * @param division_id The division ID to use.
     * @return integer representing Country ID.
     */
    public static int getCountryIdFromDivisionId(int division_id){
        int country_id_found = 0;
        try {
            ResultSet countryMatches = getQueryResults("select Country_ID from first_level_divisions where Division_ID = " + division_id);
            while(countryMatches.next()) {
                country_id_found = countryMatches.getInt("Country_ID");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return country_id_found;
    }

    /**
     * Get a contact using the contact's ID.
     * @param contact_id The contact ID to search for.
     * @return Contact object with the data from the contact with the matching contact ID.
     */
    public static Contact getContactById(int contact_id) {
        Contact contactFound = null;
        try {
            ResultSet contactsFound = getQueryResults("SELECT * from contacts WHERE Contact_ID = " + contact_id);
            while (contactsFound.next()){
                int id = contactsFound.getInt("Contact_ID");
                String name = contactsFound.getString("Contact_Name");
                String email = contactsFound.getString("Email");
                contactFound = new Contact(id, name, email);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return contactFound;
    }

    /**
     * Get a list of all contacts from the database.
     * @return ObservableList of Contact objects representing all Contacts in the database.
     */
    public static ObservableList<Contact> getAllContacts(){
        ObservableList<Contact> allContacts = FXCollections.observableArrayList();
        try {
            ResultSet contact_info = getQueryResults("SELECT * FROM contacts");
            while (contact_info.next()) {
                int id = contact_info.getInt("Contact_ID");
                String name = contact_info.getString("Contact_Name");
                String email = contact_info.getString("Email");
                Contact new_contact = new Contact(id, name, email);
                allContacts.add(new_contact);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return allContacts;
    }

    /**
     * Get a list of all customers from the database.
     * @return An ObservableList of Customer objects representing all customers in the database.
     */
    public static ObservableList<Customer> getAllCustomers(){
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        try {
            ResultSet customer_info = getQueryResults("SELECT Customer_ID, Customer_Name, Phone, Address, Postal_Code, Division_ID FROM customers");
            while (customer_info.next()) {
                String new_name = customer_info.getString("Customer_Name");
                String new_address = customer_info.getString("Address");
                String new_zip = customer_info.getString("Postal_Code");
                String new_phone = customer_info.getString("Phone");
                int new_division_id = customer_info.getInt("Division_ID");
                int new_customer_id = customer_info.getInt("Customer_ID");
                Customer new_customer = new Customer(new_customer_id, new_name, new_address, new_zip, new_phone, new_division_id);
                allCustomers.add(new_customer);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            ui_popups.errorMessage("Unable to retrieve data. Please check your internet connection.");
        }
        return allCustomers;
    }

    /**
     * Get a list of all appointments in the database.
     * @return ObservableList of Appointment objects representing all appointments in the database.
     */
    public static ObservableList<Appointment> getAllAppointments(){
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        try{
            ResultSet appointments = getQueryResults("SELECT * FROM appointments");
            while(appointments.next()){
                int appointment_id = appointments.getInt("Appointment_ID");
                int contact_id = appointments.getInt("Contact_ID");
                int customer_id = appointments.getInt("Customer_ID");
                int user_id = appointments.getInt("User_ID");
                String app_title = appointments.getString("Title");
                String app_desc = appointments.getString("Description");
                String app_location = appointments.getString("Location");
                String app_type = appointments.getString("Type");
                String start_utc = appointments.getString("Start");
                String end_utc = appointments.getString("End");
                Appointment new_appointment = new Appointment(appointment_id,contact_id,customer_id,user_id,app_title,app_desc,app_location,app_type,start_utc,end_utc);
                allAppointments.add(new_appointment);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            ui_popups.errorMessage("Unable to retrieve data. Please check your internet connection.");
        }
        return allAppointments;
    }

    /**
     * Get all appointments for a given month.
     * @param date LocalDate object with the first day of the month
     * @return ObservableList of Appointment objects
     */
    public static ObservableList<Appointment> getAppointmentsByMonth(LocalDate date){
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        YearMonth thisYearMonth = YearMonth.of(date.getYear(), date.getMonth());
        LocalDate end_month = thisYearMonth.atEndOfMonth();
        LocalDate start_month = LocalDate.of(date.getYear(), date.getMonth(), 1);
        try {
            ResultSet appointments = getQueryResults("SELECT * FROM appointments WHERE Start >= TIMESTAMP('" +
                    start_month + " 00:00:00', 'yyyy-mm-dd hh24:mi:ss')  AND Start <= TIMESTAMP('" +
                    end_month + " 23:59:59', 'yyyy-mm-dd hh24:mi:ss')");
            while (appointments.next()) {
                int appointment_id = appointments.getInt("Appointment_ID");
                int contact_id = appointments.getInt("Contact_ID");
                int customer_id = appointments.getInt("Customer_ID");
                int user_id = appointments.getInt("User_ID");
                String app_title = appointments.getString("Title");
                String app_desc = appointments.getString("Description");
                String app_location = appointments.getString("Location");
                String app_type = appointments.getString("Type");
                String start_utc = appointments.getString("Start");
                String end_utc = appointments.getString("End");
                Appointment new_appointment = new Appointment(appointment_id, contact_id, customer_id, user_id, app_title, app_desc, app_location, app_type, start_utc, end_utc);
                allAppointments.add(new_appointment);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            ui_popups.errorMessage("Unable to retrieve data. Please check your internet connection.");
        }
        return allAppointments;
    }

    /**
     * Get all appointments for a given week.
     * @param week_start LocalDate object with the first day of the week.
     * @return ObservableList of appointment objects
     */
    public static ObservableList<Appointment> getAppointmentsByWeek(LocalDate week_start, LocalDate week_end){
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        String start = week_start.toString();
        String end = week_end.toString();

        try {
            ResultSet appointments = getQueryResults("SELECT * FROM appointments WHERE Start >= TIMESTAMP('" +
                    start + " 00:00:00', 'yyyy-mm-dd hh24:mi:ss')  AND Start <= TIMESTAMP('" +
                    end + " 23:59:59', 'yyyy-mm-dd hh24:mi:ss')");
            while (appointments.next()) {
                int appointment_id = appointments.getInt("Appointment_ID");
                int contact_id = appointments.getInt("Contact_ID");
                int customer_id = appointments.getInt("Customer_ID");
                int user_id = appointments.getInt("User_ID");
                String app_title = appointments.getString("Title");
                String app_desc = appointments.getString("Description");
                String app_location = appointments.getString("Location");
                String app_type = appointments.getString("Type");
                String start_utc = appointments.getString("Start");
                String end_utc = appointments.getString("End");
                Appointment new_appointment = new Appointment(appointment_id, contact_id, customer_id, user_id, app_title, app_desc, app_location, app_type, start_utc, end_utc);
                allAppointments.add(new_appointment);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            ui_popups.errorMessage("Unable to retrieve data. Please check your internet connection.");
        }
        catch(NullPointerException n){
            System.out.println("The query for weekly appointments returned no results.");
            return allAppointments;
        }
        return allAppointments;
    }

    /**
     * Add a customer to the database.
     * @param new_customer Customer object containing the data to add to the customers table.
     */
    public static void add_customer(Customer new_customer){
        String new_name = new_customer.getName();
        String new_address = new_customer.getAddress();
        String new_zip = new_customer.getZip();
        String new_phone = new_customer.getPhone();
        int new_division_id = new_customer.getDivisionId();
        try {
            Connection conn = database_connection.getConnection();
            database_query.setStatement(conn);
            Statement statement = database_query.getStatement();
            String query = "INSERT INTO customers (CUSTOMER_NAME, PHONE, ADDRESS, POSTAL_CODE,  DIVISION_ID) values(" +
                    "'"+ new_name + "'"+ "," +"'"+ new_phone +"'"+ "," +"'"+ new_address +"'"+ "," +"'"+ new_zip +"'"+ "," + new_division_id + ")";
            statement.execute(query);
        }
        catch(SQLException e){
            e.printStackTrace();
            ui_popups.errorMessage("Unable to add the customer.  Please check your internet connection.");
        }
        finally{
            ui_popups.infoMessage(new_name + " successfully added.");
        }
    }

    /**
     * Edit an existing customer in the database.
     * @param new_customer Customer object containing the new information to be applied to an existing row in the customers table.
     */
    public static void edit_customer(Customer new_customer) {
        String new_name = new_customer.getName();
        String new_address = new_customer.getAddress();
        String new_zip = new_customer.getZip();
        String new_phone = new_customer.getPhone();
        int new_division_id = new_customer.getDivisionId();
        int customer_id = new_customer.getId();
        try {
            Connection conn = database_connection.getConnection();
            database_query.setStatement(conn);
            Statement statement = database_query.getStatement();
            String query = "UPDATE customers SET customer_name = '" + new_name
                    + "'," + "phone = '" + new_phone
                    + "'," + "address = '" + new_address
                    + "'," + "postal_code = '" + new_zip
                    + "'," + "division_id = " + new_division_id
                    + " WHERE Customer_ID = " + customer_id;
            System.out.println("debug: Modified customer " + customer_id);
            statement.execute(query);
        }
        catch(SQLException e){
            e.printStackTrace();
            ui_popups.errorMessage("Unable to edit customer " + customer_id + ".  Please check your internet connection.");
        }
        finally {
            ui_popups.infoMessage(new_name + " successfully updated.");
        }
    }

    /**
     * Delete all appointments associated with a customer, then delete the customer.
     * @param to_delete Customer object containing a Customer ID attribute.
     */
    public static void delete_customer(Customer to_delete) {
        int customer_id = to_delete.getId();
        // Delete all appointments for the customer
        String delete_query_1 = "DELETE FROM appointments WHERE Customer_ID = " + customer_id;
        // Delete the customer
        String delete_query_2 = "DELETE FROM customers WHERE Customer_ID = " + customer_id;
        try {
            Connection conn = database_connection.getConnection();
            database_query.setStatement(conn);
            Statement statement = database_query.getStatement();
            statement.execute(delete_query_1);
            System.out.println("debug: Removing all appointments for customer " + to_delete.getId());
            statement.execute(delete_query_2);
            System.out.println("debug: Removing customer " + to_delete.getId());
        }
        catch(SQLException e){
            e.printStackTrace();
            ui_popups.errorMessage("Unable to remove customer " + customer_id + ".  Please check your internet connection.");
        }
        finally{
            ui_popups.infoMessage("Customer successfully removed.");
        }
    }

    /**
     * Add an appointment to the database using an Appointment object.
     * @param new_appointment Appointment object using the constructor without the Appointment ID attribute.
     */
    public static void add_appointment(Appointment new_appointment){
        int contact_id = new_appointment.getContact_id();
        int user_id = new_appointment.getUser_id();
        int customer_id = new_appointment.getCustomer_id();
        String title = new_appointment.getTitle();
        String description = new_appointment.getDescription();
        String location = new_appointment.getLocation();
        String type = new_appointment.getType();
        String start_time = new_appointment.getStartUTC();
        String end_time = new_appointment.getEndUTC();
        try {
            Connection conn = database_connection.getConnection();
            database_query.setStatement(conn);
            Statement statement = database_query.getStatement();
            String query = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) "
                    + "VALUES ('" + title + "'," +
                    "'" + description + "'," +
                    "'" + location + "'," +
                    "'" + type + "'," +
                    "'" + start_time + "'," +
                    "'" + end_time + "'," +
                    "NOW()" +
                    ",'" + Main.user_name + "'," +
                    "NOW()" +
                    ",'" + Main.user_name + "'" +
                    "," + customer_id +
                    "," + user_id +
                    "," + contact_id +
                    "); ";
            System.out.println(query);
            statement.execute(query);
        }
        catch(SQLException e) {
            e.printStackTrace();
            ui_popups.errorMessage("Unable to add the appointment.  Please check your internet connection.");
        }
        finally {
            ui_popups.infoMessage("Appointment scheduled for " + new_appointment.getStartDateTimeLocal().toString() + ". ");
        }
    }

    /**
     * Update an appointment in the database.
     * @param new_appointment An appointment object containing the new data for the existing appointment.
     * @param appointment_id The appointment ID of the appointment to update.
     *                       This is used because the appointment constructor with LocalDateTime doesn't include the appointment ID.
     */
    public static void update_appointment(Appointment new_appointment, int appointment_id) {
        int contact_id = new_appointment.getContact_id();
        int user_id = new_appointment.getUser_id();
        int customer_id = new_appointment.getCustomer_id();
        String title = new_appointment.getTitle();
        String description = new_appointment.getDescription();
        String location = new_appointment.getLocation();
        String type = new_appointment.getType();
        String start_time = new_appointment.getStartUTC();
        String end_time = new_appointment.getEndUTC();
        try {
            Connection conn = database_connection.getConnection();
            database_query.setStatement(conn);
            Statement statement = database_query.getStatement();
            String query = "UPDATE appointments SET " +
                    "Title = " + "'" + title + "'," +
                    "Description = " + "'" + description + "'," +
                    "Location = " + "'" + location + "'," +
                    "Type = " + "'" + type + "'," +
                    "Start = " + "'" + start_time + "'," +
                    "End = " + "'" + end_time + "'," +
                    "Last_Update = NOW()," +
                    "Last_Updated_By = " + "'" + Main.user_name + "'," +
                    "Customer_ID = " + customer_id +
                    ",Contact_ID = " + contact_id +
                    ",User_ID = " + user_id +
                    " WHERE Appointment_ID = " + appointment_id + ";";
            statement.execute(query);
        }
        catch(SQLException e){
            e.printStackTrace();
            ui_popups.errorMessage("Unable to update appointment " + appointment_id + ".  Please check your internet connection.");
        }
        finally {
            ui_popups.infoMessage("Appointment " + appointment_id + " successfully updated.");
        }
    }

    /**
     * Remove an appointment from the database using an Appointment object.
     * @param to_delete Appointment object containing an Appointment ID attribute.
     * Returns true if delete is successful.
     */
    public static boolean delete_appointment(Appointment to_delete) {
        int appointment_id = to_delete.getId();
        String query = "DELETE FROM appointments WHERE Appointment_ID = " + appointment_id;
        try{
            Connection conn = database_connection.getConnection();
            database_query.setStatement(conn);
            Statement statement = database_query.getStatement();
            statement.execute(query);
        }
        catch(SQLException e) {
            e.printStackTrace();
            ui_popups.errorMessage("Failed to delete appointment " + appointment_id + ".  Please check your internet connection.");
            return false;
        }
        return true;
    }

    /**
     * Get the Name of a division using its ID
     * @param division_id The ID of the division to look up
     * @return The Division Name
     */
    public static String getDivisionById(int division_id) {
        String query = "SELECT Division FROM first_level_divisions WHERE Division_ID = " + division_id;
        String result = null;
        try{
            ResultSet division = getQueryResults(query);
            while(division.next()){
                result = division.getString("Division");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * A class that can be used to store user data.
     * This class is here because it isn't used the same way as other classes.
     */
    public static class User {
        String name;
        int id;
        String password;

        public User(int x_id, String x_name, String x_password) {
            this.name = x_name;
            this.id = x_id;
            this.password = x_password;
        }

        public String getName(){ return this.name; }
        public String getPassword(){ return this.password; }
        public int getId(){ return this.id; }

        @Override
        public String toString(){ return (name); }
    }

    /**
     * Get all users from the database.
     * @return An ObservableList of all users.
     */
    public static ObservableList<User> getUsers() {
        String query = "SELECT * FROM users";
        ObservableList<User> all_users = FXCollections.observableArrayList();
        try {
            ResultSet users = getQueryResults(query);
            while (users.next()) {
                int user_id = users.getInt("User_ID");
                String user_name = users.getString("User_Name");
                String password = users.getString("Password");

                User new_user = new User(user_id, user_name, password);
                all_users.add(new_user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return all_users;
    }
}
