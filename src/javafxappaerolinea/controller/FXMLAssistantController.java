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
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.observable.Notification;
import javafxappaerolinea.utility.DialogUtil;
/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLAssistantController implements Initializable, Notification {

    @FXML
    private TableView<Assistant> tvAssistants;
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
    private TableColumn tcEmail;
    @FXML
    private TableColumn tcAssistanceHours;
    @FXML
    private TableColumn tcNumberOfLanguages;
    private ObservableList<Assistant> assistants;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTable();
        loadTableData();
    }    

    @FXML
    private void btnAddAssistant(ActionEvent event) {
        openAssistantForm(false, null);
    }

    @FXML
    private void btnEditAssistant(ActionEvent event) {
        Assistant selectedAssistant = getSelectedAssistant();
        if (selectedAssistant != null) {
            openAssistantForm(true, selectedAssistant);
        }
    }

    @FXML
    private void btnDeleteAssistant(ActionEvent event) {
        Assistant selectedAssistant = getSelectedAssistant();
        
        if (selectedAssistant != null) {
            boolean confirmed = DialogUtil.showConfirmationDialog(
                "Confirmar eliminación",
                "¿Estás seguro de eliminar este asistente de vuelo? Esta acción no se puede deshacer."
            );

            if (confirmed) {
                try {
                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    employeeDAO.delete(selectedAssistant.getId());
                    DialogUtil.showInfoAlert(
                        "Eliminación exitosa", 
                        "El asistente de vuelo ha sido eliminado correctamente."
                    );
                    operationSucceeded();
                } catch (ResourceNotFoundException e) {
                    DialogUtil.showErrorAlert(
                        "Error al eliminar", 
                        "No se encontró el asistente: " + e.getMessage()
                    );
                } catch (IOException e) {
                    DialogUtil.showErrorAlert(
                        "Error", 
                        "No se pudo eliminar el asistente: " + e.getMessage()
                    );
                }
            }
        }
    }

    @FXML
    private void btnExport(ActionEvent event) {
    }

    @FXML
    private void btnViewFlights(ActionEvent event) {
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
        tcEmail.setCellValueFactory(new PropertyValueFactory("email"));
        tcAssistanceHours.setCellValueFactory(new PropertyValueFactory("assistanceHours"));
        tcNumberOfLanguages.setCellValueFactory(new PropertyValueFactory("numberOfLanguages"));
    }
    
    private void loadTableData() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            assistants = FXCollections.observableArrayList();
            List<Assistant> assistantList = employeeDAO.findAllAssistants();
            assistants.addAll(assistantList);
            tvAssistants.setItems(assistants);
        } catch (IOException e) {
            DialogUtil.showErrorAlert(
                "Error al cargar datos", 
                "No se pudieron cargar los asistentes de vuelo: " + e.getMessage()
            );
        }
    }
    
    private void openAssistantForm(boolean isEditing, Assistant assistantToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLAssistantForm.fxml"));
            Parent root = loader.load();
            
            FXMLAssistantFormController controller = loader.getController();
            controller.initializeData(isEditing, assistantToEdit, this);
            
            Stage stage = new Stage();
            stage.setTitle(isEditing ? "Editar Asistente de Vuelo" : "Nuevo Asistente de Vuelo");
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
    
    private Assistant getSelectedAssistant() {
        Assistant selectedAssistant = tvAssistants.getSelectionModel().getSelectedItem();
        
        if (selectedAssistant == null) {
            DialogUtil.showWarningAlert(
                "Sin selección", 
                "Por favor, selecciona un asistente de vuelo de la tabla."
            );
        }
        
        return selectedAssistant;
    }
    
}
