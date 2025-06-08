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
import javafx.stage.FileChooser; // Importar FileChooser
import java.io.File; // Importar File
import javafxappaerolinea.JavaFXAppAerolinea;
import javafxappaerolinea.model.dao.AirlineDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.ExportUtil; // Importar ExportUtil

// Importa el controlador de la ventana que quieres abrir
import javafxappaerolinea.controller.FXMLShowAirplanesController;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLAirlineController implements Initializable {

    @FXML
    private TableView<Airline> tvAirlines;
    @FXML
    private TableColumn<Airline, Integer> tcId;
    @FXML
    private TableColumn<Airline, String> tcName;
    @FXML
    private TableColumn<Airline, String> tcAddress;
    @FXML
    private TableColumn<Airline, String> tcContact;
    @FXML
    private TableColumn<Airline, String> tcPhone;

    private AirlineDAO airlineDAO;
    private ObservableList<Airline> airlines;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        airlineDAO = new AirlineDAO();
        airlines = FXCollections.observableArrayList();
        configureTable();
        loadTableData();
    }

    private void configureTable() {
        tcId.setCellValueFactory(new PropertyValueFactory<>("identificationNumber"));
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        tcContact.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        tcPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    private void loadTableData() {
        try {
            List<Airline> airlineList = airlineDAO.findAll();
            airlines.setAll(airlineList);
            tvAirlines.setItems(airlines);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al cargar las aerolíneas: " + e.getMessage());
        }
    }

    @FXML
    private void btnAddAirline(ActionEvent event) {
        openAirlineForm(null);
    }

    @FXML
    private void btnEditAirline(ActionEvent event) {
        Airline selectedAirline = tvAirlines.getSelectionModel().getSelectedItem();
        if (selectedAirline == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione una aerolínea para editar.");
            return;
        }
        openAirlineForm(selectedAirline);
    }

    @FXML
    private void btnDeleteAirline(ActionEvent event) {
        Airline selectedAirline = tvAirlines.getSelectionModel().getSelectedItem();
        if (selectedAirline == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione una aerolínea para eliminar.");
            return;
        }

        boolean confirmed = DialogUtil.showConfirmationDialog("Confirmar eliminación",
                "¿Está seguro de que desea eliminar la aerolínea seleccionada?");

        if (confirmed) {
            try {
                airlineDAO.delete(selectedAirline.getIdentificationNumber());
                DialogUtil.showInfoAlert("Éxito", "Aerolínea eliminada exitosamente.");
                loadTableData(); // Refresh the table
            } catch (IOException | javafxappaerolinea.exception.ResourceNotFoundException e) {
                DialogUtil.showErrorAlert("Error", "Error al eliminar la aerolínea: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btnExport(ActionEvent event) {
        try {
            // Obtener los datos de las aerolíneas para exportar
            List<Airline> airlinesToExport = tvAirlines.getItems();

            if (airlinesToExport.isEmpty()) {
                DialogUtil.showWarningAlert(
                    "Sin datos",
                    "No hay aerolíneas para exportar."
                );
                return;
            }

            // Configurar el diálogo de guardar archivo
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Archivo");

            // Configurar los filtros de extensión
            FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
            FileChooser.ExtensionFilter xlsFilter = new FileChooser.ExtensionFilter("Excel (*.xls)", "*.xls");
            FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
            FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"); // Added JSON filter

            fileChooser.getExtensionFilters().addAll(csvFilter, xlsFilter, xlsxFilter, pdfFilter, jsonFilter);

            // Mostrar el diálogo de guardar
            File file = fileChooser.showSaveDialog(tvAirlines.getScene().getWindow());

            if (file != null) {
                String filePath = file.getAbsolutePath();
                String extension = getFileExtension(file.getName()).toLowerCase();

                // Crear título para el documento
                String title = "Reporte de Aerolíneas";
                // Crear nombre para la hoja de Excel
                String sheetName = "Aerolineas";

                // Exportar según el formato seleccionado
                switch (extension) {
                    case "csv":
                        ExportUtil.exportToCSV(airlinesToExport, filePath); // No columnOrder param in ExportUtil
                        break;
                    case "xls":
                        ExportUtil.exportToXLS(airlinesToExport, filePath, sheetName); // No columnOrder param in ExportUtil
                        break;
                    case "xlsx":
                        ExportUtil.exportToXLSX(airlinesToExport, filePath, sheetName); // No columnOrder param in ExportUtil
                        break;
                    case "pdf":
                        ExportUtil.exportToPDF(airlinesToExport, filePath, title); // No columnOrder param in ExportUtil
                        break;
                    case "json": // Added JSON case
                        ExportUtil.exportToJSON(airlinesToExport, filePath);
                        break;
                    default:
                        // Si no tiene extensión o no es reconocida, usar XLSX por defecto
                        if (!filePath.endsWith(".xlsx")) {
                            filePath += ".xlsx";
                        }
                        ExportUtil.exportToXLSX(airlinesToExport, filePath, sheetName);
                        break;
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
        } catch (Exception ex) { // Catch any other unexpected exceptions
            DialogUtil.showErrorAlert(
                "Error",
                "Error inesperado al exportar: " + ex.getMessage()
            );
            ex.printStackTrace(); // Print stack trace for debugging
        }
    }

    @FXML
    private void btnViewAirplanes(ActionEvent event) {
        Airline selectedAirline = tvAirlines.getSelectionModel().getSelectedItem();
        if (selectedAirline == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione una aerolínea para ver sus aviones.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource("view/FXMLShowAirplanes.fxml"));
            Parent root = loader.load();

            FXMLShowAirplanesController controller = loader.getController();
            controller.initData(selectedAirline); // Pasa la aerolínea seleccionada al otro controlador

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Aviones de " + selectedAirline.getName());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al abrir la vista de aviones: " + e.getMessage());
        }
    }

    @FXML
    private void btnViewFlights(ActionEvent event) {
        Airline selectedAirline = tvAirlines.getSelectionModel().getSelectedItem();
        if (selectedAirline == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione una aerolínea para ver sus vuelos.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource("view/FXMLShowFlights.fxml"));
            Parent root = loader.load();

            FXMLShowFlightsController controller = loader.getController();
            controller.initData(selectedAirline); // Pass the selected airline to the flights controller

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Vuelos de " + selectedAirline.getName());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al abrir la vista de vuelos: " + e.getMessage());
        }
    }

    private void openAirlineForm(Airline airline) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource("view/FXMLAirlineForm.fxml"));
            Parent root = loader.load();

            FXMLAirlineFormController controller = loader.getController();
            controller.initData(airline);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(airline == null ? "Agregar Aerolínea" : "Editar Aerolínea");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadTableData(); // Refresh table after form is closed
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al abrir el formulario de aerolínea: " + e.getMessage());
        }
    }


    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
}