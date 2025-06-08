package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappaerolinea.model.dao.AirplaneDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Airplane;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.ExportUtil;

public class FXMLShowAirplanesController implements Initializable {

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

    private ObservableList<Airplane> airplanes;
    private AirplaneDAO airplaneDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        airplaneDAO = new AirplaneDAO();
        airplanes = FXCollections.observableArrayList();
        tvAirplanes.setItems(airplanes);
        configureTable();
    }    

    public void initData(Airline selectedAirline) {
        if (selectedAirline == null) {
            return;
        }
        
        try {
            List<Airplane> allAirplanes = airplaneDAO.findAll();
            
            List<Airplane> filteredAirplanes = allAirplanes.stream()
                .filter(plane -> plane.getAirline() != null &&
                                 plane.getAirline().getIdentificationNumber() == selectedAirline.getIdentificationNumber()) // LÍNEA CORREGIDA
                .collect(Collectors.toList());
            
            airplanes.setAll(filteredAirplanes);
            
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al cargar los datos de los aviones: " + e.getMessage());
        }
    }

    private void configureTable() {
        tcRegistration.setCellValueFactory(new PropertyValueFactory<>("registration"));
        tcModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        tcCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tcAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        tcWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));

        tcStatus.setCellValueFactory(cellData -> {
            boolean status = cellData.getValue().isStatus();
            return new SimpleStringProperty(status ? "Activo" : "Inactivo");
        });

        tcAirline.setCellValueFactory(cellData -> {
            if (cellData.getValue().getAirline() != null) {
                return new SimpleStringProperty(cellData.getValue().getAirline().getName());
            }
            return new SimpleStringProperty("N/A");
        });
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

            FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
            FileChooser.ExtensionFilter xlsFilter = new FileChooser.ExtensionFilter("Excel (*.xls)", "*.xls");
            FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
            FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");

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

    @FXML
    private void btnClose(ActionEvent event) {
        Stage stage = (Stage) tvAirplanes.getScene().getWindow();
        stage.close();
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
}