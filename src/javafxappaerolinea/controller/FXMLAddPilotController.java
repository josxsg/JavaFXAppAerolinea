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
import javafxappaerolinea.model.dao.EmployeeDAO; // Para acceder a los pilotos
import javafxappaerolinea.model.pojo.Pilot; // Asegúrate de que esta importación sea correcta
import javafxappaerolinea.utility.DialogUtil; // Para mostrar alertas

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

    // Esta lista se usará para devolver los pilotos seleccionados a la ventana que llamó
    private List<Pilot> finalSelectedPilots;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        employeeDAO = new EmployeeDAO();
        availablePilotsData = FXCollections.observableArrayList();
        addedPilotsData = FXCollections.observableArrayList();
        finalSelectedPilots = new ArrayList<>();

        configureTableColumns();
        loadAvailablePilots();
    }    

   
    public void initData(List<Pilot> currentFlightPilots) {
        if (currentFlightPilots != null && !currentFlightPilots.isEmpty()) {
            // Mueve los pilotos que ya están asignados a la tabla de "añadidos"
            // y los elimina de la tabla de "disponibles".
            List<Pilot> toRemoveFromAvailable = new ArrayList<>();
            for (Pilot currentPilot : currentFlightPilots) {
                // Busca el piloto por ID en la lista de disponibles
                Pilot foundPilot = availablePilotsData.stream()
                                    .filter(p -> p.getId().equals(currentPilot.getId()))
                                    .findFirst()
                                    .orElse(null);
                if (foundPilot != null) {
                    addedPilotsData.add(foundPilot);
                    toRemoveFromAvailable.add(foundPilot);
                } else {
                    // Si el piloto ya asignado no está en los disponibles, lo añade directamente a 'añadidos'
                    // Esto podría ocurrir si se filtra o si el piloto ya no es "disponible" por otra razón
                    addedPilotsData.add(currentPilot);
                }
            }
            availablePilotsData.removeAll(toRemoveFromAvailable);
            
            // Inicializa finalSelectedPilots con los pilotos que ya estaban
            finalSelectedPilots.addAll(addedPilotsData);
        }
    }

    // Método para que la ventana que llamó pueda obtener los pilotos seleccionados
    public List<Pilot> getFinalSelectedPilots() {
        return finalSelectedPilots;
    }

    private void configureTableColumns() {
        // Configurar las columnas de la tabla de pilotos disponibles
        tcAvailableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAvailableLicense.setCellValueFactory(new PropertyValueFactory<>("licenseType")); // Corregido a licenseType
        tcAvailableAge.setCellValueFactory(new PropertyValueFactory<>("age")); // Pilot hereda de Employee, que tiene birthDate. Necesitas un getter para 'age' o calcularlo.
        tcAvailableExperience.setCellValueFactory(new PropertyValueFactory<>("yearsExperience")); // Corregido a yearsExperience
        tcAvailableFlightTypes.setCellValueFactory(new PropertyValueFactory<>("licenseType")); // Usando licenseType para esto, ajusta si tienes otra propiedad

        tvAvailablePilots.setItems(availablePilotsData);

        // Configurar las columnas de la tabla de pilotos añadidos
        tcAddedName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAddedLicense.setCellValueFactory(new PropertyValueFactory<>("licenseType")); // Corregido a licenseType
        tcAddedAge.setCellValueFactory(new PropertyValueFactory<>("age")); // Pilot hereda de Employee, que tiene birthDate. Necesitas un getter para 'age' o calcularlo.
        tcAddedExperience.setCellValueFactory(new PropertyValueFactory<>("yearsExperience")); // Corregido a yearsExperience
        tcAddedFlightTypes.setCellValueFactory(new PropertyValueFactory<>("licenseType")); // Usando licenseType para esto, ajusta si tienes otra propiedad
        
        tvAddedPilots.setItems(addedPilotsData);
    }

    private void loadAvailablePilots() {
        try {
            List<Pilot> allPilots = employeeDAO.findAllPilots(); //
            availablePilotsData.setAll(allPilots); // Carga todos los pilotos inicialmente
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error de carga", "No se pudieron cargar los pilotos disponibles: " + e.getMessage()); //
            e.printStackTrace();
        }
    }

    @FXML
    private void btnAdd(ActionEvent event) {
        Pilot selectedPilot = tvAvailablePilots.getSelectionModel().getSelectedItem();
        if (selectedPilot != null) {
            if (addedPilotsData.size() < 2) { // Asumiendo un máximo de 2 pilotos por vuelo
                availablePilotsData.remove(selectedPilot);
                addedPilotsData.add(selectedPilot);
            } else {
                DialogUtil.showWarningAlert("Límite de Pilotos", "Un vuelo solo puede tener un máximo de 2 pilotos."); //
            }
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, selecciona un piloto de la tabla de pilotos disponibles."); //
        }
    }

    @FXML
    private void btnRemove(ActionEvent event) {
        Pilot selectedPilot = tvAddedPilots.getSelectionModel().getSelectedItem();
        if (selectedPilot != null) {
            addedPilotsData.remove(selectedPilot);
            availablePilotsData.add(selectedPilot);
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, selecciona un piloto de la tabla de pilotos añadidos."); //
        }
    }

    @FXML
    private void btnSaveChanges(ActionEvent event) {
        if (addedPilotsData.isEmpty()) {
            DialogUtil.showWarningAlert("Pilotos Requeridos", "Debe añadir al menos un piloto al vuelo."); //
            return;
        }
        if (addedPilotsData.size() > 2) {
             DialogUtil.showWarningAlert("Límite de Pilotos", "Un vuelo solo puede tener un máximo de 2 pilotos. Por favor, remueve pilotos de la lista de añadidos."); //
             return;
        }

        // Guarda los pilotos actualmente en la tabla de "añadidos" como la selección final
        finalSelectedPilots.clear();
        finalSelectedPilots.addAll(addedPilotsData);
        
        DialogUtil.showInfoAlert("Cambios guardados", "Los pilotos seleccionados han sido registrados."); //
        closeWindow();
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        // Si se cancela, no se actualiza finalSelectedPilots, conservando el estado inicial
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tvAvailablePilots.getScene().getWindow();
        stage.close();
    }
}