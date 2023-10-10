package com.example.pillmasterjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class MedicationFormController {
    public TextField medicationField;
    public ToggleButton dailyButton;
    public ToggleGroup scheduleToggle;
    public ToggleButton twiceButton;
    public ToggleButton thriceButton;
    public TextField quantityField;
    public Button cancelButton;
    public Button confirmButton;

    private Medication medication;
    private Boolean dataReady;

    public Medication getMedication() {
        return medication;
    }

    public Boolean isDataReady() {
        return dataReady;
    }

    @FXML
    public void initialize() {
        dailyButton.setUserData(Medication.Schedule.DAILY);
        twiceButton.setUserData(Medication.Schedule.TWICE);
        thriceButton.setUserData(Medication.Schedule.THRICE);
    }

    @FXML
    public void onConfirmClick(ActionEvent e) {
        System.out.println("Entry confirmed!");
        medication = new Medication(
                medicationField.getText(),
                Integer.parseInt(quantityField.getText()),
                (Medication.Schedule) scheduleToggle.getSelectedToggle().getUserData()
        );
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
    }

    @FXML
    public void onCancelClick(ActionEvent e) {
        System.out.println("Entry canceled!");
        medication = null;
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
    }
}
