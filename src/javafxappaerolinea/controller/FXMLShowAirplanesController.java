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
public class FXMLShowAirplanesController implements Initializable {

    @FXML
    private TableView<?> tvAirplanes;
    @FXML
    private TableColumn<?, ?> tcRegistration;
    @FXML
    private TableColumn<?, ?> tcModel;
    @FXML
    private TableColumn<?, ?> tcCapacity;
    @FXML
    private TableColumn<?, ?> tcAge;
    @FXML
    private TableColumn<?, ?> tcWeight;
    @FXML
    private TableColumn<?, ?> tcStatus;
    @FXML
    private TableColumn<?, ?> tcAirline;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    
}
