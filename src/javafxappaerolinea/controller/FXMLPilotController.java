/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafxappaerolinea.model.dao.EmployeeDAO;
import javafxappaerolinea.model.pojo.Administrative;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.utility.DialogUtil;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLPilotController implements Initializable {

    @FXML
    private TableView<Pilot> tvPilots;
    @FXML
    private TableColumn tcId;
    @FXML
    private TableColumn tcName;
    @FXML
    private TableColumn tcAddress;
    @FXML
    private TableColumn tcBirthDate;
    @FXML
    private TableColumn tcGender;
    @FXML
    private TableColumn tcSalary;
    @FXML
    private TableColumn tcUsername;
    @FXML
    private TableColumn tcYearsExperience;
    @FXML
    private TableColumn tcFlightHours;
    @FXML
    private TableColumn tcLicenseType;
    @FXML
    private TableColumn tcEmail;
    private ObservableList<Pilot> pilots;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTable();
        loadTableData();
    }    

    @FXML
    private void btnAddPilot(ActionEvent event) {
    }

    @FXML
    private void btnEditPilot(ActionEvent event) {
    }

    @FXML
    private void btnDeletePilot(ActionEvent event) {
    }

    @FXML
    private void btnExport(ActionEvent event) {
    }

    @FXML
    private void btnViewFlights(ActionEvent event) {
    }
    
    private void configureTable() {
        tcId.setCellValueFactory(new PropertyValueFactory("id"));
        tcName.setCellValueFactory(new PropertyValueFactory("name"));
        tcAddress.setCellValueFactory(new PropertyValueFactory("address"));
        tcBirthDate.setCellValueFactory(new PropertyValueFactory("birthDate"));
        tcGender.setCellValueFactory(new PropertyValueFactory("gender"));
        tcSalary.setCellValueFactory(new PropertyValueFactory("salary"));
        tcUsername.setCellValueFactory(new PropertyValueFactory("username"));
        tcYearsExperience.setCellValueFactory(new PropertyValueFactory("yearsExperience"));
        tcEmail.setCellValueFactory(new PropertyValueFactory("email"));
        tcFlightHours.setCellValueFactory(new PropertyValueFactory("flightHours"));
        tcLicenseType.setCellValueFactory(new PropertyValueFactory("licenseType"));
        
    }
    
    private void loadTableData() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            pilots = FXCollections.observableArrayList();
            List<Pilot> pilotList = employeeDAO.findAllPilots();
            pilots.addAll(pilotList);
            tvPilots.setItems(pilots);
        } catch (IOException e) {
            DialogUtil.showErrorAlert(
                "Error al cargar datos", 
                "No se pudieron cargar los administrativos: " + e.getMessage()
            );
        }
    }
}
