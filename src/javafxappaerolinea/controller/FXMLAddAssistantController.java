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
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.EmployeeDAO; 
import javafxappaerolinea.model.pojo.Assistant; 
import javafxappaerolinea.utility.DialogUtil; 

public class FXMLAddAssistantController implements Initializable {

    @FXML
    private TableView<Assistant> tvAvailableAssistants;
    @FXML
    private TableColumn<Assistant, String> tcAvailableName;
    @FXML
    private TableColumn<Assistant, Integer> tcAvailableAge;
    @FXML
    private TableColumn<Assistant, Integer> tcAvailableAssistanceHours;
    @FXML
    private TableColumn<Assistant, Integer> tcAvailableLanguages;

    @FXML
    private TableView<Assistant> tvAddedAssistants;
    @FXML
    private TableColumn<Assistant, String> tcAddedName;
    @FXML
    private TableColumn<Assistant, Integer> tcAddedAge;
    @FXML
    private TableColumn<Assistant, Integer> tcAddedAssistanceHours;
    @FXML
    private TableColumn<Assistant, Integer> tcAddedLanguages;

    private ObservableList<Assistant> availableAssistantsData;
    private ObservableList<Assistant> addedAssistantsData;
    
    private EmployeeDAO employeeDAO;

    private List<Assistant> finalSelectedAssistants;
    private List<Assistant> initialAssistants; // Para guardar el estado inicial
    private boolean changesSaved = false; // Para rastrear si se guardaron cambios

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        employeeDAO = new EmployeeDAO();
        availableAssistantsData = FXCollections.observableArrayList();
        addedAssistantsData = FXCollections.observableArrayList();
        finalSelectedAssistants = new ArrayList<>();
        initialAssistants = new ArrayList<>();

        configureTableColumns();
        loadAvailableAssistants();
    }    

    public void initData(List<Assistant> currentFlightAssistants) {
        // Guardar el estado inicial
        if (currentFlightAssistants != null) {
            initialAssistants.addAll(currentFlightAssistants);
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
            
            // Sincronizar finalSelectedAssistants con el estado inicial
            finalSelectedAssistants.clear();
            finalSelectedAssistants.addAll(addedAssistantsData); 
        }
    }

    public List<Assistant> getFinalSelectedAssistants() {
        // Si no se guardaron cambios, devolver la lista inicial
        if (!changesSaved) {
            return new ArrayList<>(initialAssistants);
        }
        return new ArrayList<>(finalSelectedAssistants);
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
            availableAssistantsData.setAll(allAssistants); 
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error de carga", "No se pudieron cargar los asistentes disponibles: " + e.getMessage()); 
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
                // No actualizar finalSelectedAssistants aquí, solo al guardar
            } else {
                DialogUtil.showWarningAlert("Límite de Asistentes", "Un vuelo solo puede tener un máximo de 4 asistentes."); 
            }
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, selecciona un asistente de la tabla de asistentes disponibles."); 
        }
    }

    @FXML
    private void btnRemove(ActionEvent event) {
        Assistant selectedAssistant = tvAddedAssistants.getSelectionModel().getSelectedItem();
        if (selectedAssistant != null) {
            addedAssistantsData.remove(selectedAssistant);
            availableAssistantsData.add(selectedAssistant);
            // No actualizar finalSelectedAssistants aquí, solo al guardar
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, selecciona un asistente de la tabla de asistentes añadidos."); 
        }
    }

    @FXML
    private void btnSaveChanges(ActionEvent event) {
        if (addedAssistantsData.isEmpty()) {
            DialogUtil.showWarningAlert("Asistentes Requeridos", "Debe añadir al menos un asistente al vuelo."); 
            return;
        }
        if (addedAssistantsData.size() > 4) {
             DialogUtil.showWarningAlert("Límite de Asistentes", "Un vuelo solo puede tener un máximo de 4 asistentes. Por favor, remueve asistentes de la lista de añadidos."); 
             return;
        }

        // Actualizar finalSelectedAssistants solo cuando se guardan los cambios
        changesSaved = true;
        finalSelectedAssistants.clear();
        finalSelectedAssistants.addAll(addedAssistantsData);
        
        DialogUtil.showInfoAlert("Cambios guardados", "Los asistentes seleccionados han sido registrados."); 
        closeWindow();
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        // No marcar cambios como guardados
        changesSaved = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tvAvailableAssistants.getScene().getWindow();
        stage.close();
    }
}