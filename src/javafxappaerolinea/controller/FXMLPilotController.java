package javafxappaerolinea.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.EmployeeDAO;
import javafxappaerolinea.model.pojo.Administrative;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.observable.Notification;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.ExportUtil;

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
        try {
            // Obtener los pilotos para exportar
            List<Pilot> pilotsToExport = tvPilots.getItems();

            if (pilotsToExport.isEmpty()) {
                DialogUtil.showWarningAlert(
                    "Sin datos", 
                    "No hay pilotos para exportar."
                );
                return;
            }

            // Configurar el diálogo de guardar archivo
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Archivo");

            // Configurar los filtros de extensión (solo CSV, Excel y PDF)
            FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
            FileChooser.ExtensionFilter xlsFilter = new FileChooser.ExtensionFilter("Excel (*.xls)", "*.xls");
            FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");

            fileChooser.getExtensionFilters().addAll(csvFilter, xlsFilter, xlsxFilter, pdfFilter);

            // Mostrar el diálogo de guardar
            File file = fileChooser.showSaveDialog(tvPilots.getScene().getWindow());

            if (file != null) {
                String filePath = file.getAbsolutePath();
                String extension = getFileExtension(file.getName()).toLowerCase();

                // Crear título para el documento
                String title = "Pilotos - Aerolínea";
                // Crear nombre para la hoja de Excel
                String sheetName = "Pilotos";

                // Crear una lista con los nombres de las columnas en el orden deseado
                List<String> columnOrder = new ArrayList<>();
                // Primero los campos de Employee
                columnOrder.add("id");
                columnOrder.add("name");
                columnOrder.add("address");
                columnOrder.add("birthDate");
                columnOrder.add("gender");
                columnOrder.add("salary");
                columnOrder.add("username");
                // Luego los campos específicos de Pilot
                columnOrder.add("yearsExperience");
                columnOrder.add("flightHours");
                columnOrder.add("licenseType");
                columnOrder.add("email");

                // Exportar según el formato seleccionado
                switch (extension) {
                    case "csv":
                        ExportUtil.exportToCSV(pilotsToExport, filePath, columnOrder);
                        break;
                    case "xls":
                        ExportUtil.exportToXLS(pilotsToExport, filePath, sheetName, columnOrder);
                        break;
                    case "xlsx":
                        ExportUtil.exportToXLSX(pilotsToExport, filePath, sheetName, columnOrder);
                        break;
                    case "pdf":
                        ExportUtil.exportToPDF(pilotsToExport, filePath, title, columnOrder);
                        break;
                    default:
                        DialogUtil.showErrorAlert(
                            "Formato no soportado", 
                            "El formato de archivo seleccionado no es soportado."
                        );
                        return;
                }

                DialogUtil.showInfoAlert(
                    "Exportación Exitosa", 
                    "Los datos se han exportado correctamente a: " + file.getName()
                );
            }
        } catch (IOException ex) {
            DialogUtil.showErrorAlert(
                "Error", 
                "Error al exportar los datos: " + ex.getMessage()
            );
        } catch (Exception ex) {
            DialogUtil.showErrorAlert(
                "Error", 
                "Error inesperado: " + ex.getMessage()
            );
        }
    }

@FXML
private void btnViewFlights(ActionEvent event) {
    // Get the currently selected pilot from the table
    Pilot selectedPilot = getSelectedPilot(); 

    if (selectedPilot != null) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLPilotUpcomingFlights.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the selected pilot data
            FXMLPilotUpcomingFlightsController controller = loader.getController();
            controller.initData(selectedPilot); // Pass the selected pilot to the Upcoming Flights controller

            Stage stage = new Stage();
            stage.setTitle("Vuelos Próximos del Piloto: " + selectedPilot.getName()); // Set title dynamically
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            DialogUtil.showErrorAlert(
                "Error", 
                "No se pudo abrir la ventana de vuelos próximos: " + e.getMessage()
            );
        }
    }
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
    
    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // No hay extensión
        }
        return fileName.substring(lastIndexOf + 1);
    }
}