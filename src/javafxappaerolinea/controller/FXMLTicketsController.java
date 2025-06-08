package javafxappaerolinea.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappaerolinea.model.dao.FlightDAO;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.ExportUtil;

/**
 * Controlador para la vista de boletos/vuelos
 */
public class FXMLTicketsController implements Initializable {
    
    @FXML
    private TableView<Flight> tableFlights;
    @FXML
    private TableColumn<Flight, String> columnId;
    @FXML
    private TableColumn<Flight, String> columnAirline;
    @FXML
    private TableColumn<Flight, String> columnOriginCity;
    @FXML
    private TableColumn<Flight, String> columnDestinationCity;
    @FXML
    private TableColumn<Flight, LocalDate> columnDepartureDate;
    @FXML
    private TableColumn<Flight, String> columnDepartureHour;
    @FXML
    private TableColumn<Flight, LocalDate> columnArrivalDate;
    @FXML
    private TableColumn<Flight, String> columnArrivalHour;
    @FXML
    private TableColumn<Flight, Double> columnTicketCost;
    @FXML
    private TableColumn<Flight, String> columnGate;
    @FXML
    private TableColumn<Flight, Integer> columnPassengerCount;
    @FXML
    private TableColumn<Flight, String> columnTravelTime;
    
    @FXML
    private TextField txtOriginFilter;
    @FXML
    private TextField txtDestinationFilter;
    @FXML
    private DatePicker dpDepartureDateFilter;
    @FXML
    private Button btnApplyFilter;
    @FXML
    private Button btnClearFilter;
    @FXML
    private Button btnBuyTicket;
    @FXML
    private Button btnExport;
    
    private ObservableList<Flight> flights;
    private ObservableList<Flight> filteredFlights;
    private FlightDAO flightDAO;
    
    /**
     * Inicializa el controlador
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        flightDAO = new FlightDAO();
        configureTableColumns();
        loadFlights();
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configureTableColumns() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnAirline.setCellValueFactory(new PropertyValueFactory<>("airline.name"));
        columnOriginCity.setCellValueFactory(new PropertyValueFactory<>("origin"));
        columnDestinationCity.setCellValueFactory(new PropertyValueFactory<>("destination"));
        columnDepartureDate.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        columnDepartureHour.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        columnArrivalDate.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        columnArrivalHour.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        columnTicketCost.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnGate.setCellValueFactory(new PropertyValueFactory<>("gate"));
        columnPassengerCount.setCellValueFactory(new PropertyValueFactory<>("passengerCount"));
        columnTravelTime.setCellValueFactory(new PropertyValueFactory<>("travelTime"));
    }
    
    /**
     * Carga los vuelos desde la base de datos
     */
    private void loadFlights() {
        flights = FXCollections.observableArrayList();
        try {
            List<Flight> flightsList = flightDAO.findAll();
            flights.addAll(flightsList);
            filteredFlights = FXCollections.observableArrayList(flights);
            tableFlights.setItems(filteredFlights);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error al cargar vuelos", 
                    "No se pudieron cargar los vuelos: " + e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de aplicar filtros
     */
    @FXML
    private void handleApplyFilter(ActionEvent event) {
        String origin = txtOriginFilter.getText().trim().toLowerCase();
        String destination = txtDestinationFilter.getText().trim().toLowerCase();
        LocalDate departureDate = dpDepartureDateFilter.getValue();
        
        filteredFlights = FXCollections.observableArrayList(flights.stream()
                .filter(flight -> 
                    (origin.isEmpty() || flight.getOriginCity().toLowerCase().contains(origin)) &&
                    (destination.isEmpty() || flight.getDestinationCity().toLowerCase().contains(destination)) &&
                    (departureDate == null || flight.getDepartureDate().equals(departureDate))
                )
                .collect(Collectors.toList()));
        
        tableFlights.setItems(filteredFlights);
        
        if (filteredFlights.isEmpty()) {
            DialogUtil.showInfoAlert("Sin resultados", 
                    "No se encontraron vuelos con los criterios especificados.");
        }
    }
    
    /**
     * Maneja el evento de limpiar filtros
     */
    @FXML
    private void handleClearFilter(ActionEvent event) {
        txtOriginFilter.clear();
        txtDestinationFilter.clear();
        dpDepartureDateFilter.setValue(null);
        
        filteredFlights = FXCollections.observableArrayList(flights);
        tableFlights.setItems(filteredFlights);
    }
    
    /**
     * Maneja el evento de comprar un boleto
     */
    @FXML
    private void handleBuyTicket(ActionEvent event) {
        Flight selectedFlight = tableFlights.getSelectionModel().getSelectedItem();
        if (selectedFlight == null) {
            DialogUtil.showWarningAlert("Selección requerida", 
                    "Debe seleccionar un vuelo para comprar un boleto.");
            return;
        }
        
        if (selectedFlight.getPassengerCount() >= selectedFlight.getAirplane().getCapacity()) {
            DialogUtil.showWarningAlert("Vuelo lleno", 
                    "El vuelo seleccionado no tiene asientos disponibles.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLBuyTicket.fxml"));
            Parent root = loader.load();
            
            FXMLBuyTicketController controller = loader.getController();
            controller.setFlight(selectedFlight);
            controller.setTicketsController(this);
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Comprar Boleto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", 
                    "No se pudo abrir la ventana para comprar boleto: " + e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de exportar la tabla de vuelos
     */
    @FXML
    private void handleExport(ActionEvent event) {
        if (filteredFlights == null || filteredFlights.isEmpty()) {
            DialogUtil.showWarningAlert("Sin datos", 
                    "No hay datos para exportar.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Vuelos");
        
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
                        ExportUtil.exportToXLSX(new ArrayList<>(filteredFlights), filePath, "Vuelos");
                        break;
                    case "xls":
                        ExportUtil.exportToXLS(new ArrayList<>(filteredFlights), filePath, "Vuelos");
                        break;
                    case "csv":
                        ExportUtil.exportToCSV(new ArrayList<>(filteredFlights), filePath);
                        break;
                    case "pdf":
                        ExportUtil.exportToPDF(new ArrayList<>(filteredFlights), filePath, "Listado de Vuelos");
                        break;
                    case "json":
                        ExportUtil.exportToJSON(new ArrayList<>(filteredFlights), filePath);
                        break;
                    default:
                        // Si no tiene extensión o no es reconocida, usar XLSX por defecto
                        if (!filePath.endsWith(".xlsx")) {
                            filePath += ".xlsx";
                        }
                        ExportUtil.exportToXLSX(new ArrayList<>(filteredFlights), filePath, "Vuelos");
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
     * Actualiza la tabla de vuelos
     */
    public void refreshFlights() {
        loadFlights();
    }
}