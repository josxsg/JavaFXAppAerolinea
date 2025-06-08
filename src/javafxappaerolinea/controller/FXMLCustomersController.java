package javafxappaerolinea.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.CustomerDAO;
import javafxappaerolinea.model.pojo.Customer;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.ExportUtil;

/**
 * Controlador para la vista de clientes
 * @author zenbook i5
 */
public class FXMLCustomersController implements Initializable {

    @FXML
    private Button btnAddCustomer;
    @FXML
    private Button btnExport;
    @FXML
    private TableView<Customer> tableCustomers;
    @FXML
    private TableColumn<Customer, String> columnName;
    @FXML
    private TableColumn<Customer, String> columnEmail;
    @FXML
    private TableColumn<Customer, LocalDate> columnBirthDate;
    @FXML
    private TableColumn<Customer, String> columnNationality;

    private ObservableList<Customer> customers;
    private CustomerDAO customerDAO;

    /**
     * Inicializa el controlador
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerDAO = new CustomerDAO();
        configureTableColumns();
        loadCustomers();
    }    

    /**
     * Configura las columnas de la tabla
     */
    private void configureTableColumns() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        columnNationality.setCellValueFactory(new PropertyValueFactory<>("nationality"));
    }
    
    /**
     * Carga los clientes desde la base de datos
     */
    private void loadCustomers() {
        customers = FXCollections.observableArrayList();
        try {
            List<Customer> customersList = customerDAO.findAll();
            customers.addAll(customersList);
            tableCustomers.setItems(customers);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error al cargar clientes", 
                    "No se pudieron cargar los clientes: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de agregar un nuevo cliente
     */
    @FXML
    private void handleAddCustomer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLAddCustomer.fxml"));
            Parent root = loader.load();
            
            FXMLAddCustomerController controller = loader.getController();
            controller.setCustomersController(this);
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Agregar Cliente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", 
                    "No se pudo abrir la ventana para agregar cliente: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de exportar la tabla de clientes
     */
    @FXML
    private void handleExport(ActionEvent event) {
        if (customers == null || customers.isEmpty()) {
            DialogUtil.showWarningAlert("Sin datos", 
                    "No hay datos para exportar.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Clientes");
        
        // Configurar filtros para diferentes formatos
        FileChooser.ExtensionFilter xlsxFilter = 
                new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx");
        FileChooser.ExtensionFilter xlsFilter = 
                new FileChooser.ExtensionFilter("Excel 97-2003 (*.xls)", "*.xls");
        FileChooser.ExtensionFilter csvFilter = 
                new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
        FileChooser.ExtensionFilter pdfFilter = 
                new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter jsonFilter = 
                new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
        
        fileChooser.getExtensionFilters().addAll(xlsxFilter, xlsFilter, csvFilter, pdfFilter, jsonFilter);
        fileChooser.setSelectedExtensionFilter(xlsxFilter); // Por defecto XLSX
        
        File file = fileChooser.showSaveDialog(btnExport.getScene().getWindow());
        
        if (file != null) {
            try {
                String filePath = file.getAbsolutePath();
                String extension = getFileExtension(filePath).toLowerCase();
                
                switch (extension) {
                    case "xlsx":
                        ExportUtil.exportToXLSX(new ArrayList<>(customers), filePath, "Clientes");
                        break;
                    case "xls":
                        ExportUtil.exportToXLS(new ArrayList<>(customers), filePath, "Clientes");
                        break;
                    case "csv":
                        ExportUtil.exportToCSV(new ArrayList<>(customers), filePath);
                        break;
                    case "pdf":
                        ExportUtil.exportToPDF(new ArrayList<>(customers), filePath, "Listado de Clientes");
                        break;
                    case "json":
                        ExportUtil.exportToJSON(new ArrayList<>(customers), filePath);
                        break;
                    default:
                        // Si no tiene extensión o no es reconocida, usar XLSX por defecto
                        if (!filePath.endsWith(".xlsx")) {
                            filePath += ".xlsx";
                        }
                        ExportUtil.exportToXLSX(new ArrayList<>(customers), filePath, "Clientes");
                        break;
                }
                
                DialogUtil.showInfoAlert("Exportación exitosa", 
                        "Los datos se han exportado correctamente a " + filePath);
            } catch (Exception e) {
                DialogUtil.showErrorAlert("Error al exportar", 
                        "No se pudieron exportar los datos: " + e.getMessage());
            }
        }
    }
    
    /**
     * Obtiene la extensión de un archivo
     */
    private String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return filePath.substring(lastDotIndex + 1);
        }
        return "";
    }
    
    /**
     * Actualiza la tabla de clientes
     */
    public void refreshCustomers() {
        loadCustomers();
    }
}