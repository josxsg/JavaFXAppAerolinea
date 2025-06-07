/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxappaerolinea.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLPilotController implements Initializable {

    @FXML
    private TableView<?> tvPilots;
    @FXML
    private TableColumn<?, ?> tcId;
    @FXML
    private TableColumn<?, ?> tcName;
    @FXML
    private TableColumn<?, ?> tcAddress;
    @FXML
    private TableColumn<?, ?> tcBirthDate;
    @FXML
    private TableColumn<?, ?> tcGender;
    @FXML
    private TableColumn<?, ?> tcSalary;
    @FXML
    private TableColumn<?, ?> tcUsername;
    @FXML
    private TableColumn<?, ?> tcYearsExperience;
    @FXML
    private TableColumn<?, ?> tcFlightHours;
    @FXML
    private TableColumn<?, ?> tcLicenseType;
    @FXML
    private TableColumn<?, ?> tcEmail;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnAddPilot(ActionEvent event) {
    }

    @FXML
    private void btnEditPilot(ActionEvent event) {
    }

    @FXML
    private void btnDeletePilot(ActionEvent event) {
    }

    @FXML
    private void btnExport(ActionEvent event) {
    }

    @FXML
    private void btnViewFlights(ActionEvent event) {
    }
    
}
