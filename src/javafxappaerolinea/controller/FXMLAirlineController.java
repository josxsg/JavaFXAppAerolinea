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
public class FXMLAirlineController implements Initializable {

    @FXML
    private TableView<?> tvAirlines;
    @FXML
    private TableColumn<?, ?> tcId;
    @FXML
    private TableColumn<?, ?> tcName;
    @FXML
    private TableColumn<?, ?> tcAddress;
    @FXML
    private TableColumn<?, ?> tcContact;
    @FXML
    private TableColumn<?, ?> tcPhone;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnAddAirline(ActionEvent event) {
    }

    @FXML
    private void btnEditAirline(ActionEvent event) {
    }

    @FXML
    private void btnDeleteAirline(ActionEvent event) {
    }

    @FXML
    private void btnExport(ActionEvent event) {
    }

    @FXML
    private void btnViewAirplanes(ActionEvent event) {
    }

    @FXML
    private void btnViewFlights(ActionEvent event) {
    }
    
}
