package com.example.pillmasterjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.util.ArrayList;

public class MedicationFormController {
    public TextField medicationField;
    public ToggleButton dailyButton;
    public ToggleGroup scheduleToggle;
    public ToggleButton twiceButton;
    public ToggleButton thriceButton;
    public TextField quantityField;
    public Button cancelButton;
    public Button confirmButton;
    public ToggleButton monButton;
    public ToggleButton tuesButton;
    public ToggleButton wedButton;
    public ToggleButton thursButton;
    public ToggleButton friButton;
    public ToggleButton satButton;
    public ToggleButton sunButton;
    public Button nextButton;
    public HBox timeGroupA;
    public Button upHourButtonA;
    public Label hourLabelA;
    public Button downHourButtonA;
    public Button upMinuteButtonA;
    public Label minuteLabelA;
    public Button downMinuteButtonA;
    public Label periodLabelA;
    public HBox timeGroupB;
    public Button upHourButtonB;
    public Label hourLabelB;
    public Button downHourButtonB;
    public Button upMinuteButtonB;
    public Label minuteLabelB;
    public Button downMinuteButtonB;
    public Label periodLabelB;
    public HBox timeGroupC;
    public Button upHourButtonC;
    public Label hourLabelC;
    public Button downHourButtonC;
    public Button upMinuteButtonC;
    public Label minuteLabelC;
    public Button downMinuteButtonC;
    public Label periodLabelC;
    public VBox secondaryEntryGroup;
    public VBox primaryEntryGroup;
    public HBox dayButtonGroup;
    public Button backButton;
    public HBox scheduleGroup;

    private Medication medication;

    private ArrayList<ToggleButton> dayButtonList;

    private HourMinuteCounter timeA;
    private HourMinuteCounter timeB;
    private HourMinuteCounter timeC;

    public Medication getMedication() {
        return medication;
    }

    @FXML
    public void initialize() {
        dailyButton.setUserData("DAILY");
        twiceButton.setUserData("TWICE");
        thriceButton.setUserData("THRICE");
        timeA = new HourMinuteCounter();
        timeB = new HourMinuteCounter();
        timeC = new HourMinuteCounter();
        primaryEntryGroup.managedProperty().bind(
                primaryEntryGroup.visibleProperty());
        secondaryEntryGroup.managedProperty().bind(
                secondaryEntryGroup.visibleProperty());
        timeGroupA.managedProperty().bind(timeGroupA.visibleProperty());
        timeGroupB.managedProperty().bind(timeGroupB.visibleProperty());
        timeGroupC.managedProperty().bind(timeGroupC.visibleProperty());

        monButton.setUserData(DayOfWeek.MONDAY);
        tuesButton.setUserData(DayOfWeek.TUESDAY);
        wedButton.setUserData(DayOfWeek.WEDNESDAY);
        thursButton.setUserData(DayOfWeek.THURSDAY);
        friButton.setUserData(DayOfWeek.FRIDAY);
        satButton.setUserData(DayOfWeek.SATURDAY);
        sunButton.setUserData(DayOfWeek.SUNDAY);

        dayButtonList = new ArrayList<>();
        dayButtonList.add(monButton);
        dayButtonList.add(tuesButton);
        dayButtonList.add(wedButton);
        dayButtonList.add(thursButton);
        dayButtonList.add(friButton);
        dayButtonList.add(satButton);
        dayButtonList.add(sunButton);

        medicationField.textProperty().addListener((observable,oldValue, newValue) -> {
            //resets red border if text is entered
            if(newValue.length() > 0) {
                medicationField.setStyle(null);
            }
        });
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            //restricts text entry to only numbers
            if (!newValue.matches("\\d*")) {
                quantityField.setText(oldValue);
            }
            //resets red border if value is changed (only allowing nums)
            if(quantityField.getText().length() > 0){
                quantityField.setStyle(null);
            }
        });
    }

    @FXML
    public void onDayButtonToggled(ActionEvent e) {
        if(((ToggleButton)e.getSource()).isSelected()) {
            dayButtonGroup.setStyle(null);
        }
    }

    @FXML
    public void onScheduleButtonToggle(ActionEvent e) {
        Toggle button = scheduleToggle.getSelectedToggle();
        scheduleGroup.setStyle(null);
        if(button != null) {
            switch ((String)button.getUserData()) {
                case "DAILY" -> {
                    timeGroupA.setVisible(true);
                    timeGroupB.setVisible(false);
                    timeGroupC.setVisible(false);
                }
                case "TWICE" -> {
                    timeGroupA.setVisible(true);
                    timeGroupB.setVisible(true);
                    timeGroupC.setVisible(false);
                }
                case "THRICE" -> {
                    timeGroupA.setVisible(true);
                    timeGroupB.setVisible(true);
                    timeGroupC.setVisible(true);
                }
                default -> {
                }
            }
        }
    }

    public void onNextClick(){
        boolean valuesEntered = true;
        if(medicationField.getText() == null | medicationField.getText().isEmpty()) {
            medicationField.setStyle("-fx-border-color: red; " +
                    "-fx-border-width: 3px");
            valuesEntered = false;
        }
        if(quantityField.getText() == null | quantityField.getText().isEmpty()) {
            quantityField.setStyle("-fx-border-color: red; " +
                    "-fx-border-width: 3px");
            valuesEntered = false;
        }
        if(!(monButton.isSelected() | tuesButton.isSelected()
                | wedButton.isSelected() | thursButton.isSelected()
                | friButton.isSelected() | satButton.isSelected()
                | sunButton.isSelected())) {
            dayButtonGroup.setStyle("-fx-border-color: red; " +
                    "-fx-border-width: 3px");
            valuesEntered = false;
        }
        if(valuesEntered) {
            primaryEntryGroup.setVisible(false);
            secondaryEntryGroup.setVisible(true);
        }
    }

    @FXML
    public void onBackClick(){
        secondaryEntryGroup.setVisible(false);
        primaryEntryGroup.setVisible(true);
    }

    @FXML
    public void onConfirmClick(ActionEvent e) {
        boolean valuesEntered = true;
        if(scheduleToggle.getSelectedToggle() == null) {
            scheduleGroup.setStyle("-fx-border-color: red; " +
                    "-fx-border-width: 3px");
            valuesEntered = false;
        }
        if(valuesEntered) {
            ArrayList<HourMinuteCounter> tmpTime = new ArrayList<>();
            ArrayList<DayOfWeek> tmpDay = new ArrayList<>();

            for (ToggleButton button : dayButtonList) {
                if (button.isSelected()) {
                    tmpDay.add((DayOfWeek) button.getUserData());
                }
            }

            String schedule =
                    (String) scheduleToggle.getSelectedToggle().getUserData();
            switch (schedule) {
                case "THRICE":
                    tmpTime.add(0, timeC);
                case "TWICE":
                    tmpTime.add(0, timeB);
                case "DAILY":
                    tmpTime.add(0, timeA);
                    break;
            }
            medication = new Medication(
                    medicationField.getText(),
                    Integer.parseInt(quantityField.getText()),
                    tmpDay,
                    tmpTime
            );
            System.out.println("Entry confirmed!");
            ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
        }
    }

    @FXML
    public void onCancelClick(ActionEvent e) {
        System.out.println("Entry canceled!");
        medication = null;
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
    }

    private void updateTimeLabels(String id) {
        switch (id) {
            case "A" -> {
                hourLabelA.setText(String.valueOf(timeA.getHour()));
                minuteLabelA.setText(String.valueOf(timeA.getMinute()));
                if (timeA.isPM()) {
                    periodLabelA.setText("PM");
                } else {
                    periodLabelA.setText("AM");
                }
            }
            case "B" -> {
                hourLabelB.setText(String.valueOf(timeB.getHour()));
                minuteLabelB.setText(String.valueOf(timeB.getMinute()));
                if (timeB.isPM()) {
                    periodLabelB.setText("PM");
                } else {
                    periodLabelB.setText("AM");
                }
            }
            case "C" -> {
                hourLabelC.setText(String.valueOf(timeC.getHour()));
                minuteLabelC.setText(String.valueOf(timeC.getMinute()));
                if (timeC.isPM()) {
                    periodLabelC.setText("PM");
                } else {
                    periodLabelC.setText("AM");
                }
            }
            default -> System.out.println("updateTimeLabels error");
        }
    }

    @FXML
    public void onUpHourButtonClick(ActionEvent e) {
        String id = ((Control)e.getSource()).getId();
        String subId = id.substring(id.length()-1);
        switch (subId) {
            case "A" -> timeA.incrementHour();
            case "B" -> timeB.incrementHour();
            case "C" -> timeC.incrementHour();
            default -> System.out.println("Wrong Hour Button ID");
        }
        updateTimeLabels(subId);
    }
    @FXML
    public void onDownHourButtonClick(ActionEvent e) {
        String id = ((Control)e.getSource()).getId();
        String subId = id.substring(id.length()-1);
        switch (subId) {
            case "A" -> timeA.decrementHour();
            case "B" -> timeB.decrementHour();
            case "C" -> timeC.decrementHour();
            default -> System.out.println("Wrong Hour Button ID");
        }
        updateTimeLabels(subId);
    }
    @FXML
    public void onUpMinuteButtonClick(ActionEvent e) {
        String id = ((Control)e.getSource()).getId();
        String subId = id.substring(id.length()-1);
        switch (subId) {
            case "A" -> timeA.incrementMinuteTen();
            case "B" -> timeB.incrementMinuteTen();
            case "C" -> timeC.incrementMinuteTen();
            default -> System.out.println("Wrong Hour Button ID");
        }
        updateTimeLabels(subId);
    }
    @FXML
    public void onDownMinuteButtonClick(ActionEvent e) {
        String id = ((Control)e.getSource()).getId();
        String subId = id.substring(id.length()-1);
        switch (subId) {
            case "A" -> timeA.decrementMinuteTen();
            case "B" -> timeB.decrementMinuteTen();
            case "C" -> timeC.decrementMinuteTen();
            default -> System.out.println("Wrong Hour Button ID");
        }
        updateTimeLabels(subId);

    }
}
