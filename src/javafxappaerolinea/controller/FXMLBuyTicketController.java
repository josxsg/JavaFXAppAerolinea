package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.CustomerDAO;
import javafxappaerolinea.model.dao.FlightDAO;
import javafxappaerolinea.model.dao.TicketDAO;
import javafxappaerolinea.model.pojo.Customer;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.model.pojo.Ticket;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.ValidationUtil;

/**
 * Controlador para la vista de compra de boletos
 * @author zenbook i5
 */
public class FXMLBuyTicketController implements Initializable {

    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;
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
    
    private Flight flight;
    private ObservableList<Customer> customers;
    private FXMLTicketsController ticketsController;
    private CustomerDAO customerDAO;
    private TicketDAO ticketDAO;
    private FlightDAO flightDAO;

    /**
     * Inicializa el controlador
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerDAO = new CustomerDAO();
        ticketDAO = new TicketDAO();
        flightDAO = new FlightDAO();
        
        // Establecer fecha actual
        datePickerPurchaseDate.setValue(LocalDate.now());
        
        // Cargar clientes
        loadCustomers();
        
        // Configurar eventos
        btnConfirm.setOnAction(this::handleConfirm);
        btnCancel.setOnAction(this::handleCancel);
    }    
    
    /**
     * Establece el vuelo seleccionado
     */
    public void setFlight(Flight flight) {
        this.flight = flight;
        updateFlightInfo();
    }
    
    /**
     * Establece el controlador de boletos para actualizar la tabla después de comprar
     */
    public void setTicketsController(FXMLTicketsController controller) {
        this.ticketsController = controller;
    }
    
    /**
     * Actualiza la información del vuelo en la interfaz
     */
    private void updateFlightInfo() {
        if (flight != null) {
            labelFlightID.setText(flight.getId());
            labelOrigin.setText(flight.getOriginCity());
            labelDestination.setText(flight.getDestinationCity());
            labelDepartureDate.setText(flight.getDepartureDate().toString());
            labelPrice.setText(String.format("$%.2f", flight.getTicketCost()));
        }
    }
    
    /**
     * Carga los clientes desde la base de datos
     */
    private void loadCustomers() {
        customers = FXCollections.observableArrayList();
        try {
            List<Customer> customersList = customerDAO.findAll();
            customers.addAll(customersList);
            comboBoxCustomer.setItems(customers);
            
            // Configurar cómo se muestran los clientes en el ComboBox
            comboBoxCustomer.setCellFactory(param -> new javafx.scene.control.ListCell<Customer>() {
                @Override
                protected void updateItem(Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " " + item.getEmail());
                    }
                }
            });
            
            comboBoxCustomer.setButtonCell(new javafx.scene.control.ListCell<Customer>() {
                @Override
                protected void updateItem(Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " " + item.getEmail());
                    }
                }
            });
            
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error al cargar clientes", 
                    "No se pudieron cargar los clientes: " + e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de confirmar la compra
     */
    private void handleConfirm(ActionEvent event) {
        if (validateFields()) {
            Customer selectedCustomer = comboBoxCustomer.getSelectionModel().getSelectedItem();
            
            try {
                Ticket ticket = new Ticket();
                ticket.setId(java.util.UUID.randomUUID().toString()); // Generar ID único
                ticket.setFlight(flight);
                ticket.setCustomer(selectedCustomer);
                LocalDate localDate = datePickerPurchaseDate.getValue();
                if (localDate != null) {
                    Date purchaseDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    ticket.setPurchaseDate(purchaseDate);
                }
                ticket.setSeatNumber(textFieldSeatNumber.getText().trim());
                
                ticketDAO.save(ticket);
                
                // Actualizar pasajeros del vuelo
                flight.setPassengerCount(flight.getPassengerCount() + 1);
                flightDAO.update(flight);
                
                DialogUtil.showInfoAlert("Boleto comprado", 
                        "El boleto se ha comprado correctamente para " + 
                                selectedCustomer.getName() + ".\n" +
                                "Número de asiento: " + ticket.getSeatNumber());
                
                if (ticketsController != null) {
                    ticketsController.refreshFlights();
                }
                
                closeWindow();
            } catch (DuplicateResourceException e) {
                DialogUtil.showErrorAlert("Error", 
                        "Ya existe un boleto con el mismo ID.");
            } catch (ResourceNotFoundException e) {
                DialogUtil.showErrorAlert("Error", 
                        "No se encontró el vuelo para actualizar.");
            } catch (IOException e) {
                DialogUtil.showErrorAlert("Error en la base de datos", 
                        "Ocurrió un error al intentar comprar el boleto: " + e.getMessage());
            }
        }
    }
    
    /**
     * Maneja el evento de cancelar
     */
    private void handleCancel(ActionEvent event) {
        boolean confirm = DialogUtil.showConfirmationDialog("Confirmar", 
                "¿Está seguro que desea cancelar la compra?");
        
        if (confirm) {
            closeWindow();
        }
    }
    
    /**
     * Valida los campos del formulario
     */
    private boolean validateFields() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder("Por favor corrija los siguientes errores:\n");
        
        // Validar cliente seleccionado
        if (comboBoxCustomer.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append("- Debe seleccionar un cliente\n");
            isValid = false;
        }
        
        // Validar fecha de compra
        String dateError = ValidationUtil.validateDateNotNullUI(datePickerPurchaseDate.getValue());
        if (dateError != null) {
            errorMessage.append("- La fecha de compra ").append(dateError.toLowerCase()).append("\n");
            isValid = false;
        }
        
        // Validar número de asiento
        String seatError = ValidationUtil.validateNotEmptyUI(textFieldSeatNumber.getText());
        if (seatError != null) {
            errorMessage.append("- El número de asiento ").append(seatError.toLowerCase()).append("\n");
            isValid = false;
        }
        
        if (!isValid) {
            DialogUtil.showWarningAlert("Datos incompletos o inválidos", errorMessage.toString());
        }
        
        return isValid;
    }
    
    /**
     * Cierra la ventana actual
     */
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}