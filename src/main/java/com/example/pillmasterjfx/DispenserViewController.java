package com.example.pillmasterjfx;

import javafx.animation.*;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import jssc.SerialPortException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;

public class DispenserViewController {
    public ProgressBar countdownBar;
    public Button dispenseButton;
    public Label keypadLabel;
    private Medication medication;
    private ArduinoController arduino;
    Timeline countdownTimer;
    AudioPlayer player;
    MobileNotifier notifier;
    final Object block = new Object();

    private static final String TEST_PIN = "0191";

    private boolean failed = false;
    private boolean ready = false;

    public DispenserViewController() {
    }

    public void initialize() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        player = new AudioPlayer();
        notifier = new MobileNotifier();
        notifier.sendEmail();

        player.play();
    }

    public void startCountdown() {
        countdownTimer = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(countdownBar.progressProperty(), 1)),
                new KeyFrame(Duration.seconds(10), e-> {
                    failed = true;
                    System.out.println("Countdown over. FAILED");
                    closeWindow(e);
                }, new KeyValue(countdownBar.progressProperty(), 0))
        );
        countdownTimer.setCycleCount(1);
        countdownTimer.play();
    }

    @FXML
    public void onDispenseButtonClick(ActionEvent e) {
        //initiate dispensing process
        countdownTimer.stop();
        Service<Integer> service = new Service<>() {
            @Override
            protected Task<Integer> createTask() {
                return new Task<>() {
                    @Override
                    protected Integer call() throws Exception {

                        synchronized (keypadLabel) {
                            keypadLabel.wait();
                        }

                        requestReady();
                        waitForArduino();
                        dispense();
                        requestReady();
                        waitForArduino();
                        requestWeight();
                        //check if weight is correct (wait for it to be set)
                        //requestTrip();
                        //check if trip is triggered

                        //if(errors) {
                        //  display warning and wait
                        // }

                        return null;
                    }
                };
            }
        };
        service.setOnSucceeded(event -> closeWindow(e));
        service.start();
    }

    @FXML
    public void onKeypadButtonClick(ActionEvent e) {
        String value = ((Button)e.getSource()).getText();
        switch (value) {
            case "GO":
                if(keypadLabel.getText().compareTo(TEST_PIN) == 0) {
                    synchronized (keypadLabel) {
                        keypadLabel.notify();
                    }
                } else {
                    keypadLabel.setStyle("-fx-border-color: red;");
                }
                break;
            case "X":
                keypadLabel.setStyle("");
                if(keypadLabel.getText().length() > 0) {
                    if(keypadLabel.getText().length() == 1) {
                        keypadLabel.setText("");
                    } else {
                        keypadLabel.setText(keypadLabel.getText().substring(0,
                                keypadLabel.getText().length() - 1));
                    }
                }
                //delete
                break;
            default:
                keypadLabel.setStyle("");
                if(keypadLabel.getText().length() < 4) {
                    keypadLabel.setText(keypadLabel.getText() + value);
                }
        }
    }

    public boolean failed() {
        return failed;
    }

    private void closeWindow(ActionEvent e) {
        player.close();
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
    }

    public void processSerial(String s) {
        //process response from arduino to determine what to do next
        String processed = s.replaceAll("([\\r\\n])", "");
        switch (processed) {
            case "Done" -> System.out.println("Arduino dispense done");
            case "R" -> {
                System.out.println("Arduino ready");
                synchronized (block) {
                    block.notify();
                }
            }

            case "T" -> System.err.println("TARE TIMEOUT, CHECK WIRING");
            default -> System.out.println(processed);
        }
    }

    public void bindMedication(Medication medication) {
        this.medication = medication;
    }

    public void bindArduino(ArduinoController arduino) {
        this.arduino = arduino;
        try {
            arduino.listen(this::processSerial);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void clearListener() throws SerialPortException {
        arduino.clearListener();
    }

    public void notifyMobile(String content) {
        //send firebase alert with MEDICATION_NAME in payload
    }

    private void requestReady() {
        arduino.write(SerialController.CONTROL_FLAG.makeSignal(
                SerialController.CONTROL_FLAG.ALT,
                SerialController.CONTROL_FLAG.READY
        ));
    }

    private void requestWeight() {
        System.out.println("Requesting Weight");
        arduino.write(SerialController.CONTROL_FLAG.makeSignal(
                SerialController.CONTROL_FLAG.ALT,
                SerialController.CONTROL_FLAG.WEIGHT
        ));
    }

    private void dispense() {
        switch (medication.getCanisterNumber()) {
            case 0 -> arduino.write(SerialController.CONTROL_FLAG.makeSignal(
                    SerialController.CONTROL_FLAG.OPERATE,
                    SerialController.CONTROL_FLAG.A
            ));
            case 1 -> arduino.write(SerialController.CONTROL_FLAG.makeSignal(
                    SerialController.CONTROL_FLAG.OPERATE,
                    SerialController.CONTROL_FLAG.B
            ));
            case 2 -> arduino.write(SerialController.CONTROL_FLAG.makeSignal(
                    SerialController.CONTROL_FLAG.OPERATE,
                    SerialController.CONTROL_FLAG.C
            ));
            case 3 -> arduino.write(SerialController.CONTROL_FLAG.makeSignal(
                    SerialController.CONTROL_FLAG.OPERATE,
                    SerialController.CONTROL_FLAG.D
            ));
            case 4 -> arduino.write(SerialController.CONTROL_FLAG.makeSignal(
                    SerialController.CONTROL_FLAG.OPERATE,
                    SerialController.CONTROL_FLAG.E
            ));
            default ->
                    throw new IllegalStateException("Unexpected value: " + medication.getCanisterNumber());
        }
    }

    public void waitForArduino() {
        //wait for arduino to be ready before continuing process
        synchronized (block) {
            try {
                block.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
