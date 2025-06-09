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
    private static final int DEFAULT_CAPACITY = 44;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ticketDAO = new TicketDAO();
        seatToggleGroup = new ToggleGroup();
        
        btnConfirm.setOnAction(this::handleConfirm);
        btnCancel.setOnAction(this::handleCancel);
        
        btnConfirm.setDisable(true);
    }
    
    public void setFlight(Flight flight) {
        this.flight = flight;
        updateFlightInfo();
        loadSeats();
    }
    
    private void updateFlightInfo() {
        if (flight != null) {
            String airlineName = (flight.getAirline() != null) ? 
                flight.getAirline().getName() : "Sin aerolínea";
            labelFlightInfo.setText(String.format("Vuelo: %s (%s) - %s a %s", 
                flight.getId(),
                airlineName,
                flight.getOriginCity(), 
                flight.getDestinationCity()));
        }
    }
    
    private void loadSeats() {
        seatsGrid.getChildren().clear();
        availableSeatsCount = 0;
        
        try {
            List<Ticket> soldTickets = ticketDAO.findByFlight(flight.getId());
            
            int capacity = (flight.getPassengerCount() > 0) ?
                    flight.getPassengerCount() : DEFAULT_CAPACITY;
            
            SeatConfiguration config = getSeatConfiguration(capacity);
            
            for (int col = 0; col < config.columns.length; col++) {
                if (!config.columns[col].isEmpty()) {
                    Label colLabel = new Label(config.columns[col]);
                    colLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    colLabel.setAlignment(Pos.CENTER);
                    colLabel.setPrefWidth(50);
                    seatsGrid.add(colLabel, col, 0);
                }
            }
            
            int seatCount = 0;
            for (int row = 1; row <= config.rows && seatCount < capacity; row++) {
                Label rowLabel = new Label(String.valueOf(row));
                rowLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                rowLabel.setAlignment(Pos.CENTER);
                rowLabel.setPrefWidth(30);
                seatsGrid.add(rowLabel, config.columns.length, row);
                
                for (int col = 0; col < config.columns.length && seatCount < capacity; col++) {
                    if (config.columns[col].isEmpty()) {
                        Label aisle = new Label("");
                        aisle.setPrefWidth(30);
                        seatsGrid.add(aisle, col, row);
                    } else {
                        String seatNumber = String.format("%02d%s", row, config.columns[col]);
                        boolean isOccupied = soldTickets.stream()
                            .anyMatch(t -> t.getSeatNumber().equals(seatNumber));
                        
                        ToggleButton seatButton = createSeatButton(seatNumber, isOccupied);
                        seatsGrid.add(seatButton, col, row);
                        
                        if (!isOccupied) {
                            availableSeatsCount++;
                        }
                        seatCount++;
                    }
                }
            }
            
            labelAvailableSeats.setText(String.format("Asientos disponibles: %d de %d", 
                availableSeatsCount, capacity));
            
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", 
                "No se pudieron cargar los asientos: " + e.getMessage());
        }
    }
    
    private SeatConfiguration getSeatConfiguration(int capacity) {
        if (capacity <= 20) {
            return new SeatConfiguration(
                new String[]{"A", "B", "", "C", "D"},
                (int) Math.ceil(capacity / 4.0)
            );
        } else if (capacity <= 50) {
            return new SeatConfiguration(
                new String[]{"A", "B", "C", "", "D", "E", "F"},
                (int) Math.ceil(capacity / 6.0)
            );
        } else if (capacity <= 100) {
            return new SeatConfiguration(
                new String[]{"A", "B", "C", "", "D", "E", "F"},
                (int) Math.ceil(capacity / 6.0)
            );
        } else if (capacity <= 200) {
            return new SeatConfiguration(
                new String[]{"A", "B", "C", "", "D", "E", "F", "G", "", "H", "J", "K"},
                (int) Math.ceil(capacity / 10.0)
            );
        } else {
            return new SeatConfiguration(
                new String[]{"A", "B", "C", "", "D", "E", "F", "G", "", "H", "J", "K"},
                (int) Math.ceil(capacity / 10.0)
            );
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
    
    private static class SeatConfiguration {
        String[] columns;
        int rows;
        
        SeatConfiguration(String[] columns, int rows) {
            this.columns = columns;
            this.rows = rows;
        }
    }
}