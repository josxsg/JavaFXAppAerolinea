package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
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
import javafxappaerolinea.exception.ValidationException;
import javafxappaerolinea.observable.Notification;
import javafxappaerolinea.model.dao.EmployeeDAO;
import javafxappaerolinea.model.pojo.Administrative;
import javafxappaerolinea.utility.DateUtil;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.IdGeneratorUtil;
import javafxappaerolinea.utility.PasswordUtil;
import javafxappaerolinea.utility.ValidationUtil;


public class FXMLAdminFormController implements Initializable {

    @FXML
    private TextField tfName;
    @FXML
    private TextField tfAddress;
    @FXML
    private TextField tfDepartment;
    @FXML
    private TextField tfWorkHours;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private ComboBox<String> cbGender;
    @FXML
    private TextField tfSalary;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label lbNameError;
    @FXML
    private Label lbAddressError;
    @FXML
    private Label lbDepartmentError;
    @FXML
    private Label lbWorkHoursError;
    @FXML
    private Label lbBirthDateError;
    @FXML
    private Label lbGenderError;
    @FXML
    private Label lbSalaryError;
    @FXML
    private Label lbUsernameError;
    @FXML
    private Label lbPasswordError;
    
    private boolean isEditing;
    private Administrative adminToEdit;
    private Notification observer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbGender.setItems(FXCollections.observableArrayList(Arrays.asList("Masculino", "Femenino", "Otro")));
    }    
    
    public void initializeData(boolean isEditing, Administrative adminToEdit, Notification observer) {
        this.isEditing = isEditing;
        this.adminToEdit = adminToEdit;
        this.observer = observer;
        
        if (isEditing && adminToEdit != null) {
            loadAdminData();
        }
    }

    @FXML
    private void btnSave(ActionEvent event) {
        if (validateFields()) {
            saveAdministrative();
        }
    }

    @FXML
    private void btnCancel(ActionEvent event) {
        closeWindow();
    }
    
    private void loadAdminData() {
        if (adminToEdit != null) {
            tfName.setText(adminToEdit.getName());
            tfAddress.setText(adminToEdit.getAddress());
            tfDepartment.setText(adminToEdit.getDepartment());
            tfWorkHours.setText(String.valueOf(adminToEdit.getWorkHours()));
            
            Date birthDate = adminToEdit.getBirthDate();
            if (birthDate != null) {
                LocalDate localDate = birthDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dpBirthDate.setValue(localDate);
            }
            
            cbGender.setValue(adminToEdit.getGender());
            tfSalary.setText(String.valueOf(adminToEdit.getSalary()));
            tfUsername.setText(adminToEdit.getUsername());
            
            pfPassword.setVisible(false);
        }
    }
    
    
    
    private boolean validateFields() {
        boolean isValid = true;

        String nameError = ValidationUtil.validateNotEmptyUI(tfName.getText());
        if (nameError != null) {
            lbNameError.setText(nameError);
            isValid = false;
        } else {
            lbNameError.setText("");
        }
        
        String adressError = ValidationUtil.validateNotEmptyUI(tfAddress.getText());
        if (adressError != null) {
            lbAddressError.setText(adressError);
            isValid = false;
        } else {
            lbAddressError.setText("");
        }
        
        String departmentError = ValidationUtil.validateNotEmptyUI(tfDepartment.getText());
        if (departmentError != null) {
            lbDepartmentError.setText(departmentError);
            isValid = false;
        } else {
            lbDepartmentError.setText("");
        }

        String workHoursError = ValidationUtil.validateRangeUI(tfWorkHours.getText(), 1, 24);
        if (workHoursError != null) {
            lbWorkHoursError.setText(workHoursError);
            isValid = false;
        } else {
            lbWorkHoursError.setText("");
        }
        
        String birthDateError = ValidationUtil.validateAgeRangeUI(dpBirthDate.getValue(), 18, 70);
        if (birthDateError != null) {
            lbBirthDateError.setText(birthDateError);
            isValid = false;
        } else {
            lbBirthDateError.setText("");
        }
        
        String genderError = cbGender.getValue() == null ? ValidationUtil.MSG_REQUIRED : null;
        if (genderError != null) {
            lbGenderError.setText(genderError);
            isValid = false;
        } else {
            lbGenderError.setText("");
        }

        String salaryError = ValidationUtil.validatePositiveUI(tfSalary.getText());
        if (salaryError != null) {
            lbSalaryError.setText(salaryError);
            isValid = false;
        } else {
            lbSalaryError.setText("");
        }

        String usernameError = ValidationUtil.validateUsernameUI(tfUsername.getText());
        if (usernameError != null) {
            lbUsernameError.setText(usernameError);
            isValid = false;
        } else {
            lbUsernameError.setText("");
        }

        if (!isEditing || !pfPassword.getText().isEmpty()) {
            String passwordError = ValidationUtil.validatePasswordUI(pfPassword.getText());
            if (passwordError != null) {
                lbPasswordError.setText(passwordError);
                isValid = false;
            } else {
                lbPasswordError.setText("");
            }
        }

        return isValid;
    }
    
    private Administrative getAdministrativeFromForm() {
        Administrative admin = new Administrative();
        
        if (isEditing && adminToEdit != null) {
            admin.setId(adminToEdit.getId());
        } else {
            admin.setId(IdGeneratorUtil.generateAdministrativeId());
        }
        
        admin.setName(tfName.getText().trim());
        admin.setAddress(tfAddress.getText().trim());
        admin.setDepartment(tfDepartment.getText().trim());
        admin.setWorkHours(Integer.parseInt(tfWorkHours.getText().trim()));
        
        LocalDate localDate = dpBirthDate.getValue();
        Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        admin.setBirthDate(birthDate);
        
        admin.setGender(cbGender.getValue());
        admin.setSalary(Double.parseDouble(tfSalary.getText().trim()));
        admin.setUsername(tfUsername.getText().trim());
        
        if (!isEditing || !pfPassword.getText().isEmpty()) {
            String hashedPassword = PasswordUtil.hashPassword(pfPassword.getText());
            admin.setPassword(hashedPassword);
        } else {
            admin.setPassword(adminToEdit.getPassword());
        }
        admin.setType("Administrative");
        
        return admin;
    }
    
    private void saveAdministrative() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            Administrative admin = getAdministrativeFromForm();
            
            if (isEditing) {
                employeeDAO.update(admin);
                DialogUtil.showInfoAlert(
                    "Actualización exitosa", 
                    "El administrativo ha sido actualizado correctamente."
                );
            } else {
                employeeDAO.save(admin);
                DialogUtil.showInfoAlert(
                    "Registro exitoso", 
                    "El administrativo ha sido registrado correctamente."
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
                "No se encontró el administrativo a actualizar: " + e.getMessage()
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
