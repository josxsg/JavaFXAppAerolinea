package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javafx.stage.Stage;
import javafx.stage.FileChooser; 
import java.io.File; 
import javafxappaerolinea.JavaFXAppAerolinea;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.FlightDAO;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.ExportUtil; 


public class FXMLFlightController implements Initializable {

    @FXML
    private TableView<Flight> tvFlights;
    @FXML
    private TableColumn<Flight, String> tcId;
    @FXML
    private TableColumn<Flight, String> tcOriginCity;
    @FXML
    private TableColumn<Flight, String> tcDestinationCity;
    @FXML
    private TableColumn<Flight, String> tcDepartureDate;
    @FXML
    private TableColumn<Flight, String> tcDepartureHour;
    @FXML
    private TableColumn<Flight, String> tcArrivalDate;
    @FXML
    private TableColumn<Flight, String> tcArrivalHour;
    @FXML
    private TableColumn<Flight, Double> tcTravelTime;
    @FXML
    private TableColumn<Flight, Double> tcTicketCost;
    @FXML
    private TableColumn<Flight, Integer> tcPassengerCount;
    @FXML
    private TableColumn<Flight, String> tcGate;
    @FXML
    private TableColumn<Flight, String> tcAirplane;
    @FXML
    private TableColumn<Flight, String> tcAirline;

    private FlightDAO flightDAO;
    private ObservableList<Flight> flights;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        flightDAO = new FlightDAO();
        flights = FXCollections.observableArrayList();
        configureTable();
        loadTableData();
    }

    private void configureTable() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcOriginCity.setCellValueFactory(new PropertyValueFactory<>("originCity"));
        tcDestinationCity.setCellValueFactory(new PropertyValueFactory<>("destinationCity"));

        tcDepartureDate.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getDepartureDate();
            return new SimpleStringProperty(date != null ? dateFormat.format(date) : "");
        });

        tcDepartureHour.setCellValueFactory(new PropertyValueFactory<>("departureHour"));

        tcArrivalDate.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getArrivalDate();
            return new SimpleStringProperty(date != null ? dateFormat.format(date) : "");
        });

        tcArrivalHour.setCellValueFactory(new PropertyValueFactory<>("arrivalHour"));
        tcTravelTime.setCellValueFactory(new PropertyValueFactory<>("travelTime"));
        tcTicketCost.setCellValueFactory(new PropertyValueFactory<>("ticketCost"));
        tcPassengerCount.setCellValueFactory(new PropertyValueFactory<>("passengerCount"));
        tcGate.setCellValueFactory(new PropertyValueFactory<>("gate"));

        tcAirplane.setCellValueFactory(cellData -> {
            if (cellData.getValue().getAirplane() != null) {
                return new SimpleStringProperty(cellData.getValue().getAirplane().getModel());
            }
            return new SimpleStringProperty("N/A");
        });

        tcAirline.setCellValueFactory(cellData -> {
            if (cellData.getValue().getAirline() != null) {
                return new SimpleStringProperty(cellData.getValue().getAirline().getName());
            }
            return new SimpleStringProperty("N/A");
        });

        tvFlights.setItems(flights);
    }

    private void loadTableData() {
        try {
            List<Flight> flightList = flightDAO.findAll();
            flights.setAll(flightList);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al cargar los vuelos: " + e.getMessage());
        }
    }

    @FXML
    private void btnAddFlight(ActionEvent event) {
        openFlightForm(null);
    }

    @FXML
    private void btnEditFlight(ActionEvent event) {
        Flight selectedFlight = tvFlights.getSelectionModel().getSelectedItem();
        if (selectedFlight == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione un vuelo para editar.");
            return;
        }
        openFlightForm(selectedFlight);
    }

    @FXML
    private void btnDeleteFlight(ActionEvent event) {
        Flight selectedFlight = tvFlights.getSelectionModel().getSelectedItem();
        if (selectedFlight == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione un vuelo para eliminar.");
            return;
        }

        boolean confirmed = DialogUtil.showConfirmationDialog("Confirmar eliminación",
                "¿Está seguro de que desea eliminar el vuelo " + selectedFlight.getId() + "?");

        if (confirmed) {
            try {
                flightDAO.delete(selectedFlight.getId());
                DialogUtil.showInfoAlert("Éxito", "Vuelo eliminado exitosamente.");
                loadTableData();
            } catch (IOException | ResourceNotFoundException e) {
                DialogUtil.showErrorAlert("Error", "Error al eliminar el vuelo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btnExport(ActionEvent event) {
        try {
            List<Flight> flightsToExport = tvFlights.getItems();

            if (flightsToExport.isEmpty()) {
                DialogUtil.showWarningAlert(
                    "Sin datos",
                    "No hay vuelos para exportar."
                );
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportar Vuelos");

            FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
            FileChooser.ExtensionFilter xlsFilter = new FileChooser.ExtensionFilter("Excel (*.xls)", "*.xls");
            FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
            FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");

            fileChooser.getExtensionFilters().addAll(csvFilter, xlsFilter, xlsxFilter, pdfFilter, jsonFilter);

            File file = fileChooser.showSaveDialog(tvFlights.getScene().getWindow());

            if (file != null) {
                String filePath = file.getAbsolutePath();
                String extension = getFileExtension(filePath).toLowerCase();

                String title = "Reporte de Vuelos";
                String sheetName = "Vuelos";

                switch (extension) {
                    case "csv":
                        ExportUtil.exportToCSV(flightsToExport, filePath);
                        break;
                    case "xls":
                        ExportUtil.exportToXLS(flightsToExport, filePath, sheetName);
                        break;
                    case "xlsx":
                        ExportUtil.exportToXLSX(flightsToExport, filePath, sheetName);
                        break;
                    case "pdf":
                        ExportUtil.exportToPDF(flightsToExport, filePath, title);
                        break;
                    case "json":
                        ExportUtil.exportToJSON(flightsToExport, filePath);
                        break;
                    default:
                        if (!filePath.endsWith(".xlsx")) {
                            filePath += ".xlsx";
                        }
                        ExportUtil.exportToXLSX(flightsToExport, filePath, sheetName);
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
        }
    }

    @FXML
    private void btnViewDetails(ActionEvent event) {
        Flight selectedFlight = tvFlights.getSelectionModel().getSelectedItem();
        if (selectedFlight == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione un vuelo para ver detalles.");
            return;
        }
        openFlightDetails(selectedFlight);
    }

    private void openFlightForm(Flight flight) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource("view/FXMLFlightForm.fxml"));
            Parent root = loader.load();

            FXMLFlightFormController controller = loader.getController();
            
            controller.initData(flight); 

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(flight == null ? "Agregar Vuelo" : "Editar Vuelo");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadTableData();
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al abrir el formulario de vuelo: " + e.getMessage());
        }
    }

    private void openFlightDetails(Flight flight) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource("view/FXMLFlightDetails.fxml"));
            Parent root = loader.load();

            FXMLFlightDetailsController controller = loader.getController();
            controller.initData(flight);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle("Detalles del Vuelo");
            stage.setScene(new Scene(root));
            stage.showAndWait(); 

        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al abrir la vista de detalles del vuelo: " + e.getMessage());
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