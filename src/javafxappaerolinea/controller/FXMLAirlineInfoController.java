/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafxappaerolinea.model.dao.AirplaneDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Airplane;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Employee;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.service.SessionManager;

/**
 * FXML Controller class for displaying airline information
 *
 * @author zenbook i5
 */
public class FXMLAirlineInfoController implements Initializable {

    @FXML
    private Label lbIdentificationNumber;
    
    @FXML
    private Label lbName;
    
    @FXML
    private Label lbAddress;
    
    @FXML
    private Label lbContactPerson;
    
    @FXML
    private Label lbPhoneNumber;
    
    @FXML
    private TableView<Airplane> tvAirplanes;
    
    @FXML
    private TableColumn<Airplane, String> tcRegistration;
    
    @FXML
    private TableColumn<Airplane, String> tcModel;
    
    @FXML
    private TableColumn<Airplane, Integer> tcCapacity;
    
    @FXML
    private TableColumn<Airplane, Integer> tcAge;
    
    @FXML
    private TableColumn<Airplane, Double> tcWeight;
    
    @FXML
    private TableColumn<Airplane, Boolean> tcStatus;
    
    private Airline airline;
    private ObservableList<Airplane> airplanes;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        loadAirlineInfo();
    }
    
    /**
     * Configures the table columns to display airplane properties
     */
    private void configureTableColumns() {
        tcRegistration.setCellValueFactory(new PropertyValueFactory<>("registration"));
        tcModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        tcCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tcAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        tcWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        tcStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    
    /**
     * Loads the airline information based on the current logged-in employee
     */
    private void loadAirlineInfo() {
        try {
            // Get the current employee
            Employee currentEmployee = SessionManager.getInstance().getCurrentUser();
            
            if (currentEmployee != null) {
                // Get the airline associated with the employee based on their type
                if (currentEmployee instanceof Pilot) {
                    airline = ((Pilot) currentEmployee).getAirline();
                } else if (currentEmployee instanceof Assistant) {
                    airline = ((Assistant) currentEmployee).getAirline();
                } else {
                    // For other employee types, show a message
                    showAlert("Información no disponible", 
                             "Este tipo de empleado no tiene una aerolínea asociada directamente.", 
                             Alert.AlertType.INFORMATION);
                    return;
                }
                
                if (airline != null) {
                    // Display airline information
                    lbIdentificationNumber.setText(String.valueOf(airline.getIdentificationNumber()));
                    lbName.setText(airline.getName());
                    lbAddress.setText(airline.getAddress());
                    lbContactPerson.setText(airline.getContactPerson());
                    lbPhoneNumber.setText(airline.getPhoneNumber());
                    
                    // Load airplanes for the airline
                    loadAirplanes();
                } else {
                    showAlert("Información no disponible", 
                             "No se encontró información de la aerolínea asociada.", 
                             Alert.AlertType.INFORMATION);
                }
            }
        } catch (Exception ex) {
            showAlert("Error", 
                     "No se pudo cargar la información de la aerolínea: " + ex.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Loads the airplanes associated with the current airline
     */
    private void loadAirplanes() {
        try {
            AirplaneDAO airplaneDAO = new AirplaneDAO();
            // Use the findByAirline method which should filter airplanes by airline ID
            List<Airplane> airplaneList = airplaneDAO.findByAirline(airline.getIdentificationNumber());
            
            if (airplaneList != null && !airplaneList.isEmpty()) {
                airplanes = FXCollections.observableArrayList(airplaneList);
                tvAirplanes.setItems(airplanes);
            } else {
                // Clear the table if no airplanes are found
                tvAirplanes.setItems(FXCollections.observableArrayList());
                showAlert("Información", 
                         "No hay aviones registrados para esta aerolínea.", 
                         Alert.AlertType.INFORMATION);
            }
        } catch (IOException ex) {
            showAlert("Error", 
                     "Error al cargar los aviones: " + ex.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Shows an alert dialog with the specified title, message, and type
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}