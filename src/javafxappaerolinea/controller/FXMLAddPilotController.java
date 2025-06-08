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
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.utility.DialogUtil;

/**
 * FXML Controller class
 *
 * @author migue
 */
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
    private TableColumn<Pilot, String> tcAvailableFlightTypes; // Se mapeará a licenseType en Pilot

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
    private TableColumn<Pilot, String> tcAddedFlightTypes; // Se mapeará a licenseType en Pilot

    private ObservableList<Pilot> availablePilotsData;
    private ObservableList<Pilot> addedPilotsData;

    private EmployeeDAO employeeDAO;

    private List<Pilot> initialPilots; // Para almacenar el estado inicial cuando se abre la ventana
    private boolean changesConfirmed = false; // Bandera para indicar si los cambios fueron guardados

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        employeeDAO = new EmployeeDAO();
        availablePilotsData = FXCollections.observableArrayList();
        addedPilotsData = FXCollections.observableArrayList();
        initialPilots = new ArrayList<>(); // Inicializar siempre a una lista vacía

        configureTableColumns();
        loadAvailablePilots();
    }

    // Nuevo método para verificar si los cambios fueron confirmados
    public boolean areChangesConfirmed() {
        return changesConfirmed;
    }

    public void initData(List<Pilot> currentFlightPilots) {
        // Almacenar siempre el estado inicial (incluso si es nulo o vacío)
        initialPilots.clear();
        if (currentFlightPilots != null) {
            initialPilots.addAll(currentFlightPilots);
        }

        // Limpiar los datos existentes y poblar según los pilotos iniciales
        addedPilotsData.clear();
        if (currentFlightPilots != null && !currentFlightPilots.isEmpty()) {
            List<Pilot> toRemoveFromAvailable = new ArrayList<>();
            for (Pilot currentPilot : currentFlightPilots) {
                // Buscar y añadir los pilotos existentes a la tabla de 'añadidos'
                Pilot foundPilot = availablePilotsData.stream()
                                        .filter(p -> p.getId().equals(currentPilot.getId()))
                                        .findFirst()
                                        .orElse(null);
                if (foundPilot != null) {
                    addedPilotsData.add(foundPilot);
                    toRemoveFromAvailable.add(foundPilot);
                } else {
                    // Si un piloto fue añadido previamente pero no está en disponibles (ej. eliminado de los datos fuente)
                    addedPilotsData.add(currentPilot);
                }
            }
            // Eliminar los pilotos añadidos de la tabla de 'disponibles'
            availablePilotsData.removeAll(toRemoveFromAvailable);
        }
        // Inicializar changesConfirmed a falso
        this.changesConfirmed = false;
    }

    // Este método ahora devuelve el estado actual de addedPilotsData.
    // El FXMLFlightFormController verificará la bandera 'changesConfirmed'.
    public List<Pilot> getSelectedPilotsList() {
        return new ArrayList<>(addedPilotsData);
    }

    private void configureTableColumns() {
        // Configurar las columnas de la tabla de pilotos disponibles
        tcAvailableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAvailableLicense.setCellValueFactory(new PropertyValueFactory<>("licenseType"));
        tcAvailableAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        tcAvailableExperience.setCellValueFactory(new PropertyValueFactory<>("yearsExperience"));
        tcAvailableFlightTypes.setCellValueFactory(new PropertyValueFactory<>("licenseType"));

        tvAvailablePilots.setItems(availablePilotsData);

        // Configurar las columnas de la tabla de pilotos añadidos
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
            availablePilotsData.setAll(allPilots);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error de carga", "No se pudieron cargar los pilotos disponibles: " + e.getMessage());
            e.printStackTrace();
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
                DialogUtil.showWarningAlert("Límite de Pilotos", "Un vuelo solo puede tener un máximo de 2 pilotos.");
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
        if (addedPilotsData.isEmpty()) {
            DialogUtil.showWarningAlert("Pilotos Requeridos", "Debe añadir al menos un piloto al vuelo.");
            return;
        }
        if (addedPilotsData.size() > 2) {
             DialogUtil.showWarningAlert("Límite de Pilotos", "Un vuelo solo puede tener un máximo de 2 pilotos. Por favor, remueve pilotos de la lista de añadidos.");
             return;
        }

        changesConfirmed = true; // Establecer la bandera a verdadero
        DialogUtil.showInfoAlert("Cambios guardados", "Los pilotos seleccionados han sido registrados.");
        closeWindow();
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        changesConfirmed = false; // Establecer la bandera a falso
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tvAvailablePilots.getScene().getWindow();
        stage.close();
    }
}