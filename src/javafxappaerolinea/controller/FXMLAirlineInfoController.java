package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafxappaerolinea.model.dao.AirplaneDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Airplane;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Employee;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.service.SessionManager;

public class FXMLAirlineInfoController implements Initializable {

    @FXML
    private Label lbIdentificationNumber;

    @FXML
    private Label lbName;

    @FXML
    private Label lbAddress;

    @FXML
    private Label lbContactPerson;

    @FXML
    private Label lbPhoneNumber;

    @FXML
    private TableView<Airplane> tvAirplanes;

    @FXML
    private TableColumn<Airplane, Integer> tcAirplaneId;

    @FXML
    private TableColumn<Airplane, String> tcModel;

    @FXML
    private TableColumn<Airplane, Integer> tcCapacity;

    @FXML
    private TableColumn<Airplane, String> tcManufacturer;

    @FXML
    private TableColumn<Airplane, Integer> tcYearOfManufacture;

    private Airline airline;
    private ObservableList<Airplane> airplanes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        loadAirlineInfo();
    }

    private void configureTableColumns() {
        // Asegúrate de que los nombres de las propiedades coincidan con tu clase Airplane
        tcAirplaneId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        tcCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tcManufacturer.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        tcYearOfManufacture.setCellValueFactory(new PropertyValueFactory<>("yearOfManufacture"));
    }

    private void loadAirlineInfo() {
        try {
            Employee currentEmployee = SessionManager.getInstance().getCurrentUser();

            if (currentEmployee != null) {
                if (currentEmployee instanceof Pilot) {
                    airline = ((Pilot) currentEmployee).getAirline();
                } else if (currentEmployee instanceof Assistant) {
                    airline = ((Assistant) currentEmployee).getAirline();
                } else {
                    showAlert("Información no disponible",
                            "Este tipo de empleado no tiene una aerolínea asociada directamente.",
                            Alert.AlertType.INFORMATION);
                    return;
                }

                if (airline != null) {
                    lbIdentificationNumber.setText(String.valueOf(airline.getIdentificationNumber()));
                    lbName.setText(airline.getName());
                    lbAddress.setText(airline.getAddress());
                    lbContactPerson.setText(airline.getContactPerson());
                    lbPhoneNumber.setText(airline.getPhoneNumber());

                    loadAirplanes();
                } else {
                    showAlert("Información no disponible",
                            "No se encontró información de la aerolínea asociada.",
                            Alert.AlertType.INFORMATION);
                }
            }
        } catch (Exception ex) {
            showAlert("Error",
                    "No se pudo cargar la información de la aerolínea: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void loadAirplanes() {
        try {
            AirplaneDAO airplaneDAO = new AirplaneDAO();
            List<Airplane> airplaneList = airplaneDAO.findByAirline(airline.getIdentificationNumber());

            if (airplaneList != null && !airplaneList.isEmpty()) {
                airplanes = FXCollections.observableArrayList(airplaneList);
                tvAirplanes.setItems(airplanes);
            } else {
                tvAirplanes.setItems(FXCollections.observableArrayList());
                showAlert("Información",
                        "No hay aviones registrados para esta aerolínea.",
                        Alert.AlertType.INFORMATION);
            }
        } catch (IOException ex) {
            showAlert("Error",
                    "Error al cargar los aviones: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
