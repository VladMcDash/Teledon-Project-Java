package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.domain.Volunteer;
import org.example.services.ITeledonServices;

import java.awt.event.ActionEvent;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    private ITeledonServices service;

    public void setService(ITeledonServices service) {
        this.service = service;
    }

    @FXML
    public void handleLogin(javafx.event.ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/main-view.fxml"));
            Parent root = loader.load();
            MainController mainCtrl = loader.getController();

            Volunteer loggedUser = service.login(username, password, mainCtrl);

            mainCtrl.setService(service);
            mainCtrl.setLoggedUser(loggedUser);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch (Exception e) {
            System.out.println("Eroare la login: " + e.getMessage());
        }
    }

    private void switchToMainView() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/main-view.fxml"));
        Scene scene = new Scene(loader.load());

        MainController mainCtrl = loader.getController();
        mainCtrl.setService(service);

        Stage stage = new Stage();
        stage.setTitle("Sistem Gestiune Teledon - Voluntar");
        stage.setScene(scene);
        stage.show();

        ((Stage) usernameField.getScene().getWindow()).close();
    }
}