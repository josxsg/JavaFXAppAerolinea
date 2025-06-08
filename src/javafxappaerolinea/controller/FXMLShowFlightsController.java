package javafxappaerolinea.controller;

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
import javafx.scene.control.TableCell; // Importar TableCell
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappaerolinea.model.dao.FlightDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.utility.DialogUtil;

public class FXMLShowFlightsController implements Initializable {

    @FXML
    private TableView<Flight> tvFlights;
    @FXML
    private TableColumn<Flight, String> tcId;
    @FXML
    private TableColumn<Flight, String> tcOriginCity;
    @FXML
    private TableColumn<Flight, String> tcDestinationCity;
    @FXML
    private TableColumn<Flight, Date> tcDepartureDate; // Corrected type to Date
    @FXML
    private TableColumn<Flight, String> tcDepartureHour;
    @FXML
    private TableColumn<Flight, Date> tcArrivalDate; // Corrected type to Date
    @FXML
    private TableColumn<Flight, String> tcArrivalHour; // Corrected type to String
    @FXML
    private TableColumn<Flight, Double> tcTravelTime;
    @FXML
    private TableColumn<Flight, Integer> tcPassengerCount;
    @FXML
    private TableColumn<Flight, String> tcGate;
    @FXML
    private TableColumn<Flight, String> tcAirplane;
    @FXML
    private TableColumn<Flight, String> tcAirline;
    @FXML
    private TextField tfFilterDestination;

    private Airline currentAirline;
    private FlightDAO flightDAO;
    private ObservableList<Flight> flights;
    private ObservableList<Flight> filteredFlights;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        flightDAO = new FlightDAO();
        flights = FXCollections.observableArrayList();
        filteredFlights = FXCollections.observableArrayList();
        tvFlights.setItems(filteredFlights);
        configureTableColumns();
    }

    public void initData(Airline airline) {
        this.currentAirline = airline;
        loadFlightsForAirline();
    }

    private void configureTableColumns() {
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcOriginCity.setCellValueFactory(new PropertyValueFactory<>("originCity"));
        tcDestinationCity.setCellValueFactory(new PropertyValueFactory<>("destinationCity"));

        // Corrected setup for Date columns:
        // 1. setCellValueFactory maps the property
        tcDepartureDate.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        // 2. setCellFactory defines how the Date object is displayed as a String
        tcDepartureDate.setCellFactory(column -> {
            return new TableCell<Flight, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(dateFormat.format(item));
                    }
                }
            };
        });

        tcArrivalDate.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        tcArrivalDate.setCellFactory(column -> {
            return new TableCell<Flight, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(dateFormat.format(item));
                    }
                }
            };
        });

        tcDepartureHour.setCellValueFactory(new PropertyValueFactory<>("departureHour"));
        tcArrivalHour.setCellValueFactory(new PropertyValueFactory<>("arrivalHour")); 
        
        tcTravelTime.setCellValueFactory(new PropertyValueFactory<>("travelTime"));
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
    }

    private void loadFlightsForAirline() {
        if (currentAirline == null) {
            DialogUtil.showWarningAlert("Error", "No se ha seleccionado ninguna aerolínea.");
            return;
        }
        try {
            List<Flight> flightsList = flightDAO.findByAirline(currentAirline.getIdentificationNumber());
            flights.setAll(flightsList);
            filteredFlights.setAll(flightsList);
            tvFlights.setItems(filteredFlights);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al cargar los vuelos de la aerolínea: " + e.getMessage());
        }
    }

    @FXML
    private void btnFilter(ActionEvent event) {
        String filterText = tfFilterDestination.getText().trim().toLowerCase();
        if (filterText.isEmpty()) {
            filteredFlights.setAll(flights);
        } else {
            List<Flight> result = flights.stream()
                    .filter(flight -> flight.getDestinationCity().toLowerCase().contains(filterText))
                    .collect(Collectors.toList());
            filteredFlights.setAll(result);
        }
        if (filteredFlights.isEmpty()) {
            DialogUtil.showInfoAlert("Sin resultados", "No se encontraron vuelos para el destino especificado.");
        }
    }

    @FXML
    private void btnClearFilter(ActionEvent event) {
        tfFilterDestination.clear();
        filteredFlights.setAll(flights);
    }

    @FXML
    private void btnViewDetails(ActionEvent event) {
        Flight selectedFlight = tvFlights.getSelectionModel().getSelectedItem();
        if (selectedFlight == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione un vuelo para ver detalles.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLFlightDetails.fxml"));
            Parent root = loader.load();

            FXMLFlightDetailsController controller = loader.getController();
            controller.initData(selectedFlight);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Detalles del Vuelo");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al abrir la vista de detalles del vuelo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void btnExport(ActionEvent event) {
        // This method is intentionally left empty as per user request to omit export functionality.
        // If export is needed in the future, the code can be re-added here.
    }
    
    // Removed the getFileExtension helper method as it was only used by btnExport.
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void btnClose(ActionEvent event) {
        Stage stage = (Stage) tvFlights.getScene().getWindow();
        stage.close();
    }
}