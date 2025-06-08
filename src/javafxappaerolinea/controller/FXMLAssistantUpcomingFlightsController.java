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

public class FXMLAssistantUpcomingFlightsController implements Initializable {
    
    @FXML
    private TableView<Flight> tvUpcomingFlights;
    
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
    private TableColumn<Flight, String> tcGate;
    
    @FXML
    private TableColumn<Flight, Integer> tcPassengerCount;
    
    @FXML
    private TableColumn<Flight, String> tcAirline;
    
    @FXML
    private TextField tfFilterDestination;
    
    private ObservableList<Flight> upcomingFlights;
    private Assistant currentAssistant; // Changed from loggedAssistant and made accessible for initData
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        // loadUpcomingFlights(); // Removed this call from here, it will be called by initData
    }

    // New method to initialize data for the controller
    public void initData(Assistant assistant) {
        this.currentAssistant = assistant;
        loadUpcomingFlights(); // Call loadUpcomingFlights after the assistant is set
    }
    
    private void configureTableColumns() {
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcOrigin.setCellValueFactory(new PropertyValueFactory<>("originCity"));
        tcDestination.setCellValueFactory(new PropertyValueFactory<>("destinationCity"));
        tcDepartureDate.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        tcDepartureHour.setCellValueFactory(new PropertyValueFactory<>("departureHour"));
        tcArrivalDate.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        tcArrivalHour.setCellValueFactory(new PropertyValueFactory<>("arrivalHour"));
        tcGate.setCellValueFactory(new PropertyValueFactory<>("gate"));
        tcPassengerCount.setCellValueFactory(new PropertyValueFactory<>("passengerCount"));
        
        // Configuración para mostrar el nombre de la aerolínea
        tcAirline.setCellValueFactory(cellData -> {
            if (cellData.getValue().getAirline() != null) {
                return new SimpleStringProperty(cellData.getValue().getAirline().getName());
            }
            return new SimpleStringProperty("");
        });
        
        // Formato para las fechas
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
    
    private void loadUpcomingFlights() {
        try {
            // Use the currentAssistant object passed via initData
            if (currentAssistant != null) {
                // Obtener todos los vuelos
                FlightDAO flightDAO = new FlightDAO();
                List<Flight> allFlights = flightDAO.findAll();
            
                // Filtrar los vuelos donde el asistente actual está asignado
                Date today = new Date();
                List<Flight> futureFlights = allFlights.stream()
                    .filter(flight -> flight.getAssistants().stream()
                        .anyMatch(assistant -> assistant.getId().equals(currentAssistant.getId())))
                    .filter(flight -> flight.getDepartureDate().after(today))
                    .collect(Collectors.toList());
                
                upcomingFlights = FXCollections.observableArrayList(futureFlights);
                tvUpcomingFlights.setItems(upcomingFlights);
            } else {
                showAlert("Error de datos", "No se proporcionó información del asistente para cargar los vuelos.", Alert.AlertType.ERROR);
            }
        } catch (IOException ex) {
            showAlert("Error", "Error al cargar los datos: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception ex) {
            showAlert("Error", "No se pudieron cargar los vuelos programados: " + ex.getMessage(), Alert.AlertType.ERROR);
            ex.printStackTrace(); // Print stack trace for debugging unexpected exceptions
        }
    }
    
    @FXML
    private void btnFilter(ActionEvent event) {
        String filterText = tfFilterDestination.getText().trim().toLowerCase();
        if (filterText.isEmpty()) {
            tvUpcomingFlights.setItems(upcomingFlights);
        } else {
            ObservableList<Flight> filteredList = upcomingFlights.stream()
                    .filter(flight -> flight.getDestinationCity().toLowerCase().contains(filterText))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            tvUpcomingFlights.setItems(filteredList);
        }
    }
    
    @FXML
    private void btnClearFilter(ActionEvent event) {
        tfFilterDestination.clear();
        tvUpcomingFlights.setItems(upcomingFlights);
    }
    
    @FXML
    private void btnViewDetails(ActionEvent event) {
        Flight selectedFlight = tvUpcomingFlights.getSelectionModel().getSelectedItem();
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
            } catch (Exception ex) {
                showAlert("Error", "No se pudo abrir la ventana de detalles: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Selección Requerida", "Por favor, seleccione un vuelo para ver sus detalles.", Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void btnExport(ActionEvent event) {
        try {
            // Obtener los vuelos filtrados para exportar
            List<Flight> flightsToExport = tvUpcomingFlights.getItems();
        
            if (flightsToExport.isEmpty()) {
                showAlert("Sin datos", "No hay vuelos para exportar.", Alert.AlertType.WARNING);
                return;
            }
        
            // Configurar el diálogo de guardar archivo
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Archivo");
        
            // Configurar los filtros de extensión
            FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
            FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
            FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
        
            fileChooser.getExtensionFilters().addAll(csvFilter, xlsxFilter, pdfFilter, jsonFilter);
        
            // Mostrar el diálogo de guardar
            File file = fileChooser.showSaveDialog(tvUpcomingFlights.getScene().getWindow());
        
            if (file != null) {
                String filePath = file.getAbsolutePath();
                String extension = getFileExtension(file.getName()).toLowerCase();
                
                // Determinar el tipo de usuario para personalizar el título
                String userType = "";
                Object currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser instanceof Pilot) {
                    userType = "Piloto";
                } else if (currentUser instanceof Assistant) {
                    userType = "Asistente";
                }
            
                // Determinar si son vuelos próximos o pasados para el título
                String flightType = "";
                if (this.getClass().getSimpleName().contains("Upcoming")) {
                    flightType = "Próximos";
                } else if (this.getClass().getSimpleName().contains("Past")) {
                    flightType = "Pasados";
                }
            
                // Crear título para el documento
                String title = "Vuelos " + flightType + " - " + userType;
                // Crear nombre para la hoja de Excel
                String sheetName = "Vuelos" + flightType;
            
                // Exportar según el formato seleccionado
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
        } catch (Exception ex) {
            showAlert("Error", "Error inesperado: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Obtiene la extensión de un archivo a partir de su nombre
     * @param fileName Nombre del archivo
     * @return Extensión del archivo
     */
    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // No hay extensión
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