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
import javafxappaerolinea.observable.Notification;
import javafxappaerolinea.model.dao.EmployeeDAO;
import javafxappaerolinea.model.pojo.Administrative;
import javafxappaerolinea.utility.DialogUtil;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLAdministrativeController implements Initializable, Notification {

    @FXML
    private TableView<Administrative> tvAdministratives;
    @FXML
    private TableColumn tcId;
    @FXML
    private TableColumn tcName;
    @FXML
    private TableColumn tcAddress;
    @FXML
    private TableColumn tcDepartment;
    @FXML
    private TableColumn tcWorkHours;
    @FXML
    private TableColumn tcBirthDate;
    @FXML
    private TableColumn tcGender;
    @FXML
    private TableColumn tcSalary;
    @FXML
    private TableColumn tcUsername;

    private ObservableList<Administrative> administratives;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTable();
        loadTableData();
    }    

    @FXML
    private void btnAddAdministrative(ActionEvent event) {
        openAdministrativeForm(false, null);
    }

    @FXML
    private void btnEditAdministrative(ActionEvent event) {
        Administrative selectedAdmin = getSelectedAdministrative();
        if (selectedAdmin != null) {
            openAdministrativeForm(true, selectedAdmin);
        }
    }

    @FXML
    private void btnDeleteAdministrative(ActionEvent event) {
        Administrative selectedAdmin = getSelectedAdministrative();
        
        if (selectedAdmin != null) {
            boolean confirmed = DialogUtil.showConfirmationDialog(
                "Confirmar eliminación",
                "¿Estás seguro de eliminar este administrativo? Esta acción no se puede deshacer."
            );

            if (confirmed) {
                try {
                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    employeeDAO.delete(selectedAdmin.getId());
                    DialogUtil.showInfoAlert(
                        "Eliminación exitosa", 
                        "El administrativo ha sido eliminado correctamente."
                    );
                    operationSucceeded();
                } catch (ResourceNotFoundException e) {
                    DialogUtil.showErrorAlert(
                        "Error al eliminar", 
                        "No se encontró el administrativo: " + e.getMessage()
                    );
                } catch (IOException e) {
                    DialogUtil.showErrorAlert(
                        "Error", 
                        "No se pudo eliminar el administrativo: " + e.getMessage()
                    );
                }
            }
        }
    }

    @FXML
    private void btnExport(ActionEvent event) {
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
        tcDepartment.setCellValueFactory(new PropertyValueFactory("department"));
        tcWorkHours.setCellValueFactory(new PropertyValueFactory("workHours"));
        tcBirthDate.setCellValueFactory(new PropertyValueFactory("birthDate"));
        tcGender.setCellValueFactory(new PropertyValueFactory("gender"));
        tcSalary.setCellValueFactory(new PropertyValueFactory("salary"));
        tcUsername.setCellValueFactory(new PropertyValueFactory("username"));
    }
    
    private void loadTableData() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            administratives = FXCollections.observableArrayList();
            List<Administrative> adminList = employeeDAO.findAllAdministratives();
            administratives.addAll(adminList);
            tvAdministratives.setItems(administratives);
        } catch (IOException e) {
            DialogUtil.showErrorAlert(
                "Error al cargar datos", 
                "No se pudieron cargar los administrativos: " + e.getMessage()
            );
        }
    }
    
    private void openAdministrativeForm(boolean isEditing, Administrative adminToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLAdminForm.fxml"));
            Parent root = loader.load();
            
            FXMLAdminFormController controller = loader.getController();
            controller.initializeData(isEditing, adminToEdit, this);
            
            Stage stage = new Stage();
            stage.setTitle(isEditing ? "Editar Administrativo" : "Nuevo Administrativo");
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
    
    private Administrative getSelectedAdministrative() {
        Administrative selectedAdmin = tvAdministratives.getSelectionModel().getSelectedItem();
        
        if (selectedAdmin == null) {
            DialogUtil.showWarningAlert(
                "Sin selección", 
                "Por favor, selecciona un administrativo de la tabla."
            );
        }
        
        return selectedAdmin;
    }
    
    
}
