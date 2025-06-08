/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.observable.Notification;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.IdGeneratorUtil;
import javafxappaerolinea.utility.PasswordUtil;
import javafxappaerolinea.utility.ValidationUtil;

/**
 * FXML Controller class
 *
 * @author migue
 */
public class FXMLPilotFormController implements Initializable {

    @FXML
    private TextField tfName;
    @FXML
    private TextField tfAddress;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private ComboBox<String> cbGender;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfSalary;
    @FXML
    private TextField tfYearsExperience;
    @FXML
    private TextField tfFlightHours;
    @FXML
    private TextField tfLicenseType;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label lbErrorName;
    @FXML
    private Label lbErrorAddress;
    @FXML
    private Label lbErrorBirthDate;
    @FXML
    private Label lbErrorGender;
    @FXML
    private Label lbErrorEmail;
    @FXML
    private Label lbErrorSalary;
    @FXML
    private Label lbErrorYearsExperience;
    @FXML
    private Label lbErrorFlightHours;
    @FXML
    private Label lbErrorLicenseType;
    @FXML
    private Label lbErrorUsername;
    @FXML
    private Label lbErrorPassword;
    @FXML
    private ComboBox<Airline> cbAirline;
    @FXML
    private Label lbErrorAirline;
    private boolean isEditing;
    private Pilot pilotToEdit;
    private Notification observer;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbGender.setItems(FXCollections.observableArrayList(Arrays.asList("Masculino", "Femenino", "Otro")));
        loadAirlines();
    }    

    public void initializeData(boolean isEditing, Pilot pilotToEdit, Notification observer) {
        this.isEditing = isEditing;
        this.pilotToEdit = pilotToEdit;
        this.observer = observer;
        
        if (isEditing && pilotToEdit != null) {
            loadPilotData();
        }
    }
    
    @FXML
    private void btnSave(ActionEvent event) {
        if (validateFields()) {
            savePilot();
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
    
    private void loadPilotData() {
        if (pilotToEdit != null) {
            tfName.setText(pilotToEdit.getName());
            tfAddress.setText(pilotToEdit.getAddress());
            tfEmail.setText(pilotToEdit.getEmail());
            tfYearsExperience.setText(String.valueOf(pilotToEdit.getYearsExperience()));
            tfFlightHours.setText(String.valueOf(pilotToEdit.getFlightHours()));
            tfLicenseType.setText(pilotToEdit.getLicenseType());
            
            // Convertir Date a LocalDate para el DatePicker
            Date birthDate = pilotToEdit.getBirthDate();
            if (birthDate != null) {
                LocalDate localDate = birthDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dpBirthDate.setValue(localDate);
            }
            
            cbGender.setValue(pilotToEdit.getGender());
            tfSalary.setText(String.valueOf(pilotToEdit.getSalary()));
            tfUsername.setText(pilotToEdit.getUsername());
            
            // Seleccionar la aerolínea correcta
            if (pilotToEdit.getAirline() != null) {
                for (Airline airline : cbAirline.getItems()) {
                    if (airline.getIdentificationNumber()== pilotToEdit.getAirline().getIdentificationNumber()) {
                        cbAirline.setValue(airline);
                        break;
                    }
                }
            }
            
            // No cargar la contraseña por seguridad
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
        
        String yearsExperienceError = ValidationUtil.validateRangeUI(tfYearsExperience.getText(), 0, 50);
        if (yearsExperienceError != null) {
            lbErrorYearsExperience.setText(yearsExperienceError);
            isValid = false;
        } else {
            lbErrorYearsExperience.setText("");
        }
        
        String flightHoursError = ValidationUtil.validatePositiveUI(tfFlightHours.getText());
        if (flightHoursError != null) {
            lbErrorFlightHours.setText(flightHoursError);
            isValid = false;
        } else {
            lbErrorFlightHours.setText("");
        }
        
        String licenseTypeError = ValidationUtil.validateNotEmptyUI(tfLicenseType.getText());
        if (licenseTypeError != null) {
            lbErrorLicenseType.setText(licenseTypeError);
            isValid = false;
        } else {
            lbErrorLicenseType.setText("");
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
    
    private Pilot getPilotFromForm() {
        Pilot pilot = new Pilot();
        
        if (isEditing && pilotToEdit != null) {
            pilot.setId(pilotToEdit.getId());
        } else {
            pilot.setId(IdGeneratorUtil.generatePilotId());
        }
        
        pilot.setName(tfName.getText().trim());
        pilot.setAddress(tfAddress.getText().trim());
        pilot.setEmail(tfEmail.getText().trim());
        pilot.setYearsExperience(Integer.parseInt(tfYearsExperience.getText().trim()));
        pilot.setFlightHours(Double.parseDouble(tfFlightHours.getText().trim()));
        pilot.setLicenseType(tfLicenseType.getText().trim());
        
        // Convertir LocalDate a Date
        LocalDate localDate = dpBirthDate.getValue();
        Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        pilot.setBirthDate(birthDate);
        
        pilot.setGender(cbGender.getValue());
        pilot.setSalary(Double.parseDouble(tfSalary.getText().trim()));
        pilot.setUsername(tfUsername.getText().trim());
        
        // Manejar la contraseña
        if (!isEditing || !pfPassword.getText().isEmpty()) {
            String hashedPassword = PasswordUtil.hashPassword(pfPassword.getText());
            pilot.setPassword(hashedPassword);
        } else {
            // Mantener la contraseña existente
            pilot.setPassword(pilotToEdit.getPassword());
        }
        
        pilot.setAirline(cbAirline.getValue());
        pilot.setType("Pilot");
        
        return pilot;
    }
    
    private void savePilot() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            Pilot pilot = getPilotFromForm();
            
            if (isEditing) {
                employeeDAO.update(pilot);
                DialogUtil.showInfoAlert(
                    "Actualización exitosa", 
                    "El piloto ha sido actualizado correctamente."
                );
            } else {
                employeeDAO.save(pilot);
                DialogUtil.showInfoAlert(
                    "Registro exitoso", 
                    "El piloto ha sido registrado correctamente."
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
                "No se encontró el piloto a actualizar: " + e.getMessage()
            );
        } catch (IOException e) {
            DialogUtil.showErrorAlert(
                "Error", 
                "Error al guardar los datos: " + e.getMessage()
            );
        } catch (Exception e) {
            DialogUtil.showErrorAlert(
                "Error", 
                "Ocurrió un error inesperado: " + e.getMessage()
            );
        }
    }
    private void closeWindow() {
        ((Stage) tfName.getScene().getWindow()).close();
    }
}
