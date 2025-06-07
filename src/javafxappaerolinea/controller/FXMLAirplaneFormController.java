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
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLAirplaneFormController implements Initializable {

    @FXML
    private TextField tfRegistration;
    @FXML
    private TextField tfModel;
    @FXML
    private TextField tfCapacity;
    @FXML
    private TextField tfAge;
    @FXML
    private TextField tfWeight;
    @FXML
    private ComboBox<?> cbStatus;
    @FXML
    private ComboBox<?> cbAirline;

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
