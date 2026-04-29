package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import org.example.network.rpcprotocol.TeledonServerRpcProxy;
import org.example.services.ITeledonServices;

import java.io.IOException;
import java.util.Properties;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties clientProps = new Properties();
        try {
            var inputStream = StartClient.class.getResourceAsStream("/client.properties");
            if (inputStream == null) {
                System.err.println("ATENTIE: Fisierul client.properties nu a fost gasit in resources!");
                System.err.println("Vom folosi datele default (localhost:55555).");
            } else {
                clientProps.load(inputStream);
                System.out.println("Client properties loaded.");
            }
        } catch (IOException e) {
            System.err.println("Eroare la citirea client.properties: " + e.getMessage());
        }

        String serverIP = clientProps.getProperty("server.ip", "localhost").trim();
        int serverPort = 55555;
        try {
            String portString = clientProps.getProperty("server.port");
            if (portString != null) {
                serverPort = Integer.parseInt(portString.trim());
            }
        } catch (NumberFormatException ex) {
            System.err.println("Port gresit in fisier, folosim 55555: " + ex.getMessage());
        }

        System.out.println("Incercare de conectare la serverul: " + serverIP + " pe portul " + serverPort);

        ITeledonServices server = new TeledonServerRpcProxy(serverIP, serverPort);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/login-view.fxml"));
            Parent root = loader.load();

            LoginController ctrl = loader.getController();
            ctrl.setService(server);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Teledon - Logare");
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Eroare fatala la pornirea interfetei grafice: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}