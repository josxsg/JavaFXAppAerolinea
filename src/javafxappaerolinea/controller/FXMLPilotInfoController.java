package javafxappaerolinea.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafxappaerolinea.model.pojo.Pilot;
import javafxappaerolinea.service.SessionManager;

public class FXMLPilotInfoController implements Initializable {
    
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
    private Label lbYearsExperience;
    
    @FXML
    private Label lbEmail;
    
    @FXML
    private Label lbFlightHours;
    
    @FXML
    private Label lbLicenseType;
    
    private Pilot loggedPilot;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPilotInfo();
    }
    
    private void loadPilotInfo() {
        try {
            loggedPilot = (Pilot) SessionManager.getInstance().getCurrentUser();
            if (loggedPilot != null) {
                lbId.setText(String.valueOf(loggedPilot.getId()));
                lbName.setText(loggedPilot.getName());
                lbAddress.setText(loggedPilot.getAddress());
                lbBirthDate.setText(dateFormat.format(loggedPilot.getBirthDate()));
                lbGender.setText(loggedPilot.getGender());
                lbSalary.setText(String.format("$%.2f", loggedPilot.getSalary()));
                lbYearsExperience.setText(String.valueOf(loggedPilot.getYearsExperience()));
                lbEmail.setText(loggedPilot.getEmail());
                lbFlightHours.setText(String.format("%.2f", loggedPilot.getFlightHours()));
                lbLicenseType.setText(loggedPilot.getLicenseType());
            }
        } catch (NullPointerException | ClassCastException | IllegalArgumentException ex) {
            showAlert("Error", 
                    "No se pudo cargar la información del asistente: " + ex.getMessage(), 
                    Alert.AlertType.ERROR);
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
