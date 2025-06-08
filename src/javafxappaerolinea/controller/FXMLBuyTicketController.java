package javafxappaerolinea.controller;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
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
import javafx.stage.FileChooser;
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
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            labelDepartureDate.setText(dateFormat.format(flight.getDepartureDate()) + " " + flight.getDepartureHour());
            
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
            
            // Generar y descargar el PDF del boleto
            generateTicketPDF(newTicket);
            
            DialogUtil.showInfoAlert("Compra exitosa", 
                "El boleto ha sido comprado exitosamente.\n" +
                "Asiento: " + seatNumber + "\n" +
                "Cliente: " + selectedCustomer.getName() + "\n\n" +
                "Se ha descargado el PDF del boleto.");
            
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
    
    private void generateTicketPDF(Ticket ticket) {
        try {
            // Crear diálogo para guardar archivo
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Boleto");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
            
            // Nombre de archivo sugerido
            String suggestedFileName = "Boleto_" + ticket.getId() + ".pdf";
            fileChooser.setInitialFileName(suggestedFileName);
            
            File file = fileChooser.showSaveDialog(btnConfirm.getScene().getWindow());
            
            if (file != null) {
                Document document = new Document(new Rectangle(500, 300));
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                
                // Título
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
                Paragraph title = new Paragraph("BOLETO DE AVIÓN", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                
                // Información del vuelo
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setSpacingBefore(20);
                table.setSpacingAfter(20);
                
                // Estilos
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
                
                // Número de boleto
                addTableRow(table, "Número de Boleto:", ticket.getId(), headerFont, contentFont);
                
                // Información del vuelo
                addTableRow(table, "Vuelo:", ticket.getFlight().getId(), headerFont, contentFont);
                addTableRow(table, "Aerolínea:", ticket.getFlight().getAirline().getName(), headerFont, contentFont);
                addTableRow(table, "Origen:", ticket.getFlight().getOriginCity(), headerFont, contentFont);
                addTableRow(table, "Destino:", ticket.getFlight().getDestinationCity(), headerFont, contentFont);
                
                // Fecha y hora
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String departureDate = dateFormat.format(ticket.getFlight().getDepartureDate());
                String departureTime = ticket.getFlight().getDepartureHour();
                
                addTableRow(table, "Fecha de Salida:", departureDate, headerFont, contentFont);
                addTableRow(table, "Hora de Salida:", departureTime, headerFont, contentFont);
                addTableRow(table, "Puerta:", ticket.getFlight().getGate(), headerFont, contentFont);
                
                // Información del pasajero
                addTableRow(table, "Pasajero:", ticket.getCustomer().getName(), headerFont, contentFont);
                addTableRow(table, "Asiento:", ticket.getSeatNumber(), headerFont, contentFont);
                
                // Fecha de compra
                String purchaseDate = dateFormat.format(ticket.getPurchaseDate());
                addTableRow(table, "Fecha de Compra:", purchaseDate, headerFont, contentFont);
                
                // Precio
                addTableRow(table, "Precio:", String.format("$%.2f", ticket.getFlight().getTicketCost()), headerFont, contentFont);
                
                document.add(table);
                
                // Pie de página
                Paragraph footer = new Paragraph("Gracias por volar con nosotros", contentFont);
                footer.setAlignment(Element.ALIGN_CENTER);
                document.add(footer);
                
                document.close();
            }
        } catch (Exception e) {
            DialogUtil.showErrorAlert("Error al generar PDF", 
                "No se pudo generar el PDF del boleto: " + e.getMessage());
        }
    }
    
    private void addTableRow(PdfPTable table, String header, String content, Font headerFont, Font contentFont) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setPadding(5);
        
        PdfPCell contentCell = new PdfPCell(new Phrase(content, contentFont));
        contentCell.setBorder(Rectangle.NO_BORDER);
        contentCell.setPadding(5);
        
        table.addCell(headerCell);
        table.addCell(contentCell);
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