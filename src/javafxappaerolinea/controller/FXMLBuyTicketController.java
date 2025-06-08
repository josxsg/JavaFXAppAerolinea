package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.model.dao.CustomerDAO;
import javafxappaerolinea.model.dao.TicketDAO;
import javafxappaerolinea.model.pojo.Customer;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.model.pojo.Ticket;
import javafxappaerolinea.utility.DialogUtil;

public class FXMLBuyTicketController implements Initializable {
    
    @FXML
    private Label labelFlightID;
    @FXML
    private Label labelOrigin;
    @FXML
    private Label labelDestination;
    @FXML
    private Label labelDepartureDate;
    @FXML
    private Label labelPrice;
    @FXML
    private ComboBox<Customer> comboBoxCustomer;
    @FXML
    private DatePicker datePickerPurchaseDate;
    @FXML
    private TextField textFieldSeatNumber;
    @FXML
    private Button btnSelectSeat;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;
    
    private Flight flight;
    private CustomerDAO customerDAO;
    private TicketDAO ticketDAO;
    private FXMLTicketsController ticketsController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerDAO = new CustomerDAO();
        ticketDAO = new TicketDAO();
        
        // Configurar eventos de botones
        btnSelectSeat.setOnAction(this::handleSelectSeat);
        btnConfirm.setOnAction(this::handleConfirm);
        btnCancel.setOnAction(this::handleCancel);
        
        // Configurar DatePicker para no permitir fechas futuras
        datePickerPurchaseDate.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
        
        // Establecer fecha actual por defecto
        datePickerPurchaseDate.setValue(LocalDate.now());
        
        loadCustomers();
    }
    
    public void setFlight(Flight flight) {
        this.flight = flight;
        updateFlightInfo();
    }
    
    public void setTicketsController(FXMLTicketsController controller) {
        this.ticketsController = controller;
    }
    
    private void updateFlightInfo() {
        if (flight != null) {
            labelFlightID.setText(flight.getId());
            labelOrigin.setText(flight.getOriginCity());
            labelDestination.setText(flight.getDestinationCity());
            labelDepartureDate.setText(flight.getDepartureDate().toString());
            labelPrice.setText(String.format("$%.2f", flight.getTicketCost()));
        }
    }
    
    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.findAll();
            ObservableList<Customer> customerList = FXCollections.observableArrayList(customers);
            comboBoxCustomer.setItems(customerList);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", 
                "No se pudieron cargar los clientes: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSelectSeat(ActionEvent event) {
        if (flight == null) {
            DialogUtil.showWarningAlert("Error", 
                "No se ha seleccionado un vuelo.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLSeatSelector.fxml"));
            Parent root = loader.load();
            
            FXMLSeatSelectorController controller = loader.getController();
            controller.setFlight(flight);
            
            Scene scene = new Scene(root);
            // Si tienes el archivo CSS, descomenta la siguiente línea
            // scene.getStylesheets().add(getClass().getResource("/javafxappaerolinea/styles/seats.css").toExternalForm());
            
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Seleccionar Asiento");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Obtener el asiento seleccionado
            String selectedSeat = (String) stage.getUserData();
            if (selectedSeat != null) {
                textFieldSeatNumber.setText(selectedSeat);
            }
            
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", 
                "No se pudo abrir el selector de asientos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleConfirm(ActionEvent event) {
        // Validar campos
        if (!validateFields()) {
            return;
        }
        
        Customer selectedCustomer = comboBoxCustomer.getValue();
        LocalDate purchaseDate = datePickerPurchaseDate.getValue();
        String seatNumber = textFieldSeatNumber.getText().trim();
        
        // Verificar que el asiento no esté ocupado
        try {
            List<Ticket> existingTickets = ticketDAO.findByFlight(flight.getId());
            boolean seatOccupied = existingTickets.stream()
                .anyMatch(t -> t.getSeatNumber().equals(seatNumber));
            
            if (seatOccupied) {
                DialogUtil.showWarningAlert("Asiento ocupado", 
                    "El asiento " + seatNumber + " ya está ocupado. Por favor seleccione otro.");
                return;
            }
            
            // Crear nuevo ticket
            Ticket newTicket = new Ticket();
            newTicket.setId(generateTicketId());
            newTicket.setFlight(flight);
            newTicket.setCustomer(selectedCustomer);
            newTicket.setPurchaseDate(Date.from(purchaseDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            newTicket.setSeatNumber(seatNumber);
            
            // Guardar ticket
            ticketDAO.save(newTicket);
            
            // Actualizar contador de pasajeros del vuelo
            flight.setPassengerCount(flight.getPassengerCount() + 1);
            
            DialogUtil.showInfoAlert("Compra exitosa", 
                "El boleto ha sido comprado exitosamente.\n" +
                "Asiento: " + seatNumber + "\n" +
                "Cliente: " + selectedCustomer.getName());
            
            // Actualizar la tabla de vuelos
            if (ticketsController != null) {
                ticketsController.refreshFlights();
            }
            
            // Cerrar ventana
            closeWindow();
            
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", 
                "No se pudo completar la compra: " + e.getMessage());
        } catch (DuplicateResourceException ex) {
            Logger.getLogger(FXMLBuyTicketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }
    
    private boolean validateFields() {
        if (comboBoxCustomer.getValue() == null) {
            DialogUtil.showWarningAlert("Campos incompletos", 
                "Debe seleccionar un cliente.");
            return false;
        }
        
        if (datePickerPurchaseDate.getValue() == null) {
            DialogUtil.showWarningAlert("Campos incompletos", 
                "Debe seleccionar una fecha de compra.");
            return false;
        }
        
        if (textFieldSeatNumber.getText().trim().isEmpty()) {
            DialogUtil.showWarningAlert("Campos incompletos", 
                "Debe seleccionar un asiento.");
            return false;
        }
        
        return true;
    }
    
    private String generateTicketId() {
        return "TKT" + System.currentTimeMillis();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}