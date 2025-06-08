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
import javafx.scene.control.TableCell;
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
    private TableColumn<Airplane, String> tcRegistration;
    
    @FXML
    private TableColumn<Airplane, String> tcModel;
    
    @FXML
    private TableColumn<Airplane, Integer> tcCapacity;
    
    @FXML
    private TableColumn<Airplane, Integer> tcAge;
    
    @FXML
    private TableColumn<Airplane, Double> tcWeight;
    
    @FXML
    private TableColumn<Airplane, Boolean> tcStatus;
    
    private Airline airline;
    private ObservableList<Airplane> airplanes;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        loadAirlineInfo();
    }
    
    private void configureTableColumns() {
        tcRegistration.setCellValueFactory(new PropertyValueFactory<>("registration"));
        tcModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        tcCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tcAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        tcWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        tcStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        tcWeight.setCellFactory(column -> {
            return new TableCell<Airplane, Double>() {
                @Override
                protected void updateItem(Double weight, boolean empty) {
                    super.updateItem(weight, empty);
                    if (weight == null || empty) {
                        setText(null);
                    } else {
                        setText(String.format("%.1f", weight));
                    }
                }
            };
        });
        
        tcStatus.setCellFactory(column -> {
            return new TableCell<Airplane, Boolean>() {
                @Override
                protected void updateItem(Boolean status, boolean empty) {
                    super.updateItem(status, empty);
                    if (status == null || empty) {
                        setText(null);
                    } else {
                        setText(status ? "Activo" : "Inactivo");
                    }
                }
            };
        });
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
        } catch (NullPointerException | ClassCastException | IllegalArgumentException | IllegalStateException ex) {
            showAlert("Error", 
                    "Se produjo un error al cargar la información: " + ex.getMessage(), 
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