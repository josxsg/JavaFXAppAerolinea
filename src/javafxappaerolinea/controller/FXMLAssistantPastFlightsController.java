package javafxappaerolinea.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappaerolinea.model.dao.FlightDAO;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.service.SessionManager;
import javafxappaerolinea.utility.ExportUtil;

public class FXMLAssistantPastFlightsController implements Initializable {
    
    @FXML
    private TableView<Flight> tvPastFlights;
    
    @FXML
    private TableColumn<Flight, String> tcId;
    
    @FXML
    private TableColumn<Flight, String> tcOrigin;
    
    @FXML
    private TableColumn<Flight, String> tcDestination;
    
    @FXML
    private TableColumn<Flight, Date> tcDepartureDate;
    
    @FXML
    private TableColumn<Flight, String> tcDepartureHour;
    
    @FXML
    private TableColumn<Flight, Date> tcArrivalDate;
    
    @FXML
    private TableColumn<Flight, Integer> tcArrivalHour;
    
    @FXML
    private TableColumn<Flight, Integer> tcPassengerCount;
    
    @FXML
    private TableColumn<Flight, String> tcAirline;
    
    @FXML
    private ComboBox<String> cbFilterType;
    
    @FXML
    private TextField tfFilterValue;
    
    @FXML
    private Label lbTotalAssistanceHours;
    
    private ObservableList<Flight> pastFlights;
    private Assistant loggedAssistant;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        configureFilterComboBox();
        loadPastFlights();
    }
    
    private void configureTableColumns() {
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcOrigin.setCellValueFactory(new PropertyValueFactory<>("originCity"));
        tcDestination.setCellValueFactory(new PropertyValueFactory<>("destinationCity"));
        tcDepartureDate.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        tcDepartureHour.setCellValueFactory(new PropertyValueFactory<>("departureHour"));
        tcArrivalDate.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        tcArrivalHour.setCellValueFactory(new PropertyValueFactory<>("arrivalHour"));
        tcPassengerCount.setCellValueFactory(new PropertyValueFactory<>("passengerCount"));
        
        tcAirline.setCellValueFactory(cellData -> {
            if (cellData.getValue().getAirline() != null) {
                return new SimpleStringProperty(cellData.getValue().getAirline().getName());
            }
            return new SimpleStringProperty("");
        });
        
        tcDepartureDate.setCellFactory(column -> {
            return new TableCell<Flight, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(dateFormat.format(item));
                    }
                }
            };
        });
        
        tcArrivalDate.setCellFactory(column -> {
            return new TableCell<Flight, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(dateFormat.format(item));
                    }
                }
            };
        });
    }
    
    private void configureFilterComboBox() {
        ObservableList<String> filterOptions = FXCollections.observableArrayList(
                "Origen", "Destino", "Fecha de Salida", "Aerolínea"
        );
        cbFilterType.setItems(filterOptions);
        cbFilterType.getSelectionModel().selectFirst();
    }
    
    private void loadPastFlights() {
        try {
            loggedAssistant = (Assistant) SessionManager.getInstance().getCurrentUser();
            if (loggedAssistant != null) {
                FlightDAO flightDAO = new FlightDAO();
                List<Flight> allFlights = flightDAO.findAll();
            
                Date today = new Date();
                List<Flight> completedFlights = allFlights.stream()
                    .filter(flight -> flight.getAssistants().stream()
                        .anyMatch(assistant -> assistant.getId().equals(loggedAssistant.getId())))
                    .filter(flight -> flight.getDepartureDate().before(today))
                    .collect(Collectors.toList());
            
                pastFlights = FXCollections.observableArrayList(completedFlights);
                tvPastFlights.setItems(pastFlights);
            
                lbTotalAssistanceHours.setText(String.valueOf(loggedAssistant.getAssistanceHours()));
            }
        } catch (IOException ex) {
            showAlert("Error", "Error al cargar los datos: " + ex.getMessage(), Alert.AlertType.ERROR);
        } 
    }
    
    @FXML
    private void btnFilter(ActionEvent event) {
        String filterType = cbFilterType.getValue();
        String filterValue = tfFilterValue.getText().trim().toLowerCase();
        
        if (filterValue.isEmpty()) {
            tvPastFlights.setItems(pastFlights);
            return;
        }
        
        ObservableList<Flight> filteredList = FXCollections.observableArrayList();
        
        switch (filterType) {
            case "Origen":
                filteredList = pastFlights.stream()
                        .filter(flight -> flight.getOriginCity().toLowerCase().contains(filterValue))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Destino":
                filteredList = pastFlights.stream()
                        .filter(flight -> flight.getDestinationCity().toLowerCase().contains(filterValue))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Fecha de Salida":
                filteredList = pastFlights.stream()
                        .filter(flight -> dateFormat.format(flight.getDepartureDate()).contains(filterValue))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Aerolínea":
                filteredList = pastFlights.stream()
                        .filter(flight -> flight.getAirline() != null && 
                                flight.getAirline().getName().toLowerCase().contains(filterValue))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
        }
        
        tvPastFlights.setItems(filteredList);
    }
    
    @FXML
    private void btnClearFilter(ActionEvent event) {
        tfFilterValue.clear();
        tvPastFlights.setItems(pastFlights);
    }
    
    @FXML
    private void btnViewDetails(ActionEvent event) {
        Flight selectedFlight = tvPastFlights.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLFlightDetails.fxml"));
                Parent root = loader.load();
                
                FXMLFlightDetailsController controller = loader.getController();
                controller.initData(selectedFlight);
                
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Detalles del Vuelo");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException ex) {
                showAlert("Error de carga", "No se pudo cargar el archivo FXML: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Selección Requerida", "Por favor, seleccione un vuelo para ver sus detalles.", Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void btnExport(ActionEvent event) {
        try {
            List<Flight> flightsToExport = tvPastFlights.getItems();
        
            if (flightsToExport.isEmpty()) {
                showAlert("Sin datos", "No hay vuelos para exportar.", Alert.AlertType.WARNING);
                return;
            }
        
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Archivo");
        
            FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
            FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
            FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
        
            fileChooser.getExtensionFilters().addAll(csvFilter, xlsxFilter, pdfFilter, jsonFilter);
        
            File file = fileChooser.showSaveDialog(tvPastFlights.getScene().getWindow());
        
            if (file != null) {
                String filePath = file.getAbsolutePath();
                String extension = getFileExtension(file.getName()).toLowerCase();
                
                String userType = "";
                Object currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser instanceof Pilot) {
                    userType = "Piloto";
                } else if (currentUser instanceof Assistant) {
                    userType = "Asistente";
                }
            
                String flightType = "";
                if (this.getClass().getSimpleName().contains("Upcoming")) {
                    flightType = "Próximos";
                } else if (this.getClass().getSimpleName().contains("Past")) {
                    flightType = "Pasados";
                }
            
                String title = "Vuelos " + flightType + " - " + userType;
                String sheetName = "Vuelos" + flightType;
            
                switch (extension) {
                    case "csv":
                        ExportUtil.exportToCSV(flightsToExport, filePath);
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
                }
            
                showAlert("Exportación Exitosa", 
                        "Los datos se han exportado correctamente a: " + file.getName(), 
                        Alert.AlertType.INFORMATION);
            }
        } catch (IOException ex) {
            showAlert("Error", "Error al exportar los datos: " + ex.getMessage(), Alert.AlertType.ERROR);
        } 
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; 
        }
        return fileName.substring(lastIndexOf + 1);
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}