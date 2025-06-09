package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafxappaerolinea.model.dao.AirlineDAO;
import javafxappaerolinea.model.dao.AirplaneDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Airplane;
import javafxappaerolinea.utility.DialogUtil;


public class FXMLAirplaneFormController implements Initializable {

    @FXML
    private TextField tfRegistration;
    @FXML
    private TextField tfModel;
    @FXML
    private TextField tfCapacity;
    @FXML
    private TextField tfAge;
    @FXML
    private TextField tfWeight;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private ComboBox<Airline> cbAirline;
    
    @FXML private Label lbRegistrationError;
    @FXML private Label lbModelError;
    @FXML private Label lbCapacityError;
    @FXML private Label lbAgeError;
    @FXML private Label lbWeightError;
    @FXML private Label lbStatusError;
    @FXML private Label lbAirlineError;


    private AirplaneDAO airplaneDAO;
    private AirlineDAO airlineDAO;
    private Airplane airplane;
    private boolean isEditMode;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        airplaneDAO = new AirplaneDAO();
        airlineDAO = new AirlineDAO();
        configureForm();
    }    

    private void configureForm() {
        cbStatus.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));

        try {
            cbAirline.setItems(FXCollections.observableArrayList(airlineDAO.findAll()));
            cbAirline.setConverter(new StringConverter<Airline>() {
                @Override
                public String toString(Airline airline) {
                    return airline == null ? "" : airline.getName();
                }

                @Override
                public Airline fromString(String string) {
                    return null; 
                }
            });
        } catch (IOException e) {
            DialogUtil.showErrorAlert("Error de Carga", "No se pudieron cargar las aerolíneas.");
        }
        
        tfCapacity.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d*") ? c : null));
        tfAge.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d*") ? c : null));
        tfWeight.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d*\\.?\\d*") ? c : null));
    }

    public void initData(Airplane airplane) {
        if (airplane != null) {
            this.isEditMode = true;
            this.airplane = airplane;
            
            tfRegistration.setText(airplane.getRegistration());
            tfRegistration.setDisable(true); 
            tfModel.setText(airplane.getModel());
            tfCapacity.setText(String.valueOf(airplane.getCapacity()));
            tfAge.setText(String.valueOf(airplane.getAge()));
            tfWeight.setText(String.valueOf(airplane.getWeight()));
            cbStatus.setValue(airplane.isStatus() ? "Activo" : "Inactivo");
            
            if (airplane.getAirline() != null) {
                cbAirline.getSelectionModel().select(airplane.getAirline());
            }

        } else {
            this.isEditMode = false;
            this.airplane = new Airplane();
        }
    }

    @FXML
    private void btnSave(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

        airplane.setRegistration(tfRegistration.getText().trim());
        airplane.setModel(tfModel.getText().trim());
        airplane.setCapacity(Integer.parseInt(tfCapacity.getText()));
        airplane.setAge(Integer.parseInt(tfAge.getText()));
        airplane.setWeight(Double.parseDouble(tfWeight.getText()));
        airplane.setStatus(cbStatus.getValue().equals("Activo"));
        airplane.setAirline(cbAirline.getValue());

        try {
            if (isEditMode) {
                airplaneDAO.update(airplane);
                DialogUtil.showInfoAlert("Éxito", "Avión actualizado exitosamente.");
            } else {
                airplaneDAO.save(airplane);
                DialogUtil.showInfoAlert("Éxito", "Avión guardado exitosamente.");
            }
            closeStage();
        } catch (IOException | javafxappaerolinea.exception.DuplicateResourceException | javafxappaerolinea.exception.ResourceNotFoundException e) {
            DialogUtil.showErrorAlert("Error", "Error al guardar el avión: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        clearErrorLabels();
        boolean isValid = true;

        if (tfRegistration.getText().trim().isEmpty()) {
            lbRegistrationError.setText("Matrícula requerida");
            isValid = false;
        }
        if (tfModel.getText().trim().isEmpty()) {
            lbModelError.setText("Modelo requerido");
            isValid = false;
        }
        if (tfCapacity.getText().trim().isEmpty()) {
            lbCapacityError.setText("Capacidad requerida");
            isValid = false;
        }
        if (tfAge.getText().trim().isEmpty()) {
            lbAgeError.setText("Edad requerida");
            isValid = false;
        }
        if (tfWeight.getText().trim().isEmpty()) {
            lbWeightError.setText("Peso requerido");
            isValid = false;
        }
        if (cbStatus.getValue() == null) {
            lbStatusError.setText("Estado requerido");
            isValid = false;
        }
        if (cbAirline.getValue() == null) {
            lbAirlineError.setText("Aerolínea requerida");
            isValid = false;
        }
        return isValid;
    }

    private void clearErrorLabels() {
        lbRegistrationError.setText("");
        lbModelError.setText("");
        lbCapacityError.setText("");
        lbAgeError.setText("");
        lbWeightError.setText("");
        lbStatusError.setText("");
        lbAirlineError.setText("");
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) tfRegistration.getScene().getWindow();
        stage.close();
    }
}