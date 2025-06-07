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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLPilotFormController implements Initializable {

    @FXML
    private TextField tfName;
    @FXML
    private TextField tfAddress;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private ComboBox<?> cbGender;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfSalary;
    @FXML
    private TextField tfYearsExperience;
    @FXML
    private TextField tfFlightHours;
    @FXML
    private TextField tfLicenseType;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnSave(ActionEvent event) {
    }

    @FXML
    private void btnCancel(ActionEvent event) {
    }
    
}
