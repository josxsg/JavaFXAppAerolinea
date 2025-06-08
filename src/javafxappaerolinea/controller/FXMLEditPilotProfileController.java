package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.exception.ValidationException;
import javafxappaerolinea.model.dao.EmployeeDAO;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.service.SessionManager;
import javafxappaerolinea.utility.PasswordUtil;
import javafxappaerolinea.utility.ValidationUtil;

public class FXMLEditPilotProfileController implements Initializable {
    
    @FXML
    private Label lbId;
    
    @FXML
    private TextField tfName;
    
    @FXML
    private TextField tfAddress;
    
    @FXML
    private DatePicker dpBirthDate;
    
    @FXML
    private ComboBox<String> cbGender;
    
    @FXML
    private TextField tfSalary;
    
    @FXML
    private TextField tfYearsExperience;
    
    @FXML
    private TextField tfEmail;
    
    @FXML
    private TextField tfFlightHours;
    
    @FXML
    private ComboBox<String> cbLicenseType;
    
    @FXML
    private PasswordField pfPassword;
    
    @FXML
    private PasswordField pfConfirmPassword;
    
    private Pilot pilot;
    private EmployeeDAO employeeDAO;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        employeeDAO = new EmployeeDAO();
        configureGenderComboBox();
        configureLicenseTypeComboBox();
    }
    
    private void configureGenderComboBox() {
        ObservableList<String> genderOptions = FXCollections.observableArrayList(
                "Masculino", "Femenino", "Otro"
        );
        cbGender.setItems(genderOptions);
    }
    
    private void configureLicenseTypeComboBox() {
        ObservableList<String> licenseOptions = FXCollections.observableArrayList(
                "Comercial", "Privado", "Transporte de Línea Aérea", "Instructor"
        );
        cbLicenseType.setItems(licenseOptions);
    }
    
    public void setPilot(Pilot pilot) {
        this.pilot = pilot;
        loadPilotData();
    }
    
    private void loadPilotData() {
        if (pilot != null) {
            lbId.setText(String.valueOf(pilot.getId()));
            tfName.setText(pilot.getName());
            tfAddress.setText(pilot.getAddress());
            
            if (pilot.getBirthDate() != null) {
                LocalDate birthDate = pilot.getBirthDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dpBirthDate.setValue(birthDate);
            }
            
            cbGender.setValue(pilot.getGender());
            tfSalary.setText(String.valueOf(pilot.getSalary()));
            tfYearsExperience.setText(String.valueOf(pilot.getYearsExperience()));
            tfEmail.setText(pilot.getEmail());
            tfFlightHours.setText(String.valueOf(pilot.getFlightHours()));
            cbLicenseType.setValue(pilot.getLicenseType());
        }
    }
    
    @FXML
    private void btnSave(ActionEvent event) {
        try {
            validateFields();
            
            pilot.setName(tfName.getText().trim());
            pilot.setAddress(tfAddress.getText().trim());
            
            if (dpBirthDate.getValue() != null) {
                Date birthDate = Date.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                pilot.setBirthDate(birthDate);
            }
            
            pilot.setGender(cbGender.getValue());
            pilot.setSalary(Double.parseDouble(tfSalary.getText().trim()));
            pilot.setYearsExperience(Integer.parseInt(tfYearsExperience.getText().trim()));
            pilot.setEmail(tfEmail.getText().trim());
            pilot.setFlightHours(Double.parseDouble(tfFlightHours.getText().trim()));
            pilot.setLicenseType(cbLicenseType.getValue());
            
            if (!pfPassword.getText().isEmpty()) {
                String hashedPassword = PasswordUtil.hashPassword(pfPassword.getText());
                pilot.setPassword(hashedPassword);
            }
            
            try {
                employeeDAO.update(pilot);
                
                SessionManager.getInstance().setCurrentUser(pilot);
                
                showAlert("Actualización Exitosa", "El perfil se ha actualizado correctamente.", Alert.AlertType.INFORMATION);
                closeWindow();
            } catch (ResourceNotFoundException ex) {
                showAlert("Error", "No se encontró el piloto en la base de datos: " + ex.getMessage(), Alert.AlertType.ERROR);
            } catch (IOException ex) {
                showAlert("Error", "Error al guardar los datos: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (ValidationException ex) {
            showAlert("Error de Validación", ex.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException ex) {
            showAlert("Error de Formato", "Por favor, ingrese valores numéricos válidos.", Alert.AlertType.ERROR);
        } catch (Exception ex) {
            showAlert("Error", "Ocurrió un error al guardar los cambios: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void validateFields() throws ValidationException {
        ValidationUtil.validateNotEmpty(tfName.getText().trim(), "Nombre");
        ValidationUtil.validateNotEmpty(tfAddress.getText().trim(), "Dirección");
        
        if (dpBirthDate.getValue() == null) {
            throw new ValidationException("La fecha de nacimiento es requerida.");
        }
        
        if (cbGender.getValue() == null) {
            throw new ValidationException("El género es requerido.");
        }
        
        ValidationUtil.validateNotEmpty(tfSalary.getText().trim(), "Salario");
        ValidationUtil.validateNotEmpty(tfYearsExperience.getText().trim(), "Años de experiencia");
        ValidationUtil.validateNotEmpty(tfEmail.getText().trim(), "Email");
        ValidationUtil.validateNotEmpty(tfFlightHours.getText().trim(), "Horas de vuelo");
        
        if (cbLicenseType.getValue() == null) {
            throw new ValidationException("El tipo de licencia es requerido.");
        }
        
        ValidationUtil.validateEmail(tfEmail.getText().trim(), "Email");
        
        try {
            double salary = Double.parseDouble(tfSalary.getText().trim());
            if (salary <= 0) {
                throw new ValidationException("El salario debe ser mayor que cero.");
            }
        } catch (NumberFormatException ex) {
            throw new ValidationException("El salario debe ser un valor numérico válido.");
        }
        
        try {
            int yearsExperience = Integer.parseInt(tfYearsExperience.getText().trim());
            if (yearsExperience < 0) {
                throw new ValidationException("Los años de experiencia no pueden ser negativos.");
            }
        } catch (NumberFormatException ex) {
            throw new ValidationException("Los años de experiencia deben ser un valor numérico válido.");
        }
        
        try {
            double flightHours = Double.parseDouble(tfFlightHours.getText().trim());
            if (flightHours < 0) {
                throw new ValidationException("Las horas de vuelo no pueden ser negativas.");
            }
        } catch (NumberFormatException ex) {
            throw new ValidationException("Las horas de vuelo deben ser un valor numérico válido.");
        }
        
        if (!pfPassword.getText().isEmpty() && !pfPassword.getText().equals(pfConfirmPassword.getText())) {
            throw new ValidationException("Las contraseñas no coinciden.");
        }
    }
    
    @FXML
    private void btnCancel(ActionEvent event) {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) lbId.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}