package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import org.example.repository.*;
import org.example.service.TeledonService;
import java.io.FileReader;
import java.util.Properties;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties props = new Properties();
        props.load(new FileReader("db.properties"));

        TeledonService service = new TeledonService(
                new VolunteerJdbcRepository(props),
                new DonorJdbcRepository(props),
                new CharityCaseJdbcRepository(props),
                new DonationJdbcRepository(props)
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/login-view.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController loginCtrl = loader.getController();
        loginCtrl.setService(service);

        primaryStage.setTitle("Teledon - Autentificare");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}