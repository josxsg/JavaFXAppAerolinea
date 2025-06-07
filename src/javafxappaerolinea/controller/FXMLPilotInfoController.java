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

/**
 * FXML Controller class
 *
 * @author zenbook i5
 */
public class FXMLPilotInfoController implements Initializable {

    @FXML
    private Label lbId;
    @FXML
    private Label lbName;
    @FXML
    private Label lbAddress;
    @FXML
    private Label lbBirthDate;
    @FXML
    private Label lbGender;
    @FXML
    private Label lbSalary;
    @FXML
    private Label lbYearsExperience;
    @FXML
    private Label lbEmail;
    @FXML
    private Label lbFlightHours;
    @FXML
    private Label lbLicenseType;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnEditProfile(ActionEvent event) {
    }
    
}
