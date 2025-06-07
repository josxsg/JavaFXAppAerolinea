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
public class FXMLAddPilotController implements Initializable {

    @FXML
    private TableView<?> tvAvailablePilots;
    @FXML
    private TableColumn<?, ?> tcAvailableName;
    @FXML
    private TableColumn<?, ?> tcAvailableLicense;
    @FXML
    private TableView<?> tvAddedPilots;
    @FXML
    private TableColumn<?, ?> tcAddedName;
    @FXML
    private TableColumn<?, ?> tcAddedLicense;
    @FXML
    private TableColumn<?, ?> tcAvailableAge;
    @FXML
    private TableColumn<?, ?> tcAvailableExperience;
    @FXML
    private TableColumn<?, ?> tcAvailableFlightTypes;
    @FXML
    private TableColumn<?, ?> tcAddedAge;
    @FXML
    private TableColumn<?, ?> tcAddedExperience;
    @FXML
    private TableColumn<?, ?> tcAddedFlightTypes;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnAdd(ActionEvent event) {
    }

    @FXML
    private void btnRemove(ActionEvent event) {
    }

    @FXML
    private void btnSaveChanges(ActionEvent event) {
    }

    @FXML
    private void btnCancel(ActionEvent event) {
    }
    
}
