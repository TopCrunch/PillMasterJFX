package com.example.pillmasterjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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
    public Label hourLabel;
    public Label minuteLabel;
    public Label periodLabel;

    private Medication medication;
    private Boolean dataReady;
    private int hourCount = 0;
    private int minuteCount = 0;
    private boolean isPM = false;

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

    private void updateTimeLabels() {
        hourLabel.setText(String.valueOf(hourCount));
        minuteLabel.setText(String.valueOf(minuteCount));
        if(isPM) {
            periodLabel.setText("PM");
        } else {
            periodLabel.setText("AM");
        }
    }

    @FXML
    public void onUpHourButtonClick(ActionEvent e) {
        if(hourCount >= 12) {
            hourCount = 1;
        } else {
            hourCount++;
        }
        if(hourCount == 12) {
            isPM = !isPM;
        }
        updateTimeLabels();
    }
    @FXML
    public void onDownHourButtonClick(ActionEvent e) {
        if(hourCount <= 0) {
            hourCount = 23;
        } else {
            hourCount--;
        }
        updateTimeLabels();
    }
    @FXML
    public void onUpMinuteButtonClick(ActionEvent e) {
        if(minuteCount >= 50) {
            minuteCount = 0;
        } else {
            minuteCount += 10;
        }
        updateTimeLabels();
    }
    @FXML
    public void onDownMinuteButtonClick(ActionEvent e) {
        if(minuteCount <= 0) {
            minuteCount = 50;
        } else {
            minuteCount -= 10;
        }
        updateTimeLabels();

    }
}
