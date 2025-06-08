/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafxappaerolinea.model.dao.AirplaneDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Airplane;
import javafxappaerolinea.utility.DialogUtil;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLShowAirplanesController implements Initializable {

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
    private TableColumn<Airplane, String> tcStatus;
    @FXML
    private TableColumn<Airplane, String> tcAirline;

    private ObservableList<Airplane> airplanes;
    private AirplaneDAO airplaneDAO; // DAO para acceder a los datos de los aviones

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        airplaneDAO = new AirplaneDAO(); // Instanciar el DAO
        airplanes = FXCollections.observableArrayList();
        tvAirplanes.setItems(airplanes);
        configureTable();
    }    

    public void initData(Airline selectedAirline) {
        if (selectedAirline == null) {
            return; // No hacer nada si no se pasa una aerolínea
        }
        
        try {
            // 1. Cargar la lista completa de todos los aviones desde aviones.json
            List<Airplane> allAirplanes = airplaneDAO.findAll();
            
            // 2. Filtrar la lista para obtener solo los aviones de la aerolínea seleccionada
            List<Airplane> filteredAirplanes = allAirplanes.stream()
                .filter(plane -> plane.getAirline() != null && 
                                 plane.getAirline().getName().equalsIgnoreCase(selectedAirline.getName()))
                .collect(Collectors.toList());
            
            // 3. Cargar los aviones filtrados en la tabla
            airplanes.setAll(filteredAirplanes);
            
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al cargar los datos de los aviones: " + e.getMessage());
        }
    }

    private void configureTable() {
        tcRegistration.setCellValueFactory(new PropertyValueFactory<>("registration"));
        tcModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        tcCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tcAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        tcWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));

        // Columna de Estado (Status) - Muestra texto en lugar de booleano
        tcStatus.setCellValueFactory(cellData -> {
            boolean status = cellData.getValue().isStatus();
            return new SimpleStringProperty(status ? "Activo" : "Inactivo");
        });

        // Columna de Aerolínea (Airline) - Muestra el nombre de la aerolínea
        tcAirline.setCellValueFactory(cellData -> {
            if (cellData.getValue().getAirline() != null) {
                return new SimpleStringProperty(cellData.getValue().getAirline().getName());
            }
            return new SimpleStringProperty("N/A");
        });
    }
}