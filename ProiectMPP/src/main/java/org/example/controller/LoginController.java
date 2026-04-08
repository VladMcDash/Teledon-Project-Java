package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.domain.Volunteer;
import org.example.service.TeledonService;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    private TeledonService service;

    public void setService(TeledonService service) {
        this.service = service;
    }

    @FXML
    public void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();
        try {
            Volunteer v = service.login(user, pass);
            if (v != null) {
                switchToMainView();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
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