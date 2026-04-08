package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.domain.*;
import org.example.service.TeledonService;
import java.util.List;

public class MainController {
    private TeledonService service;

    @FXML private TableView<CharityCase> casesTable;
    @FXML private TableColumn<CharityCase, String> nameCol;
    @FXML private TableColumn<CharityCase, Double> amountCol;
    @FXML private TextField donorNameField, addressField, phoneField, amountField, searchField;
    @FXML private ListView<Donor> donorsList;

    public void setService(TeledonService service) {
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

    private void loadData() {
        casesTable.setItems(FXCollections.observableArrayList(service.getAllCases()));
    }

    @FXML
    public void handleSearch() {
        List<Donor> results = service.searchDonors(searchField.getText());
        donorsList.setItems(FXCollections.observableArrayList(results));
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
            if (selectedCase == null) throw new Exception("Selectați un caz caritabil!");

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
    public void handleLogout() {
        service.logout();
        ((Stage) casesTable.getScene().getWindow()).close();
    }

    private void clearFields() {
        donorNameField.clear(); addressField.clear(); phoneField.clear(); amountField.clear();
    }
}