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
public class FXMLShowFlightsController implements Initializable {

    @FXML
    private TableView<?> tvFlights;
    @FXML
    private TableColumn<?, ?> tcId;
    @FXML
    private TableColumn<?, ?> tcOriginCity;
    @FXML
    private TableColumn<?, ?> tcDestinationCity;
    @FXML
    private TableColumn<?, ?> tcDepartureDate;
    @FXML
    private TableColumn<?, ?> tcDepartureHour;
    @FXML
    private TableColumn<?, ?> tcArrivalDate;
    @FXML
    private TableColumn<?, ?> tcArrivalHour;
    @FXML
    private TableColumn<?, ?> tcTravelTime;
    @FXML
    private TableColumn<?, ?> tcPassengerCount;
    @FXML
    private TableColumn<?, ?> tcGate;
    @FXML
    private TableColumn<?, ?> tcAirplane;
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
    private void btnClose(ActionEvent event) {
    }
    
}
