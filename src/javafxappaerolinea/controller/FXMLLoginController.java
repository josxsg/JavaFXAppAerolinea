package javafxappaerolinea.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxappaerolinea.JavaFXAppAerolinea;
import javafxappaerolinea.exception.AuthenticationException;
import javafxappaerolinea.model.pojo.Administrative;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Employee;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.service.AuthenticationService;
import javafxappaerolinea.service.SessionManager;
import static javafxappaerolinea.utility.DialogUtil.showErrorAlert;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLLoginController implements Initializable {

    @FXML
    private TextField tfUser;
    @FXML
    private Label lbUserError;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label lbPasswordError;
    
    private AuthenticationService authService;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        authService = new AuthenticationService();
        setupFieldListeners();
    }    

    @FXML
    private void btnClicIniciarSesion(ActionEvent event) {
        clearErrorMessages();
        if (!validateFields())
            return;
        
        try {
            Employee employee = authenticateUser();
            SessionManager.getInstance().setCurrentUser(employee);
            openAppropriateWindow(employee);

        } catch (AuthenticationException e) {
            showErrorAlert("Error de autenticación", e.getMessage());
        } catch (Exception e) {
            showErrorAlert("Error", "Ocurrió un error al intentar iniciar sesión: " + e.getMessage());
        }
    }
    
    private void setupFieldListeners() {
        tfUser.textProperty().addListener((observable, oldValue, newValue) -> {
            lbUserError.setVisible(false);
        });
        
        pfPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            lbPasswordError.setVisible(false);
        });
    }
    
    private void clearErrorMessages() {
        lbUserError.setText("");
        lbPasswordError.setText("");
    }
    
    private boolean validateFields() {
        boolean isValid = true;
        
        String username = tfUser.getText().trim();
        if (username.isEmpty()) {
            lbUserError.setText("*Usuario obligatorio");
            isValid = false;
        }
        
        String password = pfPassword.getText();
        if (password.isEmpty()) {
            lbPasswordError.setText("Contraseña obligatoria");
            isValid = false;
        }
        return isValid;
    }
    
    private Employee authenticateUser() throws AuthenticationException {
        String username = tfUser.getText().trim();
        String password = pfPassword.getText();
        return authService.login(username, password);
    }
    
    private void openAppropriateWindow(Employee employee) {
        if (employee instanceof Administrative) {
            goToMainScreen("view/FXMLPrincipalAdmin.fxml", "UniAir - Panel Administrativo", employee);
        } else if (employee instanceof Pilot) {
            goToMainScreen("view/FXMLPrincipalPilot.fxml", "UniAir - Panel de Piloto", employee);
        } else if (employee instanceof Assistant) {
            goToMainScreen("view/FXMLPrincipalAssistant.fxml", "UniAir - Panel de Asistente", employee);
        } 
    }
    
    private void goToMainScreen(String fxmlPath, String windowTitle, Employee employee) {
    try {
        System.out.println("Intentando cargar: " + fxmlPath);
        Stage primaryStage = (Stage) tfUser.getScene().getWindow();
        
        // Verificar que la ruta del FXML sea accesible
        URL fxmlUrl = JavaFXAppAerolinea.class.getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new IOException("No se pudo encontrar el archivo FXML en la ruta: " + fxmlPath);
        }
        System.out.println("URL del FXML: " + fxmlUrl);
        
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        
        // Verificar que el controlador se haya cargado correctamente
        Object controller = loader.getController();
        System.out.println("Controlador cargado: " + (controller != null ? controller.getClass().getName() : "null"));
        
        loadEmployeeInWindow(employee, loader);
        
        Scene primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);
        primaryStage.setTitle(windowTitle);
        primaryStage.show();
    } catch (IOException ex) {
        ex.printStackTrace();
        showErrorAlert("Error", "No se pudo abrir la ventana principal: " + ex.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        showErrorAlert("Error inesperado", "Error al cargar la ventana: " + e.getMessage());
    }
}
    
    private void loadEmployeeInWindow(Employee employee, FXMLLoader loader) {
    try {
        if (employee instanceof Administrative) {
            FXMLPrincipalAdminController controller = loader.getController();
            controller.initInformation((Administrative) employee);
        } else if (employee instanceof Pilot) {
            // No hacemos nada, el controlador ya carga la información desde SessionManager
        } else if (employee instanceof Assistant) {
            // No hacemos nada, el controlador ya carga la información desde SessionManager
        }
    } catch (Exception e) {
        showErrorAlert("Error", "Error al inicializar la ventana: " + e.getMessage());
        e.printStackTrace();
    }
}
}