/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxappaerolinea.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.model.pojo.Pilot;

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
    
    @FXML
    private ListView<String> lvPilots;
    
    @FXML
    private ListView<String> lvAssistants;
    
    private Flight flight;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización vacía, los datos se cargarán cuando se establezca el vuelo
    }
    
    public void setFlight(Flight flight) {
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
            
            // Cargar pilotos
            ObservableList<String> pilotsList = FXCollections.observableArrayList();
            if (flight.getPilots() != null && !flight.getPilots().isEmpty()) {
                for (Pilot pilot : flight.getPilots()) {
                    pilotsList.add(pilot.getName() + " - " + pilot.getLicenseType() + 
                            " (" + pilot.getYearsExperience() + " años de experiencia)");
                }
            } else {
                pilotsList.add("No hay pilotos asignados");
            }
            lvPilots.setItems(pilotsList);
            
            // Cargar asistentes
            ObservableList<String> assistantsList = FXCollections.observableArrayList();
            if (flight.getAssistants() != null && !flight.getAssistants().isEmpty()) {
                for (Assistant assistant : flight.getAssistants()) {
                    assistantsList.add(assistant.getName() + " - " + 
                            assistant.getNumberOfLanguages() + " idiomas");
                }
            } else {
                assistantsList.add("No hay asistentes asignados");
            }
            lvAssistants.setItems(assistantsList);
        }
    }
    
    @FXML
    private void btnClose(ActionEvent event) {
        Stage stage = (Stage) lbFlightId.getScene().getWindow();
        stage.close();
    }
}