/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappaerolinea.JavaFXAppAerolinea;
import javafxappaerolinea.model.dao.AirlineDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.utility.DialogUtil;

// Importa el controlador de la ventana que quieres abrir
import javafxappaerolinea.controller.FXMLShowAirplanesController;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLAirlineController implements Initializable {

    @FXML
    private TableView<Airline> tvAirlines;
    @FXML
    private TableColumn<Airline, Integer> tcId;
    @FXML
    private TableColumn<Airline, String> tcName;
    @FXML
    private TableColumn<Airline, String> tcAddress;
    @FXML
    private TableColumn<Airline, String> tcContact;
    @FXML
    private TableColumn<Airline, String> tcPhone;

    private AirlineDAO airlineDAO;
    private ObservableList<Airline> airlines;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        airlineDAO = new AirlineDAO();
        airlines = FXCollections.observableArrayList();
        configureTable();
        loadTableData();
    }

    private void configureTable() {
        tcId.setCellValueFactory(new PropertyValueFactory<>("identificationNumber"));
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        tcContact.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        tcPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    private void loadTableData() {
        try {
            List<Airline> airlineList = airlineDAO.findAll();
            airlines.setAll(airlineList);
            tvAirlines.setItems(airlines);
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al cargar las aerolíneas: " + e.getMessage());
        }
    }

    @FXML
    private void btnAddAirline(ActionEvent event) {
        openAirlineForm(null);
    }

    @FXML
    private void btnEditAirline(ActionEvent event) {
        Airline selectedAirline = tvAirlines.getSelectionModel().getSelectedItem();
        if (selectedAirline == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione una aerolínea para editar.");
            return;
        }
        openAirlineForm(selectedAirline);
    }

    @FXML
    private void btnDeleteAirline(ActionEvent event) {
        Airline selectedAirline = tvAirlines.getSelectionModel().getSelectedItem();
        if (selectedAirline == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione una aerolínea para eliminar.");
            return;
        }

        boolean confirmed = DialogUtil.showConfirmationDialog("Confirmar eliminación",
                "¿Está seguro de que desea eliminar la aerolínea seleccionada?");

        if (confirmed) {
            try {
                airlineDAO.delete(selectedAirline.getIdentificationNumber());
                DialogUtil.showInfoAlert("Éxito", "Aerolínea eliminada exitosamente.");
                loadTableData(); // Refresh the table
            } catch (IOException | javafxappaerolinea.exception.ResourceNotFoundException e) {
                DialogUtil.showErrorAlert("Error", "Error al eliminar la aerolínea: " + e.getMessage());
            }
        }
    }

    @FXML
    private void btnExport(ActionEvent event) {
        // Implementation for exporting data
    }

    @FXML
    private void btnViewAirplanes(ActionEvent event) {
        Airline selectedAirline = tvAirlines.getSelectionModel().getSelectedItem();
        if (selectedAirline == null) {
            DialogUtil.showWarningAlert("Sin selección", "Por favor, seleccione una aerolínea para ver sus aviones.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource("view/FXMLShowAirplanes.fxml"));
            Parent root = loader.load();

            FXMLShowAirplanesController controller = loader.getController();
            controller.initData(selectedAirline); // Pasa la aerolínea seleccionada al otro controlador

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Aviones de " + selectedAirline.getName());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al abrir la vista de aviones: " + e.getMessage());
        }
    }

    @FXML
    private void btnViewFlights(ActionEvent event) {
        // Implementation for viewing flights
    }

    private void openAirlineForm(Airline airline) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource("view/FXMLAirlineForm.fxml"));
            Parent root = loader.load();

            FXMLAirlineFormController controller = loader.getController();
            controller.initData(airline);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(airline == null ? "Agregar Aerolínea" : "Editar Aerolínea");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadTableData(); // Refresh table after form is closed
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error", "Error al abrir el formulario de aerolínea: " + e.getMessage());
        }
    }
}