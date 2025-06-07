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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLFlightFormController implements Initializable {

    @FXML
    private TextField tfOriginCity;
    @FXML
    private TextField tfDestinationCity;
    @FXML
    private DatePicker dpDepartureDate;
    @FXML
    private TextField tfDepartureHour;
    @FXML
    private DatePicker dpArrivalDate;
    @FXML
    private TextField tfArrivalHour;
    @FXML
    private TextField tfTicketCost;
    @FXML
    private TextField tfPassengerCount;
    @FXML
    private TextField tfGate;
    @FXML
    private ComboBox<?> cbAirline;
    @FXML
    private ComboBox<?> cbAirplane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnAddPilots(ActionEvent event) {
    }

    @FXML
    private void btnAddAssistants(ActionEvent event) {
    }

    @FXML
    private void btnSave(ActionEvent event) {
    }

    @FXML
    private void btnCancel(ActionEvent event) {
    }
    
}
