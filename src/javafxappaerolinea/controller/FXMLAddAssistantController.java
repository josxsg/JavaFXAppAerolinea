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
import javafxappaerolinea.model.dao.EmployeeDAO; // Para acceder a los asistentes
import javafxappaerolinea.model.pojo.Assistant; // Importa tu clase Assistant
import javafxappaerolinea.utility.DialogUtil; // Para mostrar alertas

/**
 * FXML Controller class
 *
 * @author migue
 */
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

    // Esta lista se usará para devolver los asistentes seleccionados a la ventana que llamó
    private List<Assistant> finalSelectedAssistants;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        employeeDAO = new EmployeeDAO();
        availableAssistantsData = FXCollections.observableArrayList();
        addedAssistantsData = FXCollections.observableArrayList();
        finalSelectedAssistants = new ArrayList<>();

        configureTableColumns();
        loadAvailableAssistants();
    }    

 
    public void initData(List<Assistant> currentFlightAssistants) {
        if (currentFlightAssistants != null && !currentFlightAssistants.isEmpty()) {
            // Mueve los asistentes que ya están asignados a la tabla de "añadidos"
            // y los elimina de la tabla de "disponibles".
            List<Assistant> toRemoveFromAvailable = new ArrayList<>();
            for (Assistant currentAssistant : currentFlightAssistants) {
                // Busca el asistente por ID en la lista de disponibles
                Assistant foundAssistant = availableAssistantsData.stream()
                                    .filter(a -> a.getId().equals(currentAssistant.getId()))
                                    .findFirst()
                                    .orElse(null);
                if (foundAssistant != null) {
                    addedAssistantsData.add(foundAssistant);
                    toRemoveFromAvailable.add(foundAssistant);
                } else {
                    // Si el asistente ya asignado no está en los disponibles, lo añade directamente a 'añadidos'
                    addedAssistantsData.add(currentAssistant);
                }
            }
            availableAssistantsData.removeAll(toRemoveFromAvailable);
            
            // Inicializa finalSelectedAssistants con los asistentes que ya estaban
            finalSelectedAssistants.addAll(addedAssistantsData);
        }
    }

    // Método para que la ventana que llamó pueda obtener los asistentes seleccionados
    public List<Assistant> getFinalSelectedAssistants() {
        return finalSelectedAssistants;
    }

    private void configureTableColumns() {
        // Configurar las columnas de la tabla de asistentes disponibles
        tcAvailableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAvailableAge.setCellValueFactory(new PropertyValueFactory<>("age")); // Hereda de Employee
        tcAvailableAssistanceHours.setCellValueFactory(new PropertyValueFactory<>("assistanceHours"));
        tcAvailableLanguages.setCellValueFactory(new PropertyValueFactory<>("numberOfLanguages"));

        tvAvailableAssistants.setItems(availableAssistantsData);

        // Configurar las columnas de la tabla de asistentes añadidos
        tcAddedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAddedAge.setCellValueFactory(new PropertyValueFactory<>("age")); // Hereda de Employee
        tcAddedAssistanceHours.setCellValueFactory(new PropertyValueFactory<>("assistanceHours"));
        tcAddedLanguages.setCellValueFactory(new PropertyValueFactory<>("numberOfLanguages"));
        
        tvAddedAssistants.setItems(addedAssistantsData);
    }

    private void loadAvailableAssistants() {
        try {
            List<Assistant> allAssistants = employeeDAO.findAllAssistants(); //
            availableAssistantsData.setAll(allAssistants); // Carga todos los asistentes
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error de carga", "No se pudieron cargar los asistentes disponibles: " + e.getMessage()); //
            e.printStackTrace();
        }
    }

    @FXML
    private void btnAdd(ActionEvent event) {
        Assistant selectedAssistant = tvAvailableAssistants.getSelectionModel().getSelectedItem();
        if (selectedAssistant != null) {
            if (addedAssistantsData.size() < 4) { // Asumiendo un máximo de 4 asistentes por vuelo
                availableAssistantsData.remove(selectedAssistant);
                addedAssistantsData.add(selectedAssistant);
            } else {
                DialogUtil.showWarningAlert("Límite de Asistentes", "Un vuelo solo puede tener un máximo de 4 asistentes."); //
            }
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, selecciona un asistente de la tabla de asistentes disponibles."); //
        }
    }

    @FXML
    private void btnRemove(ActionEvent event) {
        Assistant selectedAssistant = tvAddedAssistants.getSelectionModel().getSelectedItem();
        if (selectedAssistant != null) {
            addedAssistantsData.remove(selectedAssistant);
            availableAssistantsData.add(selectedAssistant);
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, selecciona un asistente de la tabla de asistentes añadidos."); //
        }
    }

    @FXML
    private void btnSaveChanges(ActionEvent event) {
        if (addedAssistantsData.isEmpty()) {
            DialogUtil.showWarningAlert("Asistentes Requeridos", "Debe añadir al menos un asistente al vuelo."); //
            return;
        }
        if (addedAssistantsData.size() > 4) {
             DialogUtil.showWarningAlert("Límite de Asistentes", "Un vuelo solo puede tener un máximo de 4 asistentes. Por favor, remueve asistentes de la lista de añadidos."); //
             return;
        }

        // Guarda los asistentes actualmente en la tabla de "añadidos" como la selección final
        finalSelectedAssistants.clear();
        finalSelectedAssistants.addAll(addedAssistantsData);
        
        DialogUtil.showInfoAlert("Cambios guardados", "Los asistentes seleccionados han sido registrados."); //
        closeWindow();
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        // Si se cancela, no se actualiza finalSelectedAssistants, conservando el estado inicial
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tvAvailableAssistants.getScene().getWindow();
        stage.close();
    }
}