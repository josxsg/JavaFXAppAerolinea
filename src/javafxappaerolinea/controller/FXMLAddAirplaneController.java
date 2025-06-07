/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import javafxappaerolinea.model.dao.AirplaneDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Airplane;
import javafxappaerolinea.utility.DialogUtil;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLAddAirplaneController implements Initializable {

    @FXML
    private TableView<Airplane> tvAvailableAirplanes;
    @FXML
    private TableColumn<Airplane, String> tcAvailableRegistration;
    @FXML
    private TableColumn<Airplane, String> tcAvailableModel;
    @FXML
    private TableColumn<Airplane, Integer> tcAvailableCapacity;
    @FXML
    private TableColumn<Airplane, Integer> tcAvailableAge;
    @FXML
    private TableColumn<Airplane, Double> tcAvailableWeight;
    @FXML
    private TableColumn<Airplane, Boolean> tcAvailableStatus;
    @FXML
    private TableColumn<Airplane, Airline> tcAvailableAirline;
    
    @FXML
    private TableView<Airplane> tvAddedAirplanes;
    @FXML
    private TableColumn<Airplane, String> tcAddedRegistration;
    @FXML
    private TableColumn<Airplane, String> tcAddedModel;
    @FXML
    private TableColumn<Airplane, Integer> tcAddedCapacity;
    @FXML
    private TableColumn<Airplane, Integer> tcAddedAge;
    @FXML
    private TableColumn<Airplane, Double> tcAddedWeight;
    @FXML
    private TableColumn<Airplane, Boolean> tcAddedStatus;
    @FXML
    private TableColumn<Airplane, Airline> tcAddedAirline;

    private Airline currentAirline;
    private AirplaneDAO airplaneDAO;
    private ObservableList<Airplane> availableAirplanes;
    private ObservableList<Airplane> addedAirplanes;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        airplaneDAO = new AirplaneDAO();
        availableAirplanes = FXCollections.observableArrayList();
        addedAirplanes = FXCollections.observableArrayList();
        configureTables();
    }    

    public void initData(Airline airline) {
        this.currentAirline = airline;
        loadAirplaneData();
    }

    private void configureTables() {
        // Configurar tabla de aviones disponibles
        tcAvailableRegistration.setCellValueFactory(new PropertyValueFactory<>("registration"));
        tcAvailableModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        tcAvailableCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tcAvailableAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        tcAvailableWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        tcAvailableStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tcAvailableAirline.setCellValueFactory(new PropertyValueFactory<>("airline"));
        tvAvailableAirplanes.setItems(availableAirplanes);

        // Configurar tabla de aviones agregados
        tcAddedRegistration.setCellValueFactory(new PropertyValueFactory<>("registration"));
        tcAddedModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        tcAddedCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tcAddedAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        tcAddedWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        tcAddedStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tcAddedAirline.setCellValueFactory(new PropertyValueFactory<>("airline"));
        tvAddedAirplanes.setItems(addedAirplanes);
    }

    private void loadAirplaneData() {
        try {
            // Cargar todos los aviones
            List<Airplane> allAirplanes = airplaneDAO.findAll();

            // Cargar aviones ya agregados a la aerolínea
            if (currentAirline.getAirplanes() != null) {
                addedAirplanes.setAll(currentAirline.getAirplanes());
            }

            // Determinar aviones disponibles (todos menos los ya agregados)
            List<String> addedRegistrations = addedAirplanes.stream()
                                                            .map(Airplane::getRegistration)
                                                            .collect(Collectors.toList());
            
            List<Airplane> available = allAirplanes.stream()
                                                   .filter(plane -> !addedRegistrations.contains(plane.getRegistration()))
                                                   .collect(Collectors.toList());
            availableAirplanes.setAll(available);
            
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "No se pudo cargar la información de los aviones: " + e.getMessage());
        }
    }

    @FXML
    private void btnAdd(ActionEvent event) {
        Airplane selectedAirplane = tvAvailableAirplanes.getSelectionModel().getSelectedItem();
        if (selectedAirplane != null) {
            availableAirplanes.remove(selectedAirplane);
            addedAirplanes.add(selectedAirplane);
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione un avión de la lista de disponibles.");
        }
    }

    @FXML
    private void btnRemove(ActionEvent event) {
        Airplane selectedAirplane = tvAddedAirplanes.getSelectionModel().getSelectedItem();
        if (selectedAirplane != null) {
            addedAirplanes.remove(selectedAirplane);
            availableAirplanes.add(selectedAirplane);
        } else {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione un avión de la lista de agregados.");
        }
    }

    @FXML
    private void btnSaveChanges(ActionEvent event) {
        currentAirline.setAirplanes(new ArrayList<>(addedAirplanes));
        DialogUtil.showInfoAlert("Éxito", "La lista de aviones ha sido actualizada. Haga clic en 'Guardar' en el formulario anterior para confirmar los cambios.");
        closeStage();
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        closeStage();
    }
    
    private void closeStage() {
        Stage stage = (Stage) tvAvailableAirplanes.getScene().getWindow();
        stage.close();
    }
}