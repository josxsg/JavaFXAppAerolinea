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
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLAssistantUpcomingFlightsController implements Initializable {

    @FXML
    private TextField tfFilterDestination;
    @FXML
    private TableView<?> tvUpcomingFlights;
    @FXML
    private TableColumn<?, ?> tcId;
    @FXML
    private TableColumn<?, ?> tcOrigin;
    @FXML
    private TableColumn<?, ?> tcDestination;
    @FXML
    private TableColumn<?, ?> tcDepartureDate;
    @FXML
    private TableColumn<?, ?> tcDepartureHour;
    @FXML
    private TableColumn<?, ?> tcArrivalDate;
    @FXML
    private TableColumn<?, ?> tcArrivalHour;
    @FXML
    private TableColumn<?, ?> tcGate;
    @FXML
    private TableColumn<?, ?> tcPassengerCount;
    @FXML
    private TableColumn<?, ?> tcAirline;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnFilter(ActionEvent event) {
    }

    @FXML
    private void btnClearFilter(ActionEvent event) {
    }

    @FXML
    private void btnViewDetails(ActionEvent event) {
    }

    @FXML
    private void btnExport(ActionEvent event) {
    }
    
}
