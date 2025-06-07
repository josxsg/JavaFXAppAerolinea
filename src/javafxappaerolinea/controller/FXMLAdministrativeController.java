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
public class FXMLAdministrativeController implements Initializable {

    @FXML
    private TableView<?> tvAdministratives;
    @FXML
    private TableColumn<?, ?> tcId;
    @FXML
    private TableColumn<?, ?> tcName;
    @FXML
    private TableColumn<?, ?> tcAddress;
    @FXML
    private TableColumn<?, ?> tcDepartment;
    @FXML
    private TableColumn<?, ?> tcWorkHours;
    @FXML
    private TableColumn<?, ?> tcBirthDate;
    @FXML
    private TableColumn<?, ?> tcGender;
    @FXML
    private TableColumn<?, ?> tcSalary;
    @FXML
    private TableColumn<?, ?> tcUsername;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnAddAdministrative(ActionEvent event) {
    }

    @FXML
    private void btnEditAdministrative(ActionEvent event) {
    }

    @FXML
    private void btnDeleteAdministrative(ActionEvent event) {
    }

    @FXML
    private void btnExport(ActionEvent event) {
    }
    
}
