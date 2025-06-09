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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafxappaerolinea.JavaFXAppAerolinea;
import javafxappaerolinea.model.pojo.Administrative;
import javafxappaerolinea.utility.DialogUtil;


public class FXMLPrincipalAdminController implements Initializable {

    @FXML
    private Label lblUsername;
    @FXML
    private AnchorPane apCentral;
    private Administrative administrative;
    @FXML
    private Label lbWindowName;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public void initInformation (Administrative administrative) {
        this.administrative = administrative;
        loadInformation();
    }

    
    @FXML
    private void logOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource("view/FXMLLogin.fxml"));
            Parent root = loader.load();
            Scene primaryScene = new Scene(root);
            Stage primaryStage = (Stage) lblUsername.getScene().getWindow();
            primaryStage.setScene(primaryScene);
            primaryStage.setTitle("Inicio de sesión");
            primaryStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void openAirlinesWindow(ActionEvent event) {
        loadScene("view/FXMLAirline.fxml");
        lbWindowName.setText("Gestión de Aerolíneas");
    }

    @FXML
    private void openAirplanesWindow(ActionEvent event) {
        loadScene("view/FXMLAirplane.fxml");
        lbWindowName.setText("Gestión de Aviones");
    }

    @FXML
    private void openFlightsWindow(ActionEvent event) {
        loadScene("view/FXMLFlight.fxml");
        lbWindowName.setText("Gestión de Vuelos");
    }

    @FXML
    private void openRegisterClientWindow(ActionEvent event) {
        loadScene("view/FXMLCustomers.fxml");
        lbWindowName.setText("Gestión de Clientes");
    }

    @FXML
    private void openBuyTicketWindow(ActionEvent event) {
        loadScene("view/FXMLTickets.fxml");
        lbWindowName.setText("");
    }

    @FXML
    private void openAdminStaffWindow(ActionEvent event) {
        loadScene("view/FXMLAdministrative.fxml");
        lbWindowName.setText("Gestión de Administrativos");
    }

    @FXML
    private void openPilotsWindow(ActionEvent event) {
        loadScene("view/FXMLPilot.fxml");
        lbWindowName.setText("Gestión de Pilotos");
    }

    @FXML
    private void openFlightAttendantsWindow(ActionEvent event) {
        loadScene("view/FXMLAssistant.fxml");
        lbWindowName.setText("Gestión de Asistentes de Vuelo");
    }
    
    private void loadInformation() {
        if (administrative != null) {
            lblUsername.setText(administrative.getName());
        }
    }
    
    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(JavaFXAppAerolinea.class.getResource(fxmlPath));
            Parent root = loader.load();
            apCentral.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
        } catch (IOException ex) {
            DialogUtil.showErrorAlert("Error", "No se pudo abrir la ventana: " + ex.getMessage());
        }
    }
    
}
