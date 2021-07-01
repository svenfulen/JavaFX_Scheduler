package main_package;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import objects.Country;
import objects.Customer;
import objects.Division;
import utils.database_operation;

public class modify_customer_controller {
    @FXML private Button cancel_button;
    @FXML private TextField name_field;
    @FXML private TextField phone_field;
    @FXML private TextField address_field;
    @FXML private TextField zip_field;
    @FXML private ComboBox<Country> country_combo;
    @FXML private ComboBox<Division> region_combo;
    @FXML private TextField customer_id;

    static Customer selected_customer;

    /**
     * Populate the Country selections
     * Populate form data from the selected Customer
     */
    public void initialize(){

        populateCountries();
        // Get customer information and populate form
        name_field.setText(selected_customer.getName());
        phone_field.setText(selected_customer.getPhone());
        address_field.setText(selected_customer.getAddress());
        zip_field.setText(selected_customer.getZip());
        customer_id.setText(String.valueOf(selected_customer.getId()));

        // Select the country based on the customer
        int selected_country_id = database_operation.getCountryIdFromDivisionId(selected_customer.getDivisionId());
        ObservableList<Country> populated_countries = country_combo.getItems();
        // Lambda - Select the Country associated with the Customer.
        populated_countries.forEach(country -> {
            if (country.getId() == selected_country_id) {
                country_combo.getSelectionModel().select(country);
            }
        });

        // Select the division based on the customer
        populateLocale();
        ObservableList<Division> populated_divisions = region_combo.getItems();
        // Lambda - Select the Division associated with the Customer.
        populated_divisions.forEach(division -> {
            if (division.getId() == selected_customer.getDivisionId()) {
                region_combo.getSelectionModel().select(division);
            }
        });

    }

    /**
     * Close the Stage containing the cancel button
     */
    public void cancelButton(){
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        stage.close();
    }

    /**
     * Create a Customer object and modify an existing customer in the database.
     */
    public void modifyCustomer(){
        String newCxName = name_field.getText();
        String newCxPhone = phone_field.getText();
        String newCxAddress = address_field.getText();
        String newCxZip = zip_field.getText();
        int newCxId = selected_customer.getId(); //Set this so it can edit the customer.

        int newCxDivisionId = 0;
        if(region_combo.getSelectionModel().getSelectedItem() != null) {
            newCxDivisionId = region_combo.getSelectionModel().getSelectedItem().getId();
        }

        // Check for errors in the form
        boolean canSubmit = true;
        String problem = "";
        if(region_combo.getSelectionModel().getSelectedItem() == null) {
            canSubmit = false;
            problem = "Please select a Country and Region";
        }
        if(zip_field.getText() == null || newCxZip.isBlank()){
            canSubmit = false;
            problem = "Please enter a Zip Code";
        }
        if(address_field.getText() == null || newCxAddress.isBlank()){
            canSubmit = false;
            problem = "Please enter an Address";
        }
        if(phone_field.getText() == null || newCxPhone.isBlank()){
            canSubmit = false;
            problem = "Please enter a Phone Number";
        }
        if(name_field.getText() == null || newCxName.isBlank()){
            canSubmit = false;
            problem = "Please enter a Customer Name";
        }

        // If there are no errors, submit the form. else, show an error message
        if (canSubmit) {
            Customer new_customer = new Customer(newCxId, newCxName, newCxAddress, newCxZip, newCxPhone, newCxDivisionId);
            database_operation.edit_customer(new_customer);
            cancelButton();
        }
        else {
            utils.ui_popups.errorMessage(problem);
        }
    }

    /**
     * Populate the Countries selection menu with Country objects.
     */
    public void populateCountries(){
        region_combo.setDisable(true);
        country_combo.getItems().setAll(Country.getCountries());
        country_combo.setPromptText("Select a country...");
    }

    /**
     * Use the selected Country object to populate Divisions
     */
    public void populateLocale(){
        region_combo.setDisable(false);
        Country countrySelected = country_combo.getValue();
        region_combo.getItems().setAll(database_operation.getDivisions(countrySelected));
    }



}
