package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafxappaerolinea.model.dao.TicketDAO;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.model.pojo.Ticket;
import javafxappaerolinea.utility.DialogUtil;

public class FXMLSeatSelectorController implements Initializable {
    
    @FXML
    private Label labelFlightInfo;
    @FXML
    private Label labelAvailableSeats;
    @FXML
    private Label labelSelectedSeat;
    @FXML
    private GridPane seatsGrid;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;
    
    private Flight flight;
    private String selectedSeat;
    private ToggleGroup seatToggleGroup;
    private TicketDAO ticketDAO;
    private int availableSeatsCount = 0;
    private static final int DEFAULT_CAPACITY = 44; // Capacidad por defecto
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ticketDAO = new TicketDAO();
        seatToggleGroup = new ToggleGroup();
        
        btnConfirm.setOnAction(this::handleConfirm);
        btnCancel.setOnAction(this::handleCancel);
        
        // Deshabilitar botón confirmar hasta que se seleccione un asiento
        btnConfirm.setDisable(true);
    }
    
    public void setFlight(Flight flight) {
        this.flight = flight;
        updateFlightInfo();
        loadSeats();
    }
    
    private void updateFlightInfo() {
        if (flight != null) {
            labelFlightInfo.setText(String.format("Vuelo: %s - %s a %s", 
                flight.getId(), 
                flight.getOriginCity(), 
                flight.getDestinationCity()));
        }
    }
    
    private void loadSeats() {
        seatsGrid.getChildren().clear();
        availableSeatsCount = 0;
        
        try {
            // Obtener boletos vendidos para este vuelo
            List<Ticket> soldTickets = ticketDAO.findByFlight(flight.getId());
            
            // Determinar capacidad del avión
            int capacity = (flight.getAirplane() != null) ? 
                flight.getAirplane().getCapacity() : DEFAULT_CAPACITY;
            
            // Configuración del avión (ejemplo para avión de 44 asientos)
            int rows = 11;
            String[] columns = {"A", "B", "", "C", "D"}; // Pasillo en el medio
            
            // Agregar etiquetas de columnas
            for (int col = 0; col < columns.length; col++) {
                if (!columns[col].isEmpty()) {
                    Label colLabel = new Label(columns[col]);
                    colLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    colLabel.setAlignment(Pos.CENTER);
                    seatsGrid.add(colLabel, col, 0);
                }
            }
            
            // Crear asientos
            for (int row = 1; row <= rows; row++) {
                // Etiqueta de fila
                Label rowLabel = new Label(String.valueOf(row));
                rowLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                rowLabel.setAlignment(Pos.CENTER);
                seatsGrid.add(rowLabel, columns.length, row);
                
                for (int col = 0; col < columns.length; col++) {
                    if (columns[col].isEmpty()) {
                        // Espacio para el pasillo
                        Label aisle = new Label("");
                        aisle.setPrefWidth(30);
                        seatsGrid.add(aisle, col, row);
                    } else {
                        String seatNumber = String.format("%02d%s", row, columns[col]);
                        boolean isOccupied = soldTickets.stream()
                            .anyMatch(t -> t.getSeatNumber().equals(seatNumber));
                        
                        ToggleButton seatButton = createSeatButton(seatNumber, isOccupied);
                        seatsGrid.add(seatButton, col, row);
                        
                        if (!isOccupied) {
                            availableSeatsCount++;
                        }
                    }
                }
            }
            
            labelAvailableSeats.setText("Asientos disponibles: " + availableSeatsCount);
            
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", 
                "No se pudieron cargar los asientos: " + e.getMessage());
        }
    }
    
    private ToggleButton createSeatButton(String seatNumber, boolean isOccupied) {
        ToggleButton button = new ToggleButton(seatNumber);
        button.setToggleGroup(seatToggleGroup);
        button.setPrefSize(50, 40);
        button.getStyleClass().add("seat-button");
        
        if (isOccupied) {
            button.setDisable(true);
            button.getStyleClass().add("seat-occupied");
            button.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-opacity: 0.7;");
        } else {
            button.getStyleClass().add("seat-available");
            button.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
            
            button.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    selectedSeat = seatNumber;
                    labelSelectedSeat.setText("Asiento seleccionado: " + seatNumber);
                    btnConfirm.setDisable(false);
                    button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                } else {
                    button.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                }
            });
        }
        
        return button;
    }
    
    private void handleConfirm(ActionEvent event) {
        if (selectedSeat != null) {
            // Cerrar ventana y devolver el asiento seleccionado
            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.setUserData(selectedSeat);
            stage.close();
        }
    }
    
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
    
    public String getSelectedSeat() {
        return selectedSeat;
    }
}