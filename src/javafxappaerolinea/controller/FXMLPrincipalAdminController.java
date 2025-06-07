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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLPrincipalAdminController implements Initializable {

    @FXML
    private Label lblWindowTitle;
    @FXML
    private Label lblUsername;
    @FXML
    private AnchorPane apCentral;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    


    @FXML
    private void logOut(ActionEvent event) {
    }

    @FXML
    private void openAirlinesWindow(ActionEvent event) {
    }

    @FXML
    private void openAirplanesWindow(ActionEvent event) {
    }

    @FXML
    private void openFlightsWindow(ActionEvent event) {
    }

    @FXML
    private void openRegisterClientWindow(ActionEvent event) {
    }

    @FXML
    private void openBuyTicketWindow(ActionEvent event) {
    }

    @FXML
    private void openAdminStaffWindow(ActionEvent event) {
    }

    @FXML
    private void openPilotsWindow(ActionEvent event) {
    }

    @FXML
    private void openFlightAttendantsWindow(ActionEvent event) {
    }
    
}
