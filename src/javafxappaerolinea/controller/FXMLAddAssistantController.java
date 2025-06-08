// FXMLAddAssistantController.java
package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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

    private List<Assistant> initialAssistants; // Para almacenar el estado inicial cuando se abre la ventana
    private boolean changesConfirmed = false; // Bandera para indicar si los cambios fueron guardados

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        employeeDAO = new EmployeeDAO(); //
        availableAssistantsData = FXCollections.observableArrayList(); //
        addedAssistantsData = FXCollections.observableArrayList(); //
        initialAssistants = new ArrayList<>(); // Inicializar siempre a una lista vacía

        configureTableColumns(); //
        loadAvailableAssistants(); //
    }

    // Nuevo método para verificar si los cambios fueron confirmados
    public boolean areChangesConfirmed() {
        return changesConfirmed; //
    }

    public void initData(List<Assistant> currentFlightAssistants) {
        // Almacenar siempre el estado inicial (incluso si es nulo o vacío)
        initialAssistants.clear(); //
        if (currentFlightAssistants != null) { //
            initialAssistants.addAll(currentFlightAssistants); //
        }

        // Limpiar los datos existentes y poblar según los asistentes iniciales
        addedAssistantsData.clear(); //
        if (currentFlightAssistants != null && !currentFlightAssistants.isEmpty()) { //
            List<Assistant> toRemoveFromAvailable = new ArrayList<>(); //
            for (Assistant currentAssistant : currentFlightAssistants) { //
                // Buscar y añadir los asistentes existentes a la tabla de 'añadidos'
                Assistant foundAssistant = availableAssistantsData.stream() //
                                        .filter(a -> a.getId().equals(currentAssistant.getId())) //
                                        .findFirst() //
                                        .orElse(null); //
                if (foundAssistant != null) { //
                    addedAssistantsData.add(foundAssistant); //
                    toRemoveFromAvailable.add(foundAssistant); //
                } else {
                    // Si un asistente fue añadido previamente pero no está en disponibles (ej. eliminado de los datos fuente)
                    addedAssistantsData.add(currentAssistant); //
                }
            }
            // Eliminar los asistentes añadidos de la tabla de 'disponibles'
            availableAssistantsData.removeAll(toRemoveFromAvailable); //
        }
        // Inicializar changesConfirmed a falso
        this.changesConfirmed = false; //
    }

    // Este método ahora devuelve el estado actual de addedAssistantsData.
    // El FXMLFlightFormController verificará la bandera 'changesConfirmed'.
    public List<Assistant> getSelectedAssistantsList() {
        return new ArrayList<>(addedAssistantsData); //
    }

    private void configureTableColumns() {
        tcAvailableName.setCellValueFactory(new PropertyValueFactory<>("name")); //
        tcAvailableAge.setCellValueFactory(cellData -> { //
            Assistant assistant = cellData.getValue(); //
            if (assistant != null && assistant.getBirthDate() != null) { //
                return new SimpleIntegerProperty(assistant.getAge()).asObject(); //
            }
            return new SimpleIntegerProperty(0).asObject(); //
        });
        tcAvailableAssistanceHours.setCellValueFactory(new PropertyValueFactory<>("assistanceHours")); //
        tcAvailableLanguages.setCellValueFactory(new PropertyValueFactory<>("numberOfLanguages")); //

        tvAvailableAssistants.setItems(availableAssistantsData); //

        tcAddedName.setCellValueFactory(new PropertyValueFactory<>("name")); //
        tcAddedAge.setCellValueFactory(cellData -> { //
            Assistant assistant = cellData.getValue(); //
            if (assistant != null && assistant.getBirthDate() != null) { //
                return new SimpleIntegerProperty(assistant.getAge()).asObject(); //
            }
            return new SimpleIntegerProperty(0).asObject(); //
        });
        tcAddedAssistanceHours.setCellValueFactory(new PropertyValueFactory<>("assistanceHours")); //
        tcAddedLanguages.setCellValueFactory(new PropertyValueFactory<>("numberOfLanguages")); //

        tvAddedAssistants.setItems(addedAssistantsData); //
    }

    private void loadAvailableAssistants() {
        try {
            List<Assistant> allAssistants = employeeDAO.findAllAssistants(); //
            availableAssistantsData.setAll(allAssistants); //
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error de carga", "No se pudieron cargar los asistentes disponibles: " + e.getMessage()); //
            e.printStackTrace(); //
        }
    }

    @FXML
    private void btnAdd(ActionEvent event) {
        Assistant selectedAssistant = tvAvailableAssistants.getSelectionModel().getSelectedItem(); //
        if (selectedAssistant != null) { //
            if (addedAssistantsData.size() < 4) { //
                availableAssistantsData.remove(selectedAssistant); //
                addedAssistantsData.add(selectedAssistant); //
            } else {
                DialogUtil.showWarningAlert("Límite de Asistentes", "Un vuelo solo puede tener un máximo de 4 asistentes."); //
            }
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, selecciona un asistente de la tabla de asistentes disponibles."); //
        }
    }

    @FXML
    private void btnRemove(ActionEvent event) {
        Assistant selectedAssistant = tvAddedAssistants.getSelectionModel().getSelectedItem(); //
        if (selectedAssistant != null) { //
            addedAssistantsData.remove(selectedAssistant); //
            availableAssistantsData.add(selectedAssistant); //
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, selecciona un asistente de la tabla de asistentes añadidos."); //
        }
    }

    @FXML
    private void btnSaveChanges(ActionEvent event) {
        if (addedAssistantsData.isEmpty()) { //
            DialogUtil.showWarningAlert("Asistentes Requeridos", "Debe añadir al menos un asistente al vuelo."); //
            return; //
        }
        if (addedAssistantsData.size() > 4) { //
             DialogUtil.showWarningAlert("Límite de Asistentes", "Un vuelo solo puede tener un máximo de 4 asistentes. Por favor, remueve asistentes de la lista de añadidos."); //
             return; //
        }

        changesConfirmed = true; // Establecer la bandera a verdadero
        DialogUtil.showInfoAlert("Cambios guardados", "Los asistentes seleccionados han sido registrados."); //
        closeWindow(); //
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        changesConfirmed = false; // Establecer la bandera a falso
        closeWindow(); //
    }

    private void closeWindow() {
        Stage stage = (Stage) tvAvailableAssistants.getScene().getWindow(); //
        stage.close(); //
    }
}