package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.dao.AirlineDAO;
import javafxappaerolinea.model.dao.EmployeeDAO;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.observable.Notification;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.IdGeneratorUtil;
import javafxappaerolinea.utility.PasswordUtil;
import javafxappaerolinea.utility.ValidationUtil;

public class FXMLAssistantFormController implements Initializable {

    @FXML
    private TextField tfName;
    @FXML
    private Label lbErrorName;
    @FXML
    private TextField tfAddress;
    @FXML
    private Label lbErrorAddress;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private Label lbErrorBirthDate;
    @FXML
    private ComboBox<String> cbGender;
    @FXML
    private Label lbErrorGender;
    @FXML
    private TextField tfEmail;
    @FXML
    private Label lbErrorEmail;
    @FXML
    private TextField tfSalary;
    @FXML
    private Label lbErrorSalary;
    @FXML
    private TextField tfAssistanceHours;
    @FXML
    private Label lbErrorAssistanceHours;
    @FXML
    private TextField tfNumberOfLanguages;
    @FXML
    private Label lbErrorNumberOfLanguages;
    @FXML
    private TextField tfUsername;
    @FXML
    private Label lbErrorUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label lbErrorPassword;
    @FXML
    private ComboBox<Airline> cbAirline;
    @FXML
    private Label lbErrorAirline;

    private boolean isEditing;
    private Assistant assistantToEdit;
    private Notification observer;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbGender.setItems(FXCollections.observableArrayList(Arrays.asList("Masculino", "Femenino", "Otro")));
        loadAirlines();
    }    
    
    public void initializeData(boolean isEditing, Assistant assistantToEdit, Notification observer) {
        this.isEditing = isEditing;
        this.assistantToEdit = assistantToEdit;
        this.observer = observer;
        
        if (isEditing && assistantToEdit != null) {
            loadAssistantData();
        }
    }

    @FXML
    private void btnSave(ActionEvent event) {
        if (validateFields()) {
            saveAssistant();
        }
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        closeWindow();
    }
    
    private void loadAirlines() {
        try {
            AirlineDAO airlineDAO = new AirlineDAO();
            List<Airline> airlines = airlineDAO.findAll();
            cbAirline.setItems(FXCollections.observableArrayList(airlines));
        } catch (IOException e) {
            DialogUtil.showErrorAlert(
                "Error al cargar aerolíneas", 
                "No se pudieron cargar las aerolíneas: " + e.getMessage()
            );
        }
    }
    
    private void loadAssistantData() {
        if (assistantToEdit != null) {
            tfName.setText(assistantToEdit.getName());
            tfAddress.setText(assistantToEdit.getAddress());
            tfEmail.setText(assistantToEdit.getEmail());
            tfAssistanceHours.setText(String.valueOf(assistantToEdit.getAssistanceHours()));
            tfNumberOfLanguages.setText(String.valueOf(assistantToEdit.getNumberOfLanguages()));
            
            Date birthDate = assistantToEdit.getBirthDate();
            if (birthDate != null) {
                LocalDate localDate = birthDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dpBirthDate.setValue(localDate);
            }
            
            cbGender.setValue(assistantToEdit.getGender());
            tfSalary.setText(String.valueOf(assistantToEdit.getSalary()));
            tfUsername.setText(assistantToEdit.getUsername());
            
            if (assistantToEdit.getAirline() != null) {
                for (Airline airline : cbAirline.getItems()) {
                    if (airline.getIdentificationNumber()== assistantToEdit.getAirline().getIdentificationNumber()) {
                        cbAirline.setValue(airline);
                        break;
                    }
                }
            }
            
            pfPassword.setPromptText("Dejar en blanco para mantener la actual");
        }
    }
    
    private boolean validateFields() {
        boolean isValid = true;

        String nameError = ValidationUtil.validateNotEmptyUI(tfName.getText());
        if (nameError != null) {
            lbErrorName.setText(nameError);
            isValid = false;
        } else {
            lbErrorName.setText("");
        }
        
        String addressError = ValidationUtil.validateNotEmptyUI(tfAddress.getText());
        if (addressError != null) {
            lbErrorAddress.setText(addressError);
            isValid = false;
        } else {
            lbErrorAddress.setText("");
        }
        
        String birthDateError = ValidationUtil.validateAgeRangeUI(dpBirthDate.getValue(), 18, 65);
        if (birthDateError != null) {
            lbErrorBirthDate.setText(birthDateError);
            isValid = false;
        } else {
            lbErrorBirthDate.setText("");
        }
        
        String genderError = cbGender.getValue() == null ? ValidationUtil.MSG_REQUIRED : null;
        if (genderError != null) {
            lbErrorGender.setText(genderError);
            isValid = false;
        } else {
            lbErrorGender.setText("");
        }
        
        String emailError = ValidationUtil.validateEmailUI(tfEmail.getText());
        if (emailError != null) {
            lbErrorEmail.setText(emailError);
            isValid = false;
        } else {
            lbErrorEmail.setText("");
        }

        String salaryError = ValidationUtil.validatePositiveUI(tfSalary.getText());
        if (salaryError != null) {
            lbErrorSalary.setText(salaryError);
            isValid = false;
        } else {
            lbErrorSalary.setText("");
        }
        
        String assistanceHoursError = ValidationUtil.validateRangeUI(tfAssistanceHours.getText(), 0, 1000);
        if (assistanceHoursError != null) {
            lbErrorAssistanceHours.setText(assistanceHoursError);
            isValid = false;
        } else {
            lbErrorAssistanceHours.setText("");
        }
        
        String numberOfLanguagesError = ValidationUtil.validateRangeUI(tfNumberOfLanguages.getText(), 1, 10);
        if (numberOfLanguagesError != null) {
            lbErrorNumberOfLanguages.setText(numberOfLanguagesError);
            isValid = false;
        } else {
            lbErrorNumberOfLanguages.setText("");
        }

        String usernameError = ValidationUtil.validateUsernameUI(tfUsername.getText());
        if (usernameError != null) {
            lbErrorUsername.setText(usernameError);
            isValid = false;
        } else {
            lbErrorUsername.setText("");
        }

        if (!isEditing || !pfPassword.getText().isEmpty()) {
            String passwordError = ValidationUtil.validatePasswordUI(pfPassword.getText());
            if (passwordError != null) {
                lbErrorPassword.setText(passwordError);
                isValid = false;
            } else {
                lbErrorPassword.setText("");
            }
        }
        
        String airlineError = cbAirline.getValue() == null ? ValidationUtil.MSG_REQUIRED : null;
        if (airlineError != null) {
            lbErrorAirline.setText(airlineError);
            isValid = false;
        } else {
            lbErrorAirline.setText("");
        }

        return isValid;
    }
    
    private Assistant getAssistantFromForm() {
        Assistant assistant = new Assistant();
        
        if (isEditing && assistantToEdit != null) {
            assistant.setId(assistantToEdit.getId());
        } else {
            assistant.setId(IdGeneratorUtil.generateAssistantId());
        }
        
        assistant.setName(tfName.getText().trim());
        assistant.setAddress(tfAddress.getText().trim());
        assistant.setEmail(tfEmail.getText().trim());
        assistant.setAssistanceHours(Integer.parseInt(tfAssistanceHours.getText().trim()));
        assistant.setNumberOfLanguages(Integer.parseInt(tfNumberOfLanguages.getText().trim()));
        
        LocalDate localDate = dpBirthDate.getValue();
        Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        assistant.setBirthDate(birthDate);
        
        assistant.setGender(cbGender.getValue());
        assistant.setSalary(Double.parseDouble(tfSalary.getText().trim()));
        assistant.setUsername(tfUsername.getText().trim());
        
        if (!isEditing || !pfPassword.getText().isEmpty()) {
            String hashedPassword = PasswordUtil.hashPassword(pfPassword.getText());
            assistant.setPassword(hashedPassword);
        } else {
            assistant.setPassword(assistantToEdit.getPassword());
        }
        
        assistant.setAirline(cbAirline.getValue());
        assistant.setType("Assistant");
        
        return assistant;
    }
    
    private void saveAssistant() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            Assistant assistant = getAssistantFromForm();
            
            if (isEditing) {
                employeeDAO.update(assistant);
                DialogUtil.showInfoAlert(
                    "Actualización exitosa", 
                    "El asistente de vuelo ha sido actualizado correctamente."
                );
            } else {
                employeeDAO.save(assistant);
                DialogUtil.showInfoAlert(
                    "Registro exitoso", 
                    "El asistente de vuelo ha sido registrado correctamente."
                );
            }
            
            if (observer != null) {
                observer.operationSucceeded();
            }
            
            closeWindow();
            
        } catch (DuplicateResourceException e) {
            DialogUtil.showErrorAlert(
                "Error", 
                "Ya existe un empleado con ese nombre de usuario: " + e.getMessage()
            );
        } catch (ResourceNotFoundException e) {
            DialogUtil.showErrorAlert(
                "Error", 
                "No se encontró el asistente a actualizar: " + e.getMessage()
            );
        } catch (IOException e) {
            DialogUtil.showErrorAlert(
                "Error", 
                "Error al guardar los datos: " + e.getMessage()
            );
        } 
    }
    
    private void closeWindow() {
        ((Stage) tfName.getScene().getWindow()).close();
    }
}
    

