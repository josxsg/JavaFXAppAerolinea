/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package javafxappaerolinea;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author zenbook i5
 */
public class JavaFXAppAerolinea extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent vista  = FXMLLoader.load(getClass().getResource("view/FXMLLogin.fxml"));
            Scene escenaInicioSesion = new Scene(vista);
            
            primaryStage.setScene(escenaInicioSesion);
            primaryStage.setTitle("Inicio de sesión");
            primaryStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
