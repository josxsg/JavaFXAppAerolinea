package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.EmployeeDAO; 
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Pilot; 
import javafxappaerolinea.utility.DialogUtil; 

public class FXMLAddPilotController implements Initializable {

    @FXML
    private TableView<Pilot> tvAvailablePilots;
    @FXML
    private TableColumn<Pilot, String> tcAvailableName;
    @FXML
    private TableColumn<Pilot, String> tcAvailableLicense;
    @FXML
    private TableColumn<Pilot, Integer> tcAvailableAge;
    @FXML
    private TableColumn<Pilot, String> tcAvailableExperience;
    @FXML
    private TableColumn<Pilot, String> tcAvailableFlightTypes; 

    @FXML
    private TableView<Pilot> tvAddedPilots;
    @FXML
    private TableColumn<Pilot, String> tcAddedName;
    @FXML
    private TableColumn<Pilot, String> tcAddedLicense;
    @FXML
    private TableColumn<Pilot, Integer> tcAddedAge;
    @FXML
    private TableColumn<Pilot, String> tcAddedExperience;
    @FXML
    private TableColumn<Pilot, String> tcAddedFlightTypes; 

    private ObservableList<Pilot> availablePilotsData;
    private ObservableList<Pilot> addedPilotsData;
    
    private EmployeeDAO employeeDAO;

    private List<Pilot> finalSelectedPilots;
    private Airline selectedAirline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        employeeDAO = new EmployeeDAO();
        availablePilotsData = FXCollections.observableArrayList();
        addedPilotsData = FXCollections.observableArrayList();
        finalSelectedPilots = new ArrayList<>();

        configureTableColumns();
        loadAvailablePilots();
    }    

   
    public void initData(List<Pilot> currentFlightPilots, Airline airline) {
        this.selectedAirline = airline;

        if (airline != null) {
            loadAvailablePilots();
        }

        if (currentFlightPilots != null && !currentFlightPilots.isEmpty()) {
            List<Pilot> toRemoveFromAvailable = new ArrayList<>();
            for (Pilot currentPilot : currentFlightPilots) {
                Pilot foundPilot = availablePilotsData.stream()
                    .filter(p -> p.getId().equals(currentPilot.getId()))
                    .findFirst()
                    .orElse(null);
                if (foundPilot != null) {
                    addedPilotsData.add(foundPilot);
                    toRemoveFromAvailable.add(foundPilot);
                } else {
                    addedPilotsData.add(currentPilot);
                }
            }
            availablePilotsData.removeAll(toRemoveFromAvailable);
            finalSelectedPilots.addAll(addedPilotsData);
        }
    }

    public List<Pilot> getFinalSelectedPilots() {
        return finalSelectedPilots;
    }

    private void configureTableColumns() {
        tcAvailableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAvailableLicense.setCellValueFactory(new PropertyValueFactory<>("licenseType")); 
        tcAvailableAge.setCellValueFactory(new PropertyValueFactory<>("age")); 
        tcAvailableExperience.setCellValueFactory(new PropertyValueFactory<>("yearsExperience")); 
        tcAvailableFlightTypes.setCellValueFactory(new PropertyValueFactory<>("licenseType"));

        tvAvailablePilots.setItems(availablePilotsData);

        tcAddedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAddedLicense.setCellValueFactory(new PropertyValueFactory<>("licenseType")); 
        tcAddedAge.setCellValueFactory(new PropertyValueFactory<>("age")); 
        tcAddedExperience.setCellValueFactory(new PropertyValueFactory<>("yearsExperience")); 
        tcAddedFlightTypes.setCellValueFactory(new PropertyValueFactory<>("licenseType")); 
        
        tvAddedPilots.setItems(addedPilotsData);
    }

    private void loadAvailablePilots() {
        try {
            List<Pilot> allPilots = employeeDAO.findAllPilots();

            if (selectedAirline != null) {
                allPilots = allPilots.stream()
                    .filter(pilot -> pilot.getAirline() != null && 
                        pilot.getAirline().getIdentificationNumber() == selectedAirline.getIdentificationNumber())
                    .collect(Collectors.toList());
            }

            availablePilotsData.setAll(allPilots);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error de carga", "No se pudieron cargar los pilotos disponibles: " + e.getMessage());
        }
    }

    @FXML
    private void btnAdd(ActionEvent event) {
        Pilot selectedPilot = tvAvailablePilots.getSelectionModel().getSelectedItem();
        if (selectedPilot != null) {
            if (addedPilotsData.size() < 2) { 
                availablePilotsData.remove(selectedPilot);
                addedPilotsData.add(selectedPilot);
            } else {
                DialogUtil.showWarningAlert("Límite de Pilotos", "Ya se han agregado los 2 pilotos requeridos.");
            }
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, selecciona un piloto de la tabla de pilotos disponibles.");
        }
    }

    @FXML
    private void btnRemove(ActionEvent event) {
        Pilot selectedPilot = tvAddedPilots.getSelectionModel().getSelectedItem();
        if (selectedPilot != null) {
            addedPilotsData.remove(selectedPilot);
            availablePilotsData.add(selectedPilot);
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, selecciona un piloto de la tabla de pilotos añadidos.");
        }
    }

    @FXML
    private void btnSaveChanges(ActionEvent event) {
        if (addedPilotsData.size() != 2) {
            DialogUtil.showWarningAlert("Pilotos Requeridos", 
                "Debe seleccionar exactamente 2 pilotos para el vuelo. Actualmente tiene: " + addedPilotsData.size());
            return;
        }

        finalSelectedPilots.clear();
        finalSelectedPilots.addAll(addedPilotsData);
        
        DialogUtil.showInfoAlert("Cambios guardados", "Los 2 pilotos requeridos han sido registrados correctamente.");
        closeWindow();
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tvAvailablePilots.getScene().getWindow();
        stage.close();
    }
}