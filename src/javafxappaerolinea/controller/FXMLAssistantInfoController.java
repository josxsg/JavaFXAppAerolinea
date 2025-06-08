package javafxappaerolinea.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.service.SessionManager;

public class FXMLAssistantInfoController implements Initializable {
    
    @FXML
    private Label lbId;
    
    @FXML
    private Label lbName;
    
    @FXML
    private Label lbAddress;
    
    @FXML
    private Label lbBirthDate;
    
    @FXML
    private Label lbGender;
    
    @FXML
    private Label lbSalary;
    
    @FXML
    private Label lbEmail;
    
    @FXML
    private Label lbAssistanceHours;
    
    @FXML
    private Label lbNumberOfLanguages;
    
    private Assistant loggedAssistant;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAssistantInfo();
    }
    
    private void loadAssistantInfo() {
        try {
            loggedAssistant = (Assistant) SessionManager.getInstance().getCurrentUser();
            if (loggedAssistant != null) {
                lbId.setText(String.valueOf(loggedAssistant.getId()));
                lbName.setText(loggedAssistant.getName());
                lbAddress.setText(loggedAssistant.getAddress());
                lbBirthDate.setText(dateFormat.format(loggedAssistant.getBirthDate()));
                lbGender.setText(loggedAssistant.getGender());
                lbSalary.setText(String.format("$%.2f", loggedAssistant.getSalary()));
                lbEmail.setText(loggedAssistant.getEmail());
                lbAssistanceHours.setText(String.valueOf(loggedAssistant.getAssistanceHours()));
                lbNumberOfLanguages.setText(String.valueOf(loggedAssistant.getNumberOfLanguages()));
            }
        } catch (Exception ex) {
            showAlert("Error", "No se pudo cargar la información del asistente: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void btnEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxappaerolinea/view/FXMLEditAssistantProfile.fxml"));
            Parent root = loader.load();
            
            FXMLEditAssistantProfileController controller = loader.getController();
            controller.setAssistant(loggedAssistant);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Perfil");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            loadAssistantInfo();
        } catch (Exception ex) {
            showAlert("Error", "No se pudo abrir la ventana de edición: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}