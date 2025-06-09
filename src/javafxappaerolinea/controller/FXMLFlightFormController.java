package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafxappaerolinea.JavaFXAppAerolinea;
import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.AirlineDAO;
import javafxappaerolinea.model.dao.AirplaneDAO;
import javafxappaerolinea.model.dao.FlightDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Airplane;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Flight;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.IdGeneratorUtil;
import javafxappaerolinea.utility.ValidationUtil;

public class FXMLFlightFormController implements Initializable {

    @FXML private TextField tfOriginCity;
    @FXML private TextField tfDestinationCity;
    @FXML private DatePicker dpDepartureDate;
    @FXML private TextField tfDepartureHour;
    @FXML private DatePicker dpArrivalDate;
    @FXML private TextField tfArrivalHour;
    @FXML private TextField tfTicketCost;
    @FXML private TextField tfGate;
    @FXML private TextField tfPassengerCount;
    @FXML private ComboBox<Airline> cbAirline;
    @FXML private ComboBox<Airplane> cbAirplane;
    @FXML private Label lbTitle;
    @FXML private Label lbOriginCityError;
    @FXML private Label lbDestinationCityError;
    @FXML private Label lbDepartureDateError;
    @FXML private Label lbDepartureHourError;
    @FXML private Label lbArrivalDateError;
    @FXML private Label lbArrivalHourError;
    @FXML private Label lbTicketCostError;
    @FXML private Label lbGateError;
    @FXML private Label lbPassengerCountError;
    @FXML private Label lbAirlineError;
    @FXML private Label lbAirplaneError;

    private FlightDAO flightDAO;
    private AirlineDAO airlineDAO;
    private AirplaneDAO airplaneDAO;

    private Flight flight;
    private boolean isEditMode;
    
    private List<Pilot> selectedPilots = new ArrayList<>();
    private List<Assistant> selectedAssistants = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        flightDAO = new FlightDAO();
        airlineDAO = new AirlineDAO();
        airplaneDAO = new AirplaneDAO();
        
        configureComboBoxes();
        applyFormatters();
        loadAirlines();
    }    

    public void initData(Flight flight) {
        if (flight != null) {
            this.isEditMode = true;
            this.flight = flight;
            lbTitle.setText("Editar Vuelo");
            loadFlightData();
        } else {
            this.isEditMode = false;
            this.flight = new Flight();
            lbTitle.setText("Registrar Nuevo Vuelo");
        }
    }
    
    @FXML
    private void btnSave(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        try {
            populateFlightObject();
            
            if (isEditMode) {
                flightDAO.update(flight);
                DialogUtil.showInfoAlert("Éxito", "Vuelo actualizado correctamente.");
            } else {
                flight.setId(IdGeneratorUtil.generateVueloId(flight.getAirline().getIdentificationNumber(), flight.getOriginCity(), flight.getDestinationCity()));
                flightDAO.save(flight);
                DialogUtil.showInfoAlert("Éxito", "Vuelo registrado correctamente.");
            }
            closeWindow();
        } catch (IOException | DuplicateResourceException | ResourceNotFoundException e) {
            DialogUtil.showErrorAlert("Error al guardar", "Ocurrió un error al guardar el vuelo: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAirlineSelection(ActionEvent event) {
        Airline selectedAirline = cbAirline.getValue();
        cbAirplane.getItems().clear();
        cbAirplane.setDisable(true);
        if (selectedAirline != null) {
            try {
                List<Airplane> airplanes = airplaneDAO.findAll().stream()
                    .filter(p -> p.getAirline().getIdentificationNumber() == selectedAirline.getIdentificationNumber() && p.isStatus())
                    .collect(Collectors.toList());
                cbAirplane.setItems(FXCollections.observableArrayList(airplanes));
                cbAirplane.setDisable(false);
            } catch (IOException e) {
                DialogUtil.showErrorAlert("Error", "No se pudieron cargar los aviones de la aerolínea.");
            }
        }
    }

    private void populateFlightObject() {
        flight.setOriginCity(tfOriginCity.getText().trim());
        flight.setDestinationCity(tfDestinationCity.getText().trim());
        flight.setTicketCost(Double.parseDouble(tfTicketCost.getText()));
        flight.setGate(tfGate.getText().trim());
        flight.setAirline(cbAirline.getValue());
        flight.setAirplane(cbAirplane.getValue());
        
        if (!tfPassengerCount.getText().trim().isEmpty()) {
            flight.setPassengerCount(Integer.parseInt(tfPassengerCount.getText().trim()));
        }

        LocalDateTime departureDateTime = LocalDateTime.of(dpDepartureDate.getValue(), java.time.LocalTime.parse(tfDepartureHour.getText()));
        LocalDateTime arrivalDateTime = LocalDateTime.of(dpArrivalDate.getValue(), java.time.LocalTime.parse(tfArrivalHour.getText()));
        
        flight.setDepartureDate(Date.from(departureDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        flight.setArrivalDate(Date.from(arrivalDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        
        flight.setDepartureHour(tfDepartureHour.getText());
        flight.setArrivalHour(arrivalDateTime.getHour()); 

        flight.setTravelTime(calculateTravelTime(departureDateTime, arrivalDateTime));
        
        flight.setPilots(this.selectedPilots);
        flight.setAssistants(this.selectedAssistants);
    }
    
    private void loadFlightData() {
        tfOriginCity.setText(flight.getOriginCity());
        tfDestinationCity.setText(flight.getDestinationCity());
        tfTicketCost.setText(String.valueOf(flight.getTicketCost()));
        tfGate.setText(flight.getGate());
        tfPassengerCount.setText(String.valueOf(flight.getPassengerCount()));

        cbAirline.setValue(flight.getAirline());
        handleAirlineSelection(null); 
        cbAirplane.setValue(flight.getAirplane());
        
        dpDepartureDate.setValue(flight.getDepartureDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        dpArrivalDate.setValue(flight.getArrivalDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        tfDepartureHour.setText(flight.getDepartureHour());
        tfArrivalHour.setText(String.format("%02d:00", flight.getArrivalHour())); 
        
        this.selectedPilots = new ArrayList<>(flight.getPilots());
        this.selectedAssistants = new ArrayList<>(flight.getAssistants());
    }
    
    private boolean validateFields() {
        clearErrors();
        boolean isValid = true;
        
        if (tfOriginCity.getText().trim().isEmpty()) {
            lbOriginCityError.setText("Campo requerido"); isValid = false;
        }
        if (tfDestinationCity.getText().trim().isEmpty()) {
            lbDestinationCityError.setText("Campo requerido"); isValid = false;
        }
        if (dpDepartureDate.getValue() == null) {
            lbDepartureDateError.setText("Seleccione una fecha"); isValid = false;
        }
        if (dpArrivalDate.getValue() == null) {
            lbArrivalDateError.setText("Seleccione una fecha"); isValid = false;
        }
        if (!isValidTimeFormat(tfDepartureHour.getText())) {
            lbDepartureHourError.setText("Formato HH:MM requerido"); isValid = false;
        }
        if (!isValidTimeFormat(tfArrivalHour.getText())) {
            lbArrivalHourError.setText("Formato HH:MM requerido"); isValid = false;
        }
        if (ValidationUtil.validatePositiveUI(tfTicketCost.getText()) != null) {
            lbTicketCostError.setText("Debe ser un número positivo"); isValid = false;
        }
        if (tfGate.getText().trim().isEmpty()) {
            lbGateError.setText("Campo requerido"); isValid = false;
        }
        if (cbAirline.getValue() == null) {
            lbAirlineError.setText("Seleccione una aerolínea"); isValid = false;
        }
        if (cbAirplane.getValue() == null) {
            lbAirplaneError.setText("Seleccione un avión"); isValid = false;
        }
        
        // Validación del número de pasajeros
        if (tfPassengerCount.getText().trim().isEmpty()) {
            lbPassengerCountError.setText("Campo requerido"); isValid = false;
        } else {
            try {
                int passengerCount = Integer.parseInt(tfPassengerCount.getText().trim());
                if (passengerCount < 0) {
                    lbPassengerCountError.setText("Debe ser un número positivo"); isValid = false;
                } else if (cbAirplane.getValue() != null) {
                    int airplaneCapacity = cbAirplane.getValue().getCapacity();
                    if (passengerCount > airplaneCapacity) {
                        lbPassengerCountError.setText("Excede la capacidad del avión (" + airplaneCapacity + ")"); isValid = false;
                    }
                }
            } catch (NumberFormatException e) {
                lbPassengerCountError.setText("Debe ser un número válido"); isValid = false;
            }
        }
        
        if (isValid) {
            try {
                LocalDateTime departure = LocalDateTime.of(dpDepartureDate.getValue(), java.time.LocalTime.parse(tfDepartureHour.getText()));
                LocalDateTime arrival = LocalDateTime.of(dpArrivalDate.getValue(), java.time.LocalTime.parse(tfArrivalHour.getText()));
                if (!arrival.isAfter(departure)) {
                    lbArrivalDateError.setText("La llegada debe ser después de la salida");
                    isValid = false;
                }
            } catch (DateTimeParseException e) {
                lbArrivalDateError.setText("Error al establecer la fecha.");
            }
        }

        if (selectedPilots.size() != 2) {
            DialogUtil.showWarningAlert("Pilotos Requeridos", "Debe añadir exactamente 2 pilotos al vuelo. Actualmente tiene: " + selectedPilots.size());
            isValid = false;
        }
        if (selectedAssistants.size() != 4) {
            DialogUtil.showWarningAlert("Asistentes Requeridos", "Debe añadir exactamente 4 asistentes al vuelo. Actualmente tiene: " + selectedAssistants.size());
            isValid = false;
        }

        return isValid;
    }

    private void loadAirlines() {
        try {
            cbAirline.setItems(FXCollections.observableArrayList(airlineDAO.findAll()));
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "No se pudieron cargar las aerolíneas.");
        }
    }
    
    private void configureComboBoxes() {
        cbAirline.setConverter(new StringConverter<Airline>() {
            @Override public String toString(Airline airline) { return airline == null ? "" : airline.getName(); }
            @Override public Airline fromString(String string) { return null; }
        });

        cbAirplane.setConverter(new StringConverter<Airplane>() {
            @Override public String toString(Airplane airplane) { 
                return airplane == null ? "" : airplane.getModel() + " (" + airplane.getRegistration() + ") - Cap: " + airplane.getCapacity(); 
            }
            @Override public Airplane fromString(String string) { return null; }
        });
        
        cbAirplane.setDisable(true);
        
        cbAirplane.setOnAction(event -> {
            Airplane selectedAirplane = cbAirplane.getValue();
            if (selectedAirplane != null) {
                if (!tfPassengerCount.getText().trim().isEmpty()) {
                    try {
                        int passengerCount = Integer.parseInt(tfPassengerCount.getText().trim());
                        if (passengerCount > selectedAirplane.getCapacity()) {
                            lbPassengerCountError.setText("Excede la capacidad del avión (" + selectedAirplane.getCapacity() + ")");
                        } else {
                            lbPassengerCountError.setText("");
                        }
                    } catch (NumberFormatException e) {
                        lbPassengerCountError.setText("");
                    }
                }
            }
        });
    }
    
    private void applyFormatters() {
        tfTicketCost.setTextFormatter(new TextFormatter<>(c -> 
            c.getControlNewText().matches("\\d*\\.?\\d*") ? c : null));

        tfGate.setTextFormatter(new TextFormatter<>(c -> 
            c.getControlNewText().matches("[a-zA-Z0-9\\- ]*") ? c : null));

        TextFormatter<String> timeFormatter1 = new TextFormatter<>(c -> 
            c.getControlNewText().matches("[0-9:]*") ? c : null);
        tfDepartureHour.setTextFormatter(timeFormatter1);

        TextFormatter<String> timeFormatter2 = new TextFormatter<>(c -> 
            c.getControlNewText().matches("[0-9:]*") ? c : null);
        tfArrivalHour.setTextFormatter(timeFormatter2);
        
        tfPassengerCount.setTextFormatter(new TextFormatter<>(c -> 
            c.getControlNewText().matches("\\d*") ? c : null));
    }
    
    private boolean isValidTimeFormat(String time) {
        return time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    }
    
    private double calculateTravelTime(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toMinutes() / 60.0;
    }
    
    private void clearErrors() {
        lbOriginCityError.setText("");
        lbDestinationCityError.setText("");
        lbDepartureDateError.setText("");
        lbDepartureHourError.setText("");
        lbArrivalDateError.setText("");
        lbArrivalHourError.setText("");
        lbTicketCostError.setText("");
        lbGateError.setText("");
        lbPassengerCountError.setText("");
        lbAirlineError.setText("");
        lbAirplaneError.setText("");
    }
    
    @FXML private void btnCancel(ActionEvent event) { closeWindow(); }
    private void closeWindow() { ((Stage) lbTitle.getScene().getWindow()).close(); }

    
    @FXML
    private void btnAddPilots(ActionEvent event) {
        openCrewManagementWindow("view/FXMLAddPilot.fxml", "Gestionar Pilotos");
    }

    @FXML
    private void btnAddAssistants(ActionEvent event) {
        openCrewManagementWindow("view/FXMLAddAssistant.fxml", "Gestionar Asistentes");
    }
    
    private void openCrewManagementWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            Airline selectedAirline = cbAirline.getValue();
            
            if ("view/FXMLAddPilot.fxml".equals(fxmlPath)) {
                FXMLAddPilotController controller = loader.getController();
                controller.initData(this.selectedPilots, selectedAirline); 
                stage.showAndWait();
                this.selectedPilots = controller.getFinalSelectedPilots(); 
            } else if ("view/FXMLAddAssistant.fxml".equals(fxmlPath)) { 
                FXMLAddAssistantController controller = loader.getController();
                controller.initData(this.selectedAssistants, selectedAirline);
                stage.showAndWait();

                if (controller.isAssistantsConfirmed()) {
                    this.selectedAssistants = controller.getSelectedAssistants();
                }
            } else {
                stage.showAndWait();
            }

        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "No se pudo abrir la ventana de gestión de tripulación: " + e.getMessage());
        }
    }
}