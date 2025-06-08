package javafxappaerolinea.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory; // Import this class
import javafx.stage.Stage;
import javafx.event.ActionEvent; 
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Flight;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLFlightDetailsController implements Initializable {

    @FXML
    private Label lbFlightId;
    
    @FXML
    private Label lbOrigin;
    
    @FXML
    private Label lbDestination;
    
    @FXML
    private Label lbDepartureDate;
    
    @FXML
    private Label lbDepartureHour;
    
    @FXML
    private Label lbArrivalDate;
    
    @FXML
    private Label lbArrivalHour;
    
    @FXML
    private Label lbGate;
    
    @FXML
    private Label lbPassengerCount;
    
    @FXML
    private Label lbTicketCost;
    
    @FXML
    private Label lbTravelTime;
    
    @FXML
    private Label lbAirplane;
    
    @FXML
    private Label lbAirline;
    
    // Removed old ListView declarations

    @FXML
    private TableView<Pilot> tvPilots; // Corrected type parameter
    @FXML
    private TableColumn<Pilot, String> tcPilotName; // Corrected type parameter
    @FXML
    private TableColumn<Pilot, String> tcPilotLicense; // Corrected type parameter
    @FXML
    private TableColumn<Pilot, Integer> tcPilotYearsExperience; // Corrected type parameter
    @FXML
    private TableColumn<Pilot, Double> tcPilotFlightHours; // Corrected type parameter
    @FXML
    private TableColumn<Pilot, String> tcPilotEmail; // Corrected type parameter
    
    @FXML
    private TableView<Assistant> tvAssistants; // Corrected type parameter
    @FXML
    private TableColumn<Assistant, String> tcAssistantName; // Corrected type parameter
    @FXML
    private TableColumn<Assistant, Integer> tcAssistantAssistanceHours; // Corrected type parameter
    @FXML
    private TableColumn<Assistant, Integer> tcAssistantLanguages; // Corrected type parameter
    @FXML
    private TableColumn<Assistant, String> tcAssistantEmail; // Corrected type parameter

    private ObservableList<Pilot> pilotsObservableList;
    private ObservableList<Assistant> assistantsObservableList;

    private Flight flight;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize ObservableLists
        pilotsObservableList = FXCollections.observableArrayList();
        assistantsObservableList = FXCollections.observableArrayList();
        
        // Set ObservableLists to TableViews
        tvPilots.setItems(pilotsObservableList);
        tvAssistants.setItems(assistantsObservableList);
        
        // Configure table columns
        configurePilotTable();
        configureAssistantTable();
    }    

    public void initData(Flight flight) {
        this.flight = flight;
        loadFlightDetails();
    }
    
    private void loadFlightDetails() {
        if (flight != null) {
            lbFlightId.setText(flight.getId());
            lbOrigin.setText(flight.getOriginCity());
            lbDestination.setText(flight.getDestinationCity());
            lbDepartureDate.setText(dateFormat.format(flight.getDepartureDate()));
            lbDepartureHour.setText(flight.getDepartureHour());
            lbArrivalDate.setText(dateFormat.format(flight.getArrivalDate()));
            lbArrivalHour.setText(String.valueOf(flight.getArrivalHour()));
            lbGate.setText(flight.getGate());
            lbPassengerCount.setText(String.valueOf(flight.getPassengerCount()));
            lbTicketCost.setText(String.format("$%.2f", flight.getTicketCost()));
            lbTravelTime.setText(String.format("%.2f horas", flight.getTravelTime()));
            
            // Información del avión
            if (flight.getAirplane() != null) {
                lbAirplane.setText(flight.getAirplane().getModel() + " (Capacidad: " + 
                                   flight.getAirplane().getCapacity() + ")");
            } else {
                lbAirplane.setText("No asignado");
            }
            
            // Información de la aerolínea
            if (flight.getAirline() != null) {
                lbAirline.setText(flight.getAirline().getName());
            } else {
                lbAirline.setText("No asignada");
            }
            
            // Populate Piloting TableView
            if (flight.getPilots() != null) {
                pilotsObservableList.setAll(flight.getPilots());
            } else {
                pilotsObservableList.clear(); // Clear if no pilots
            }
            
            // Populate Assistants TableView
            if (flight.getAssistants() != null) {
                assistantsObservableList.setAll(flight.getAssistants());
            } else {
                assistantsObservableList.clear(); // Clear if no assistants
            }
        }
    }
    
    // Configures the pilot table columns with PropertyValueFactory
    private void configurePilotTable() {
        tcPilotName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcPilotLicense.setCellValueFactory(new PropertyValueFactory<>("licenseType"));
        tcPilotYearsExperience.setCellValueFactory(new PropertyValueFactory<>("yearsExperience"));
        tcPilotFlightHours.setCellValueFactory(new PropertyValueFactory<>("flightHours"));
        tcPilotEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    
    // Configures the assistant table columns with PropertyValueFactory
    private void configureAssistantTable() {
        tcAssistantName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAssistantAssistanceHours.setCellValueFactory(new PropertyValueFactory<>("assistanceHours"));
        tcAssistantLanguages.setCellValueFactory(new PropertyValueFactory<>("numberOfLanguages"));
        tcAssistantEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    
    @FXML
    private void btnClose(ActionEvent event) {
        Stage stage = (Stage) lbFlightId.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void btnExport(ActionEvent event) {
    }
}