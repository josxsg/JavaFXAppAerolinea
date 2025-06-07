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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLTicketsController implements Initializable {

    @FXML
    private Button btnBuyTicket;
    @FXML
    private Button btnExport;
    @FXML
    private TableView<?> tableFlights;
    @FXML
    private TableColumn<?, ?> columnDepartureDate;
    @FXML
    private TableColumn<?, ?> columnArrivalDate;
    @FXML
    private TextField txtOriginFilter;
    @FXML
    private TextField txtDestinationFilter;
    @FXML
    private DatePicker dpDepartureDateFilter;
    @FXML
    private Button btnApplyFilter;
    @FXML
    private Button btnClearFilter;
    @FXML
    private TableColumn<?, ?> columnId;
    @FXML
    private TableColumn<?, ?> columnAirline;
    @FXML
    private TableColumn<?, ?> columnOriginCity;
    @FXML
    private TableColumn<?, ?> columnDestinationCity;
    @FXML
    private TableColumn<?, ?> columnDepartureHour;
    @FXML
    private TableColumn<?, ?> columnArrivalHour;
    @FXML
    private TableColumn<?, ?> columnTicketCost;
    @FXML
    private TableColumn<?, ?> columnGate;
    @FXML
    private TableColumn<?, ?> columnPassengerCount;
    @FXML
    private TableColumn<?, ?> columnTravelTime;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleBuyTicket(ActionEvent event) {
    }

    @FXML
    private void handleExport(ActionEvent event) {
    }

    @FXML
    private void handleApplyFilter(ActionEvent event) {
    }

    @FXML
    private void handleClearFilter(ActionEvent event) {
    }
    
}
