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
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.service.SessionManager;
import javafxappaerolinea.utility.PasswordUtil;
import javafxappaerolinea.utility.ValidationUtil;

public class FXMLEditAssistantProfileController implements Initializable {
    
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
    private TextField tfEmail;
    
    @FXML
    private TextField tfAssistanceHours;
    
    @FXML
    private TextField tfNumberOfLanguages;
    
    @FXML
    private PasswordField pfPassword;
    
    @FXML
    private PasswordField pfConfirmPassword;
    
    private Assistant assistant;
    private EmployeeDAO employeeDAO;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        employeeDAO = new EmployeeDAO();
        configureGenderComboBox();
    }
    
    private void configureGenderComboBox() {
        ObservableList<String> genderOptions = FXCollections.observableArrayList(
                "Masculino", "Femenino", "Otro"
        );
        cbGender.setItems(genderOptions);
    }
    
    public void setAssistant(Assistant assistant) {
        this.assistant = assistant;
        loadAssistantData();
    }
    
    private void loadAssistantData() {
        if (assistant != null) {
            lbId.setText(String.valueOf(assistant.getId()));
            tfName.setText(assistant.getName());
            tfAddress.setText(assistant.getAddress());
            
            if (assistant.getBirthDate() != null) {
                LocalDate birthDate = assistant.getBirthDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dpBirthDate.setValue(birthDate);
            }
            
            cbGender.setValue(assistant.getGender());
            tfSalary.setText(String.valueOf(assistant.getSalary()));
            tfEmail.setText(assistant.getEmail());
            tfAssistanceHours.setText(String.valueOf(assistant.getAssistanceHours()));
            tfNumberOfLanguages.setText(String.valueOf(assistant.getNumberOfLanguages()));
        }
    }
    
    @FXML
    private void btnSave(ActionEvent event) {
        try {
            validateFields();
            
            assistant.setName(tfName.getText().trim());
            assistant.setAddress(tfAddress.getText().trim());
            
            if (dpBirthDate.getValue() != null) {
                Date birthDate = Date.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                assistant.setBirthDate(birthDate);
            }
            
            assistant.setGender(cbGender.getValue());
            assistant.setSalary(Double.parseDouble(tfSalary.getText().trim()));
            assistant.setEmail(tfEmail.getText().trim());
            assistant.setAssistanceHours(Integer.parseInt(tfAssistanceHours.getText().trim()));
            assistant.setNumberOfLanguages(Integer.parseInt(tfNumberOfLanguages.getText().trim()));
            
            if (!pfPassword.getText().isEmpty()) {
                String hashedPassword = PasswordUtil.hashPassword(pfPassword.getText());
                assistant.setPassword(hashedPassword);
            }
            
            try {
                employeeDAO.update(assistant);
                
                SessionManager.getInstance().setCurrentUser(assistant);
                
                showAlert("Actualización Exitosa", "El perfil se ha actualizado correctamente.", Alert.AlertType.INFORMATION);
                closeWindow();
            } catch (ResourceNotFoundException ex) {
                showAlert("Error", "No se encontró el asistente en la base de datos: " + ex.getMessage(), Alert.AlertType.ERROR);
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
        ValidationUtil.validateNotEmpty(tfEmail.getText().trim(), "Email");
        ValidationUtil.validateNotEmpty(tfAssistanceHours.getText().trim(), "Horas de asistencia");
        ValidationUtil.validateNotEmpty(tfNumberOfLanguages.getText().trim(), "Número de idiomas");
        
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
            int assistanceHours = Integer.parseInt(tfAssistanceHours.getText().trim());
            if (assistanceHours < 0) {
                throw new ValidationException("Las horas de asistencia no pueden ser negativas.");
            }
        } catch (NumberFormatException ex) {
            throw new ValidationException("Las horas de asistencia deben ser un valor numérico válido.");
        }
        
        try {
            int numberOfLanguages = Integer.parseInt(tfNumberOfLanguages.getText().trim());
            if (numberOfLanguages <= 0) {
                throw new ValidationException("El número de idiomas debe ser mayor que cero.");
            }
        } catch (NumberFormatException ex) {
            throw new ValidationException("El número de idiomas debe ser un valor numérico válido.");
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