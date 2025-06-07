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
public class FXMLAddAirplaneController implements Initializable {

    @FXML
    private TableView<?> tvAvailableAirplanes;
    @FXML
    private TableColumn<?, ?> tcAvailableRegistration;
    @FXML
    private TableColumn<?, ?> tcAvailableModel;
    @FXML
    private TableColumn<?, ?> tcAvailableCapacity;
    @FXML
    private TableColumn<?, ?> tcAvailableAge;
    @FXML
    private TableColumn<?, ?> tcAvailableWeight;
    @FXML
    private TableColumn<?, ?> tcAvailableStatus;
    @FXML
    private TableColumn<?, ?> tcAvailableAirline;
    @FXML
    private TableView<?> tvAddedAirplanes;
    @FXML
    private TableColumn<?, ?> tcAddedRegistration;
    @FXML
    private TableColumn<?, ?> tcAddedModel;
    @FXML
    private TableColumn<?, ?> tcAddedCapacity;
    @FXML
    private TableColumn<?, ?> tcAddedAge;
    @FXML
    private TableColumn<?, ?> tcAddedWeight;
    @FXML
    private TableColumn<?, ?> tcAddedStatus;
    @FXML
    private TableColumn<?, ?> tcAddedAirline;

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
