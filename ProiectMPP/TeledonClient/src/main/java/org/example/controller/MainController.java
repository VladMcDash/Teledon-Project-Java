package org.example.controller;

import javafx.collections.ObservableList;
import org.example.domain.CharityCase;
import org.example.domain.Donor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.List;

import org.example.domain.Volunteer;
import org.example.services.ITeledonServices;
import org.example.services.ITeledonObserver;
import org.example.services.TeledonException;
import javafx.application.Platform;

public class MainController implements ITeledonObserver {
    private ITeledonServices service;

    public void setService(ITeledonServices service) {
        this.service = service;
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        donorsList.setCellFactory(param -> new ListCell<Donor>() {
            @Override
            protected void updateItem(Donor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + item.getPhoneNumber());
                }
            }
        });
        loadData();
    }
    private Volunteer loggedUser;

    public void setLoggedUser(Volunteer user) {
        this.loggedUser = user;
    }

    @FXML private TableView<CharityCase> casesTable;
    @FXML private TableColumn<CharityCase, String> nameCol;
    @FXML private TableColumn<CharityCase, Double> amountCol;
    @FXML private TextField donorNameField, addressField, phoneField, amountField, searchField;
    @FXML private ListView<Donor> donorsList;

    private void loadData() {
        try {
        casesTable.setItems(FXCollections.observableArrayList(service.getAllCases()));
        } catch (TeledonException e) {
        System.out.println("Eroare la preluarea cazurilor de la server: " + e.getMessage());
         }
    }
    private void loadCases() {
        try {
            List<CharityCase> cases = service.getAllCases();
            casesTable.setItems(FXCollections.observableArrayList(cases));
        } catch (TeledonException e) {
            System.err.println("Eroare la refresh date: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        try {
        List<Donor> results = service.searchDonors(searchField.getText());
        donorsList.setItems(FXCollections.observableArrayList(results));
        } catch (TeledonException e) {
        System.out.println("Eroare la cautarea donatorului: " + e.getMessage());
        }

    }

    @FXML
    public void handleDonorSelected() {
        Donor selected = donorsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            donorNameField.setText(selected.getName());
            addressField.setText(selected.getAddress());
            phoneField.setText(selected.getPhoneNumber());
        }
    }

    @FXML
    public void handleAddDonation() {
        try {
            CharityCase selectedCase = casesTable.getSelectionModel().getSelectedItem();
            if (selectedCase == null) throw new Exception("Selectati un caz caritabil!");

            String name = donorNameField.getText();
            String addr = addressField.getText();
            String tel = phoneField.getText();
            double sum = Double.parseDouble(amountField.getText());

            service.addDonation(name, addr, tel, selectedCase.getId(), sum);

            loadData();
            clearFields();
            new Alert(Alert.AlertType.INFORMATION, "Donatie salvata cu succes!").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    public void handleLogout() {try {
        service.logout(loggedUser, this);
    } catch (TeledonException e) {
        System.out.println("Eroare la logout: " + e.getMessage());
    }
        ((Stage) casesTable.getScene().getWindow()).close();
    }

    private void clearFields() {
        donorNameField.clear(); addressField.clear(); phoneField.clear(); amountField.clear();
    }
    @Override
    public void donationAdded(CharityCase updatedCase) {
        if (updatedCase == null) return;

        Platform.runLater(() -> {
            for (int i = 0; i < casesTable.getItems().size(); i++) {
                CharityCase currentCase = casesTable.getItems().get(i);
                if (currentCase.getId().equals(updatedCase.getId())) {
                    casesTable.getItems().set(i, updatedCase);
                    break;
                }
            }
            casesTable.refresh();
        });
    }
    @Override
    public void donorUpdated(Donor updatedDonor) {
        if (updatedDonor == null) return;

        Platform.runLater(() -> {
            ObservableList<Donor> donors = donorsList.getItems();
            for (int i = 0; i < donors.size(); i++) {
                Donor current = donors.get(i);
                if (current.getId().equals(updatedDonor.getId())) {
                    donors.set(i, updatedDonor);
                    break;
                }
            }
            donorsList.refresh();
        });
    }
    @FXML
    public void handleUpdateDonor(){
        try {
            Donor selected = donorsList.getSelectionModel().getSelectedItem();
            if (selected == null) throw new Exception("Selectati un donator pentru update!");

            String name = donorNameField.getText();
            String addr = addressField.getText();
            String tel = phoneField.getText();

            service.addDonation(name, addr, tel, 0L, 0.0);

            loadData();
            clearFields();
            new Alert(Alert.AlertType.INFORMATION, "Donator actualizat cu succes!").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}