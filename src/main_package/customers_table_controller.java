package main_package;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import objects.Customer;
import utils.database_operation;

public class customers_table_controller {
    @FXML private TableView<Customer> customers_table;
    @FXML private TableColumn<Customer, Integer> id_column;
    @FXML private TableColumn<Customer, String> name_column;
    @FXML private TableColumn<Customer, String> phone_column;
    @FXML private TableColumn<Customer, String> address_column;
    @FXML private TableColumn<Customer, String> zip_column;
    @FXML private TableColumn<Customer, String> division_column;

    /**
     * Populate the Customers table when the window opens.
     */
    public void initialize(){
        populateTable();
    }

    /**
     * Open the Add Customer form.
     * When the Add Customer form is closed, refresh the Customers table.
     *
     * Lambda: When the form is closed (or a new customer is added), refresh the customer table
     *
     * @throws Exception JavaFX exception
     */
    public void open_addCustomerForm() throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../gui/add_customer.fxml"));
        Stage primaryStage = new Stage();
        primaryStage.setTitle("New Customer");
        primaryStage.setScene(new Scene(root, 226, 262));
        primaryStage.setResizable(false);
        primaryStage.show();
        // Lambda: When the form is closed (or a new customer is added), refresh the customer table
        primaryStage.setOnHiding( event -> populateTable());
    }

    /**
     * Open the Modify Customer form.
     * When the Modify Customer form is closed, refresh the Customers table.
     *
     * Lambda: When the form is closed (or a customer is modified), refresh the customer table
     *
     * @throws Exception JavaFX exception
     */
    public void open_modifyCustomerForm() throws Exception{
        Customer selectedCustomer = customers_table.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            modify_customer_controller.selected_customer = selectedCustomer;
            Parent root = FXMLLoader.load(getClass().getResource("../gui/modify_customer.fxml"));
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Edit Customer");
            primaryStage.setScene(new Scene(root, 226, 262));
            primaryStage.setResizable(false);
            primaryStage.show();
            // Lambda: When the form is closed (or a customer is modified), refresh the customer table
            primaryStage.setOnHiding( event -> populateTable());
        }
        else {
            utils.ui_popups.infoMessage("Please select a customer.");
        }
    }

    /**
     * Retrieve all customers from the database and populate the Customers table.
     */
    public void populateTable(){
        ObservableList<Customer> all_customers = database_operation.getAllCustomers();
        customers_table.setPlaceholder(new Label("No customers found"));
        id_column.setCellValueFactory(new PropertyValueFactory<>("Id"));
        name_column.setCellValueFactory(new PropertyValueFactory<>("Name"));
        phone_column.setCellValueFactory(new PropertyValueFactory<>("Phone"));
        address_column.setCellValueFactory(new PropertyValueFactory<>("Address"));
        zip_column.setCellValueFactory(new PropertyValueFactory<>("Zip"));
        division_column.setCellValueFactory(new PropertyValueFactory<>("DivisionName"));
        customers_table.getItems().setAll(all_customers);
    }

    /**
     * 1. Delete all appointments associated with the selected customer.
     * 2. Delete the selected customer from the database.
     */
    public void remove_customer(){
        Customer selectedCustomer = customers_table.getSelectionModel().getSelectedItem();
        if(selectedCustomer != null) {
            database_operation.delete_customer(selectedCustomer);
            populateTable();
        }
        else {
            utils.ui_popups.errorMessage("Please select a Customer");
        }

    }
}
