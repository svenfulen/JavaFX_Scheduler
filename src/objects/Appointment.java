package objects;

import utils.database_operation;
import utils.time_convert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Appointment {
    int app_id;
    int contact_id;
    int cust_id;
    int user_id;
    String app_title;
    String app_desc;
    String app_location;
    String app_type;
    String start_time_utc;
    String end_time_utc;
    LocalDate app_date;
    LocalDateTime start_time_local;
    LocalDateTime end_time_local;

    /**
     * Appointment constructor used to retrieve Appointment object from database
     * @param app_id Appointment ID
     * @param contact_id Contact ID
     * @param cust_id Customer ID
     * @param user_id User ID
     * @param app_title Appointment Title
     * @param app_desc Appointment Description
     * @param app_location Appointment Location
     * @param app_type Appointment Type
     * @param start_time_utc Start time UTC string returned from SQL query
     * @param end_time_utc End time UTC string returned from SQL query
     */
    public Appointment(int app_id,
                       int contact_id,
                       int cust_id,
                       int user_id,
                       String app_title,
                       String app_desc,
                       String app_location,
                       String app_type,
                       String start_time_utc,
                       String end_time_utc)
    {
        this.app_id = app_id;
        this.contact_id = contact_id;
        this.user_id = user_id;
        this.cust_id = cust_id;
        this.app_title = app_title;
        this.app_desc = app_desc;
        this.app_location = app_location;
        this.app_type = app_type;

        this.start_time_utc = start_time_utc;
        this.end_time_utc = end_time_utc;
        this.start_time_local = time_convert.toLocal(start_time_utc);
        this.end_time_local = time_convert.toLocal(end_time_utc);
        this.app_date = start_time_local.toLocalDate();
    }

    /**
     * Appointment constructor used to create a new appointment in database
     * @param contact_id Contact ID
     * @param cust_id Customer ID
     * @param user_id User ID
     * @param app_title Appointment Title
     * @param app_desc Appointment Description
     * @param app_location Appointment Location
     * @param app_type Appointment Type
     * @param start_time_local LocalDateTime created by GUI
     * @param end_time_local LocalDateTime created by GUI
     */
    public Appointment(int contact_id,
                       int cust_id,
                       int user_id,
                       String app_title,
                       String app_desc,
                       String app_location,
                       String app_type,
                       LocalDateTime start_time_local,
                       LocalDateTime end_time_local)
    {
        this.contact_id = contact_id;
        this.cust_id = cust_id;
        this.user_id = user_id;
        this.app_title = app_title;
        this.app_desc = app_desc;
        this.app_location = app_location;
        this.app_type = app_type;

        this.start_time_utc = time_convert.toUTCString(start_time_local);
        this.end_time_utc = time_convert.toUTCString(end_time_local);
        this.start_time_local = start_time_local;
        this.end_time_local = end_time_local;
        this.app_date = start_time_local.toLocalDate();
    }

    // Helper functions
    public int getId(){ return app_id; }
    public int getContact_id(){ return contact_id; }
    public int getUser_id(){ return user_id; }
    public int getCustomer_id(){ return cust_id; }
    public String getTitle(){ return app_title; }
    public String getDescription() { return app_desc; }
    public String getLocation(){ return app_location; }
    public String getType() { return app_type; }
    public String getStartUTC() { return start_time_utc; }
    public String getEndUTC() { return end_time_utc; }
    public LocalDateTime getStartDateTimeLocal() { return start_time_local; }
    public LocalDateTime getEndDateTimeLocal() { return end_time_local; }
    public LocalTime getStartTimeLocal() { return start_time_local.toLocalTime(); }
    public LocalTime getEndTimeLocal() { return end_time_local.toLocalTime(); }
    public LocalDate getDate() { return app_date; }

    // Super helper functions
    /**
     * Get a contact object using the contact_id attribute of the Appointment
     * @return Contact object
     */
    public Contact getContactObject() { return database_operation.getContactById(contact_id); }

    /**
     * Get a contact object using the contact_id attribute of the Appointment
     * @return String, contact_id.Name
     */
    public String getContactName() { return database_operation.getContactById(contact_id).getName(); }
}
