package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxappaerolinea.model.dao.EmployeeDAO; 
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Assistant; 
import javafxappaerolinea.utility.DialogUtil; 

public class FXMLAddAssistantController implements Initializable {

    @FXML
    private TableView<Assistant> tvAvailableAssistants;
    @FXML
    private TableColumn tcAvailableName;
    @FXML
    private TableColumn<Assistant, Integer> tcAvailableAge;
    @FXML
    private TableColumn tcAvailableAssistanceHours;
    @FXML
    private TableColumn tcAvailableLanguages;

    @FXML
    private TableView<Assistant> tvAddedAssistants;
    @FXML
    private TableColumn tcAddedName;
    @FXML
    private TableColumn<Assistant, Integer> tcAddedAge;
    @FXML
    private TableColumn tcAddedAssistanceHours;
    @FXML
    private TableColumn tcAddedLanguages;

    private ObservableList<Assistant> availableAssistantsData;
    private ObservableList<Assistant> addedAssistantsData;
    
    private EmployeeDAO employeeDAO;
    private boolean assistantsConfirmed = false;
    private Airline selectedAirline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        employeeDAO = new EmployeeDAO();
        availableAssistantsData = FXCollections.observableArrayList();
        addedAssistantsData = FXCollections.observableArrayList();

        configureTableColumns();
        loadAvailableAssistants();
    }    

    public void initData(List<Assistant> currentFlightAssistants, Airline airline) {
        this.selectedAirline = airline;

        // Recargar asistentes filtrados por aerolínea
        if (airline != null) {
            loadAvailableAssistants();
        }

        if (currentFlightAssistants != null && !currentFlightAssistants.isEmpty()) {
            List<Assistant> toRemoveFromAvailable = new ArrayList<>();
            for (Assistant currentAssistant : currentFlightAssistants) {
                Assistant foundAssistant = availableAssistantsData.stream()
                    .filter(a -> a.getId().equals(currentAssistant.getId()))
                    .findFirst()
                    .orElse(null);
                if (foundAssistant != null) {
                    addedAssistantsData.add(foundAssistant);
                    toRemoveFromAvailable.add(foundAssistant);
                } else {
                    addedAssistantsData.add(currentAssistant);
                }
            }
            availableAssistantsData.removeAll(toRemoveFromAvailable);
        }
    }

    public List<Assistant> getSelectedAssistants() {
        return new ArrayList<>(addedAssistantsData);
    }
    
    public boolean isAssistantsConfirmed() {
        return assistantsConfirmed;
    }

    private void configureTableColumns() {
        tcAvailableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAvailableAge.setCellValueFactory(cellData -> {
            Assistant assistant = cellData.getValue();
            if (assistant != null && assistant.getBirthDate() != null) {
                return new SimpleIntegerProperty(assistant.getAge()).asObject();
            }
            return new SimpleIntegerProperty(0).asObject(); 
        });
        tcAvailableAssistanceHours.setCellValueFactory(new PropertyValueFactory<>("assistanceHours"));
        tcAvailableLanguages.setCellValueFactory(new PropertyValueFactory<>("numberOfLanguages"));

        tvAvailableAssistants.setItems(availableAssistantsData);

        tcAddedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAddedAge.setCellValueFactory(cellData -> {
            Assistant assistant = cellData.getValue();
            if (assistant != null && assistant.getBirthDate() != null) {
                return new SimpleIntegerProperty(assistant.getAge()).asObject();
            }
            return new SimpleIntegerProperty(0).asObject();
        });
        tcAddedAssistanceHours.setCellValueFactory(new PropertyValueFactory<>("assistanceHours"));
        tcAddedLanguages.setCellValueFactory(new PropertyValueFactory<>("numberOfLanguages"));
        
        tvAddedAssistants.setItems(addedAssistantsData);
    }

    private void loadAvailableAssistants() {
        try {
            List<Assistant> allAssistants = employeeDAO.findAllAssistants();

            // Filtrar por aerolínea si está seleccionada
            if (selectedAirline != null) {
                allAssistants = allAssistants.stream()
                    .filter(assistant -> assistant.getAirline() != null && 
                            assistant.getAirline().getIdentificationNumber() == selectedAirline.getIdentificationNumber())
                    .collect(Collectors.toList());
            }

            availableAssistantsData.setAll(allAssistants);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error de carga", 
                "No se pudieron cargar los asistentes disponibles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void btnAdd(ActionEvent event) {
        Assistant selectedAssistant = tvAvailableAssistants.getSelectionModel().getSelectedItem();
        if (selectedAssistant != null) {
            if (addedAssistantsData.size() < 4) { 
                availableAssistantsData.remove(selectedAssistant);
                addedAssistantsData.add(selectedAssistant);
            } else {
                DialogUtil.showWarningAlert("Límite de Asistentes", 
                    "Un vuelo solo puede tener un máximo de 4 asistentes."); 
            }
        } else {
            DialogUtil.showWarningAlert("Sin selección", 
                "Por favor, selecciona un asistente de la tabla de asistentes disponibles."); 
        }
    }

    @FXML
    private void btnRemove(ActionEvent event) {
        Assistant selectedAssistant = tvAddedAssistants.getSelectionModel().getSelectedItem();
        if (selectedAssistant != null) {
            addedAssistantsData.remove(selectedAssistant);
            availableAssistantsData.add(selectedAssistant);
        } else {
            DialogUtil.showWarningAlert("Sin selección", 
                "Por favor, selecciona un asistente de la tabla de asistentes añadidos."); 
        }
    }

    @FXML
    private void btnSaveChanges(ActionEvent event) {
        if (addedAssistantsData.isEmpty()) {
            DialogUtil.showWarningAlert("Asistentes Requeridos", 
                "Debe añadir al menos un asistente al vuelo."); 
            return;
        }
        
        if (addedAssistantsData.size() > 4) {
            DialogUtil.showWarningAlert("Límite de Asistentes", 
                "Un vuelo solo puede tener un máximo de 4 asistentes."); 
            return;
        }

        assistantsConfirmed = true;
        
        DialogUtil.showInfoAlert("Cambios guardados", 
            "Los asistentes seleccionados han sido registrados."); 
        closeWindow();
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        assistantsConfirmed = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tvAvailableAssistants.getScene().getWindow();
        stage.close();
    }
}