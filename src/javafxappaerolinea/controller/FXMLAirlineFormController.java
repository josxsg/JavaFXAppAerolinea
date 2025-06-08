/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxappaerolinea.model.dao.AirlineDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.IdGeneratorUtil;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLAirlineFormController implements Initializable {

    @FXML
    private TextField tfName;
    @FXML
    private TextField tfAddress;
    @FXML
    private TextField tfContactPerson;
    @FXML
    private TextField tfPhoneNumber;
    @FXML
    private Label lbNameError;
    @FXML
    private Label lbAddressError;
    @FXML
    private Label lbContactPersonError;
    @FXML
    private Label lbPhoneNumberError;

    private AirlineDAO airlineDAO;
    private Airline airline;
    private boolean isEditMode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        airlineDAO = new AirlineDAO();
    }

    public void initData(Airline airline) {
        if (airline != null) {
            this.airline = airline;
            isEditMode = true;
            tfName.setText(airline.getName());
            tfAddress.setText(airline.getAddress());
            tfContactPerson.setText(airline.getContactPerson());
            tfPhoneNumber.setText(airline.getPhoneNumber());
        } else {
            this.airline = new Airline();
            isEditMode = false;
        }
    }

    @FXML
    private void btnSave(ActionEvent event) {
        if (!validateFields()) {
            return; // Detiene la ejecución si la validación falla
        }

        airline.setName(tfName.getText().trim());
        airline.setAddress(tfAddress.getText().trim());
        airline.setContactPerson(tfContactPerson.getText().trim());
        airline.setPhoneNumber(tfPhoneNumber.getText().trim());

        try {
            if (isEditMode) {
                airlineDAO.update(airline);
                DialogUtil.showInfoAlert("Éxito", "Aerolínea actualizada exitosamente.");
            } else {
                int newAirlineId = IdGeneratorUtil.generateAirlineId();
                airline.setIdentificationNumber(newAirlineId);
                airlineDAO.save(airline);
                DialogUtil.showInfoAlert("Éxito", "Aerolínea guardada exitosamente.");
            }
            closeStage();
        } catch (IOException | javafxappaerolinea.exception.DuplicateResourceException | javafxappaerolinea.exception.ResourceNotFoundException e) {
            DialogUtil.showErrorAlert("Error", "Error al guardar la aerolínea: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        clearErrorLabels();
        boolean isValid = true;

        if (tfName.getText().trim().isEmpty()) {
            lbNameError.setText("Nombre requerido");
            isValid = false;
        }
        if (tfAddress.getText().trim().isEmpty()) {
            lbAddressError.setText("Dirección requerida");
            isValid = false;
        }
        if (tfContactPerson.getText().trim().isEmpty()) {
            lbContactPersonError.setText("Contacto requerido");
            isValid = false;
        }
        if (tfPhoneNumber.getText().trim().isEmpty()) {
            lbPhoneNumberError.setText("Teléfono requerido");
            isValid = false;
        }

        return isValid;
    }

    private void clearErrorLabels() {
        lbNameError.setText("");
        lbAddressError.setText("");
        lbContactPersonError.setText("");
        lbPhoneNumberError.setText("");
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }
}