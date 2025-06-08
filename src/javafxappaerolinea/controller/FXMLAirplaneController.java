package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.FileChooser; // Importar FileChooser
import java.io.File; // Importar File
import javafx.stage.Stage;
import javafxappaerolinea.JavaFXAppAerolinea;
import javafxappaerolinea.model.dao.AirplaneDAO;
import javafxappaerolinea.model.pojo.Airplane;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.ExportUtil; // Importar ExportUtil

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLAirplaneController implements Initializable {

    @FXML
    private TableView<Airplane> tvAirplanes;
    @FXML
    private TableColumn<Airplane, String> tcRegistration;
    @FXML
    private TableColumn<Airplane, String> tcModel;
    @FXML
    private TableColumn<Airplane, Integer> tcCapacity;
    @FXML
    private TableColumn<Airplane, Integer> tcAge;
    @FXML
    private TableColumn<Airplane, Double> tcWeight;
    @FXML
    private TableColumn<Airplane, String> tcStatus;
    @FXML
    private TableColumn<Airplane, String> tcAirline;

    private AirplaneDAO airplaneDAO;
    private ObservableList<Airplane> airplanes;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        airplaneDAO = new AirplaneDAO();
        airplanes = FXCollections.observableArrayList();
        configureTable();
        loadTableData();
    }    

    private void configureTable() {
        tcRegistration.setCellValueFactory(new PropertyValueFactory<>("registration"));
        tcModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        tcCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tcAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        tcWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        tcStatus.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isStatus() ? "Activo" : "Inactivo"));
        tcAirline.setCellValueFactory(cellData -> {
            if (cellData.getValue().getAirline() != null) {
                return new SimpleStringProperty(cellData.getValue().getAirline().getName());
            }
            return new SimpleStringProperty("N/A");
        });
    }

    private void loadTableData() {
        try {
            List<Airplane> airplaneList = airplaneDAO.findAll();
            airplanes.setAll(airplaneList);
            tvAirplanes.setItems(airplanes);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al cargar los aviones: " + e.getMessage());
        }
    }

    @FXML
    private void btnAddAirplane(ActionEvent event) {
        openAirplaneForm(null);
    }

    @FXML
    private void btnEditAirplane(ActionEvent event) {
        Airplane selectedAirplane = tvAirplanes.getSelectionModel().getSelectedItem();
        if (selectedAirplane == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione un avión para editar.");
            return;
        }
        openAirplaneForm(selectedAirplane);
    }

    @FXML
    private void btnDeleteAirplane(ActionEvent event) {
        Airplane selectedAirplane = tvAirplanes.getSelectionModel().getSelectedItem();
        if (selectedAirplane == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione un avión para eliminar.");
            return;
        }

        boolean confirmed = DialogUtil.showConfirmationDialog("Confirmar eliminación",
                "¿Está seguro de que desea eliminar el avión con matrícula " + selectedAirplane.getRegistration() + "?");

        if (confirmed) {
            try {
                airplaneDAO.delete(selectedAirplane.getRegistration());
                DialogUtil.showInfoAlert("Éxito", "Avión eliminado exitosamente.");
                loadTableData(); // Refresh the table
            } catch (IOException | javafxappaerolinea.exception.ResourceNotFoundException e) {
                DialogUtil.showErrorAlert("Error", "Error al eliminar el avión: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btnExport(ActionEvent event) {
        try {
            List<Airplane> airplanesToExport = tvAirplanes.getItems();

            if (airplanesToExport.isEmpty()) {
                DialogUtil.showWarningAlert(
                    "Sin datos",
                    "No hay aviones para exportar."
                );
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportar Aviones");

            // Configurar los filtros de extensión
            FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
            FileChooser.ExtensionFilter xlsFilter = new FileChooser.ExtensionFilter("Excel (*.xls)", "*.xls");
            FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
            FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"); // Added JSON filter

            fileChooser.getExtensionFilters().addAll(csvFilter, xlsFilter, xlsxFilter, pdfFilter, jsonFilter);

            File file = fileChooser.showSaveDialog(tvAirplanes.getScene().getWindow());

            if (file != null) {
                String filePath = file.getAbsolutePath();
                String extension = getFileExtension(filePath).toLowerCase();

                String title = "Reporte de Aviones";
                String sheetName = "Aviones";

                switch (extension) {
                    case "csv":
                        ExportUtil.exportToCSV(airplanesToExport, filePath);
                        break;
                    case "xls":
                        ExportUtil.exportToXLS(airplanesToExport, filePath, sheetName);
                        break;
                    case "xlsx":
                        ExportUtil.exportToXLSX(airplanesToExport, filePath, sheetName);
                        break;
                    case "pdf":
                        ExportUtil.exportToPDF(airplanesToExport, filePath, title);
                        break;
                    case "json":
                        ExportUtil.exportToJSON(airplanesToExport, filePath);
                        break;
                    default:
                        // Si no tiene extensión o no es reconocida, usar XLSX por defecto
                        if (!filePath.endsWith(".xlsx")) {
                            filePath += ".xlsx";
                        }
                        ExportUtil.exportToXLSX(airplanesToExport, filePath, sheetName);
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
        } catch (Exception ex) {
            DialogUtil.showErrorAlert(
                "Error",
                "Error inesperado al exportar: " + ex.getMessage()
            );
            ex.printStackTrace();
        }
    }

    private void openAirplaneForm(Airplane airplane) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource("view/FXMLAirplaneForm.fxml"));
            Parent root = loader.load();

            FXMLAirplaneFormController controller = loader.getController();
            controller.initData(airplane);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(airplane == null ? "Agregar Avión" : "Editar Avión");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadTableData(); // Refresh table after form is closed
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al abrir el formulario de avión: " + e.getMessage());
        }
    }

    /**
     * Obtiene la extensión de un archivo a partir de su nombre.
     * Este método se utiliza para determinar el tipo de exportación.
     * @param fileName Nombre del archivo.
     * @return Extensión del archivo (ej. "xlsx", "csv", "pdf"), o cadena vacía si no tiene extensión.
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
}