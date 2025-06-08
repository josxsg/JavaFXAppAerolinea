package javafxappaerolinea.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent; 
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Flight;

public class FXMLShowFlightDetailsController implements Initializable {

    @FXML
    private TableView<Pilot> tvPilots;
    @FXML
    private TableColumn<Pilot, String> tcPilotName;
    @FXML
    private TableColumn<Pilot, String> tcPilotLicense;
    @FXML
    private TableColumn<Pilot, Integer> tcPilotYearsExperience;
    @FXML
    private TableColumn<Pilot, Double> tcPilotFlightHours; 
    @FXML
    private TableColumn<Pilot, String> tcPilotEmail;
    
    @FXML
    private TableView<Assistant> tvAssistants;
    @FXML
    private TableColumn<Assistant, String> tcAssistantName;
    @FXML
    private TableColumn<Assistant, Integer> tcAssistantAssistanceHours;
    @FXML
    private TableColumn<Assistant, Integer> tcAssistantLanguages;
    @FXML
    private TableColumn<Assistant, String> tcAssistantEmail;

    private ObservableList<Pilot> pilotsObservableList;
    private ObservableList<Assistant> assistantsObservableList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pilotsObservableList = FXCollections.observableArrayList();
        assistantsObservableList = FXCollections.observableArrayList();
        
        tvPilots.setItems(pilotsObservableList);
        tvAssistants.setItems(assistantsObservableList);
        
        configurePilotTable();
        configureAssistantTable();
    }    

    public void initData(Flight flight) {
        if (flight != null) {
            if (flight.getPilots() != null) {
                pilotsObservableList.setAll(flight.getPilots());
            }
            if (flight.getAssistants() != null) {
                assistantsObservableList.setAll(flight.getAssistants());
            }
        }
    }

    private void configurePilotTable() {
        tcPilotName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcPilotLicense.setCellValueFactory(new PropertyValueFactory<>("licenseType"));
        tcPilotYearsExperience.setCellValueFactory(new PropertyValueFactory<>("yearsExperience"));
        tcPilotFlightHours.setCellValueFactory(new PropertyValueFactory<>("flightHours"));
        tcPilotEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    
    private void configureAssistantTable() {
        tcAssistantName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAssistantAssistanceHours.setCellValueFactory(new PropertyValueFactory<>("assistanceHours"));
        tcAssistantLanguages.setCellValueFactory(new PropertyValueFactory<>("numberOfLanguages"));
        tcAssistantEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

}