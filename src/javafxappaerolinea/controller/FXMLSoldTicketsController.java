package javafxappaerolinea.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafxappaerolinea.model.dao.TicketDAO;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.model.pojo.Ticket;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.ExportUtil;

public class FXMLSoldTicketsController implements Initializable {

    @FXML
    private Label labelFlightId;
    @FXML
    private Label labelFlightRoute;
    @FXML
    private Label labelFlightDate;
    @FXML
    private Label labelTicketCount;
    @FXML
    private TableView<Ticket> tableTickets;
    @FXML
    private TableColumn<Ticket, String> columnTicketId;
    @FXML
    private TableColumn<Ticket, String> columnCustomerName;
    @FXML
    private TableColumn<Ticket, String> columnCustomerEmail;
    @FXML
    private TableColumn<Ticket, String> columnSeatNumber;
    @FXML
    private TableColumn<Ticket, Date> columnPurchaseDate;
    @FXML
    private Button btnExportTickets;
    @FXML
    private Button btnClose;
    
    private Flight flight;
    private TicketDAO ticketDAO;
    private ObservableList<Ticket> tickets;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ticketDAO = new TicketDAO();
        configureTableColumns();
    }
    
    public void setFlight(Flight flight) {
        this.flight = flight;
        updateFlightInfo();
        loadTickets();
    }
    
    private void updateFlightInfo() {
        if (flight != null) {
            labelFlightId.setText(flight.getId());
            labelFlightRoute.setText(flight.getOriginCity() + " - " + flight.getDestinationCity());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            labelFlightDate.setText(dateFormat.format(flight.getDepartureDate()) + " " + flight.getDepartureHour());
        }
    }
    
    private void configureTableColumns() {
        columnTicketId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnSeatNumber.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        columnPurchaseDate.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        
        columnCustomerName.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            if (ticket != null && ticket.getCustomer() != null) {
                return new SimpleStringProperty(ticket.getCustomer().getName());
            } else {
                return new SimpleStringProperty("Sin cliente");
            }
        });
        
        columnCustomerEmail.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            if (ticket != null && ticket.getCustomer() != null) {
                return new SimpleStringProperty(ticket.getCustomer().getEmail());
            } else {
                return new SimpleStringProperty("");
            }
        });
        
        columnPurchaseDate.setCellFactory(column -> new javafx.scene.control.TableCell<Ticket, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });
    }
    
    private void loadTickets() {
        try {
            List<Ticket> ticketList = ticketDAO.findByFlight(flight.getId());
            tickets = FXCollections.observableArrayList(ticketList);
            tableTickets.setItems(tickets);
            
            labelTicketCount.setText(String.valueOf(tickets.size()));
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error al cargar boletos", 
                "No se pudieron cargar los boletos: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExportTickets(ActionEvent event) {
        if (tickets == null || tickets.isEmpty()) {
            DialogUtil.showWarningAlert("Sin datos", 
                "No hay boletos para exportar.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Boletos");
        
        FileChooser.ExtensionFilter pdfFilter = 
            new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter xlsxFilter = 
            new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx");
        
        fileChooser.getExtensionFilters().addAll(pdfFilter, xlsxFilter);
        fileChooser.setSelectedExtensionFilter(pdfFilter);
        
        File file = fileChooser.showSaveDialog(btnExportTickets.getScene().getWindow());
        
        if (file != null) {
            try {
                String filePath = file.getAbsolutePath();
                String extension = getFileExtension(filePath).toLowerCase();
                
                String title = "Boletos Vendidos - Vuelo " + flight.getId();
                
                switch (extension) {
                    case "pdf":
                        ExportUtil.exportToPDF(new ArrayList<>(tickets), filePath, title);
                        break;
                    case "xlsx":
                        ExportUtil.exportToXLSX(new ArrayList<>(tickets), filePath, "Boletos");
                        break;
                    default:
                        if (!filePath.endsWith(".pdf")) {
                            filePath += ".pdf";
                        }
                        ExportUtil.exportToPDF(new ArrayList<>(tickets), filePath, title);
                        break;
                }
                
                DialogUtil.showInfoAlert("Exportación exitosa", 
                    "Los boletos se han exportado correctamente a " + filePath);
            } catch (IOException e) {
                DialogUtil.showErrorAlert("Error al exportar", 
                    "No se pudieron exportar los boletos: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
    
    private String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return filePath.substring(lastDotIndex + 1);
        }
        return "";
    }
}