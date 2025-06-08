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
import javafx.stage.Stage;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.service.SessionManager;

public class FXMLPrincipalPilotController implements Initializable {
    
    @FXML
    private Label lbWelcome;
    
    private Pilot loggedPilot;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadUserInfo();
    }
    
    private void loadUserInfo() {
        loggedPilot = (Pilot) SessionManager.getInstance().getCurrentUser();
        if (loggedPilot != null) {
            lbWelcome.setText("Bienvenido, " + loggedPilot.getName());
        }
    }
    
    @FXML
    private void btnLogout(ActionEvent event) {
        SessionManager.getInstance().logout();
        closeWindow();
        openLoginWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) lbWelcome.getScene().getWindow();
        stage.close();
    }
    
    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLLogin.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException ex) {
            System.err.println("Error al abrir ventana de login: " + ex.getMessage());
        }
    }
}