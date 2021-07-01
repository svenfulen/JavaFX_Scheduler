package main_package;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.*;
import objects.Country;
import objects.Division;
import objects.Customer;

public class add_customer_controller {
    @FXML private Button cancel_button;
    @FXML private TextField name_field;
    @FXML private TextField phone_field;
    @FXML private TextField address_field;
    @FXML private TextField zip_field;
    @FXML private TextField customer_id;
    @FXML private ComboBox<Country> country_combo;
    @FXML private ComboBox<Division> region_combo;

    /**
     * Populate the Country selections.
     */
    public void initialize(){
        populateCountries();
        customer_id.setText("ID");
    }

    /**
     * Close the Stage containing the cancel button
     */
    public void cancelButton(){
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        stage.close();
    }

    /**
     * Create a Customer Object and add a new Customer to the database.
     */
    public void addCustomer(){
        String newCxName = name_field.getText();
        String newCxPhone = phone_field.getText();
        String newCxAddress = address_field.getText();
        String newCxZip = zip_field.getText();
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
        if(canSubmit) {
            Customer new_customer = new Customer(newCxName, newCxAddress, newCxZip, newCxPhone, newCxDivisionId);
            database_operation.add_customer(new_customer);
            cancelButton();
        }
        else {
            ui_popups.errorMessage(problem);
        }
    }

    /**
     * Populate the Countries selection menu with Country objects.
     */
    public void populateCountries(){
        region_combo.setDisable(true);
        country_combo.getItems().setAll(objects.Country.getCountries());
        country_combo.setPromptText("Select a country...");
    }

    /**
     * Use the selected Country object to populate Divisions.
     */
    public void populateLocale(){
        region_combo.setDisable(false);
        Country countrySelected = country_combo.getValue();
        region_combo.getItems().setAll(database_operation.getDivisions(countrySelected));
    }



}
