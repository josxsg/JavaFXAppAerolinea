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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.EmployeeDAO;
import javafxappaerolinea.model.pojo.Administrative;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.observable.Notification;
import javafxappaerolinea.utility.DialogUtil;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLPilotController implements Initializable, Notification {

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
        openPilotForm(false, null);
    }

    @FXML
    private void btnEditPilot(ActionEvent event) {
        Pilot selectedPilot = getSelectedPilot();
        if (selectedPilot != null) {
            openPilotForm(true, selectedPilot);
        }
    }

    @FXML
    private void btnDeletePilot(ActionEvent event) {
        Pilot selectedPilot = getSelectedPilot();
        
        if (selectedPilot != null) {
            boolean confirmed = DialogUtil.showConfirmationDialog(
                "Confirmar eliminación",
                "¿Estás seguro de eliminar este piloto? Esta acción no se puede deshacer."
            );

            if (confirmed) {
                try {
                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    employeeDAO.delete(selectedPilot.getId());
                    DialogUtil.showInfoAlert(
                        "Eliminación exitosa", 
                        "El piloto ha sido eliminado correctamente."
                    );
                    operationSucceeded();
                } catch (ResourceNotFoundException e) {
                    DialogUtil.showErrorAlert(
                        "Error al eliminar", 
                        "No se encontró el piloto: " + e.getMessage()
                    );
                } catch (IOException e) {
                    DialogUtil.showErrorAlert(
                        "Error", 
                        "No se pudo eliminar el piloto: " + e.getMessage()
                    );
                }
            }
        }
    }

    @FXML
    private void btnExport(ActionEvent event) {
        //TODO
    }

    @FXML
    private void btnViewFlights(ActionEvent event) {
        //TODO
    }
    
    @Override
    public void operationSucceeded() {
        loadTableData();
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
                "No se pudieron cargar los pilotos: " + e.getMessage()
            );
        }
    }
    
    private void openPilotForm(boolean isEditing, Pilot pilotToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLPilotForm.fxml"));
            Parent root = loader.load();
            
            FXMLPilotFormController controller = loader.getController();
            controller.initializeData(isEditing, pilotToEdit, this);
            
            Stage stage = new Stage();
            stage.setTitle(isEditing ? "Editar Piloto" : "Nuevo Piloto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            DialogUtil.showErrorAlert(
                "Error", 
                "No se pudo abrir el formulario: " + e.getMessage()
            );
        }
    }
    
    private Pilot getSelectedPilot() {
        Pilot selectedPilot = tvPilots.getSelectionModel().getSelectedItem();
        
        if (selectedPilot == null) {
            DialogUtil.showWarningAlert(
                "Sin selección", 
                "Por favor, selecciona un piloto de la tabla."
            );
        }
        
        return selectedPilot;
    }
}
