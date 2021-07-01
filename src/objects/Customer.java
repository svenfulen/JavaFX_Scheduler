package objects;

import utils.database_operation;

import java.io.Serializable;

public class Customer implements Serializable{
    private int customer_id;
    private final int division_id;
    private final String customer_name;
    private final String customer_address;
    private final String customer_zip;
    private final String customer_phone;

    /**
     * Customer Constructor used for adding a customer to the database
     * @param customer_name The Name of the customer
     * @param customer_address The Address of the customer
     * @param customer_zip The Postal Code of the customer
     * @param customer_phone The Phone Number of the customer
     * @param division_id The division_id of the customer
     */
    public Customer(String customer_name, String customer_address, String customer_zip, String customer_phone, int division_id)
    {
        this.customer_name = customer_name;
        this.customer_address = customer_address;
        this.customer_zip = customer_zip;
        this.customer_phone = customer_phone;
        this.division_id = division_id;
    }

    /**
     * Customer Constructor used for retrieving a customer from the database
     * @param customer_id The Customer_ID
     * @param customer_name The Name of the customer
     * @param customer_address The Address of the customer
     * @param customer_zip The Postal Code of the customer
     * @param customer_phone The Phone Number of the customer
     * @param division_id The Division_ID of the customer
     */
    public Customer(int customer_id, String customer_name, String customer_address, String customer_zip, String customer_phone, int division_id)
    {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_address = customer_address;
        this.customer_zip = customer_zip;
        this.customer_phone = customer_phone;
        this.division_id = division_id;
    }

    // helper functions
    public int getId(){ return this.customer_id; }
    public int getDivisionId(){ return this.division_id; }
    public String getName() { return this.customer_name; }
    public String getAddress() { return this.customer_address; }
    public String getZip() { return this.customer_zip; }
    public String getPhone() { return this.customer_phone; }

    // Super helper functions

    /**
     * Get the division name using the ID
     * @return The division name
     */
    public String getDivisionName(){
        return database_operation.getDivisionById(this.division_id);
    }

    /**
     * Show the Customer's name instead of the memory location and object
     * @return Customer.customer_name
     */
    @Override
    public String toString(){
        return(customer_name);
    }

}
