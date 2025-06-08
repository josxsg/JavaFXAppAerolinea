package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.model.dao.CustomerDAO;
import javafxappaerolinea.model.pojo.Customer;
import javafxappaerolinea.utility.DialogUtil;
import javafxappaerolinea.utility.ValidationUtil;


public class FXMLAddCustomerController implements Initializable {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private TextField txtNationality;
    
    private FXMLCustomersController customersController;
    private CustomerDAO customerDAO;

    /**
     * Inicializa el controlador
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerDAO = new CustomerDAO();
        // Establecer fecha por defecto (hoy)
        dpBirthDate.setValue(LocalDate.now());
    }    

   
    public void setCustomersController(FXMLCustomersController controller) {
        this.customersController = controller;
    }
    

    @FXML
    private void handleSave(ActionEvent event) {
        if (validateFields()) {
            Customer customer = new Customer();
            customer.setName(txtName.getText().trim());
            customer.setEmail(txtEmail.getText().trim());
            LocalDate localDate = dpBirthDate.getValue();
            if (localDate != null) {
                Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                customer.setBirthDate(birthDate);
            }
            customer.setNationality(txtNationality.getText().trim());
            
            try {
                customerDAO.save(customer);
                DialogUtil.showInfoAlert("Cliente guardado", 
                        "El cliente se ha registrado correctamente.");
                
                if (customersController != null) {
                    customersController.refreshCustomers();
                }
                
                closeWindow();
            } catch (DuplicateResourceException e) {
                DialogUtil.showErrorAlert("Cliente duplicado", 
                        "Ya existe un cliente con el correo electrónico proporcionado.");
            } catch (IOException e) {
                DialogUtil.showErrorAlert("Error en la base de datos", 
                        "Ocurrió un error al intentar guardar el cliente: " + e.getMessage());
            }
        }
    }

    
    @FXML
    private void handleCancel(ActionEvent event) {
        boolean confirm = DialogUtil.showConfirmationDialog("Confirmar", 
                "¿Está seguro que desea cancelar? Los datos no guardados se perderán.");
        
        if (confirm) {
            closeWindow();
        }
    }
    
  
    private boolean validateFields() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder("Por favor corrija los siguientes errores:\n");
        
        String nameError = ValidationUtil.validateNotEmptyUI(txtName.getText());
        if (nameError != null) {
            errorMessage.append("- El nombre ").append(nameError.toLowerCase()).append("\n");
            isValid = false;
        }
        
        String emailError = ValidationUtil.validateEmailUI(txtEmail.getText());
        if (emailError != null) {
            errorMessage.append("- El email ").append(emailError.toLowerCase()).append("\n");
            isValid = false;
        }
        
        String dateError = ValidationUtil.validateDateNotNullUI(dpBirthDate.getValue());
        if (dateError != null) {
            errorMessage.append("- La fecha de nacimiento ").append(dateError.toLowerCase()).append("\n");
            isValid = false;
        } else {
            String pastDateError = ValidationUtil.validatePastDateUI(dpBirthDate.getValue());
            if (pastDateError != null) {
                errorMessage.append("- La fecha de nacimiento ").append(pastDateError.toLowerCase()).append("\n");
                isValid = false;
            }
        }
        
        String nationalityError = ValidationUtil.validateNotEmptyUI(txtNationality.getText());
        if (nationalityError != null) {
            errorMessage.append("- La nacionalidad ").append(nationalityError.toLowerCase()).append("\n");
            isValid = false;
        }
        
        if (!isValid) {
            DialogUtil.showWarningAlert("Datos incompletos o inválidos", errorMessage.toString());
        }
        
        return isValid;
    }
    
   
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}