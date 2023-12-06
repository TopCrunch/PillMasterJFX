package com.example.pillmasterjfx;

import javafx.animation.*;
import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import jssc.SerialPortException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;
import java.security.SecureRandom;

public class DispenserViewController {
    public ProgressBar countdownBar;
    public Button dispenseButton;
    public Label keypadLabel;
    public HBox keyPadBox;
    public Label weightLabel;
    public Label tripLabel;
    public HBox errorButtonsBox;
    private Medication medication;
    private ArduinoController arduino;
    Timeline countdownTimer;
    AudioPlayer player;
    MobileNotifier notifier;
    private final Object block = new Object();

    private static final String TEST_PIN = "0191";

    private boolean failed = false;
    private boolean error = false;
    private boolean retry = false;
    private final Object weightBlock = new Object();
    private final Object tripBlock = new Object();

    private final Object errorBlock = new Object();

    private double processedWeight = 0;
    private int processedTrips = 0;

    public DispenserViewController() {
    }

    public void initialize() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        player = new AudioPlayer();
        notifier = new MobileNotifier();

        dispenseButton.managedProperty().bind(dispenseButton.visibleProperty());
        keyPadBox.managedProperty().bind(keyPadBox.visibleProperty());
        countdownBar.managedProperty().bind(countdownBar.visibleProperty());
        errorButtonsBox.managedProperty().bind(errorButtonsBox.visibleProperty());

        player.play();
    }

    public void startCountdown() {
        countdownTimer = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(countdownBar.progressProperty(), 1)),
                new KeyFrame(Duration.seconds(60), e-> {
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
        dispenseButton.setVisible(false);
        keyPadBox.setVisible(true);
        Service<Integer> service = new Service<>() {
            @Override
            protected Task<Integer> createTask() {
                return new Task<>() {
                    @Override
                    protected Integer call() throws Exception {

                        synchronized (keypadLabel) {
                            keypadLabel.wait();
                        }

                        try {
                            dispenseCycle();
                        } catch (InterruptedException exception) {
                            exception.printStackTrace();
                        }

                        checkErrors();
                        if(error) {
                            Platform.runLater(() -> {
                                errorButtonsBox.setVisible(true);
                                countdownBar.setVisible(false);
                            });
                        }
                        while(error) {
                            synchronized (errorBlock) {
                                errorBlock.wait();
                            }
                            if(retry) {
                                try {
                                    Platform.runLater(() -> {
                                        weightLabel.setText("Please wait...");
                                        tripLabel.setText("");
                                    });
                                    dispenseCycle();
                                } catch (InterruptedException exception) {
                                    exception.printStackTrace();
                                }
                                checkErrors();
                                retry = false;
                            }
                        }

                        return null;
                    }
                };
            }
        };
        service.setOnSucceeded(event -> {
            if(!error) {
                closeWindow(e);
            } else {
                //do nothing
            }
        });
        service.start();
    }

    public void sendEmail() {

        notifier.sendEmail(medication.getName());
    }

    private void dispenseCycle() throws InterruptedException {
        requestReady();
        waitForArduino();
        dispense();
        requestReady();
        waitForArduino();
        requestWeight();
        synchronized (weightBlock) {
            weightBlock.wait();
        }
        //check if weight is correct (wait for it to be set)
        requestTrip();
        synchronized (tripBlock) {
            tripBlock.wait();
        }
        //check if trip is triggered
    }

    private void checkErrors() {
        //if(errors) {
        //  display warning and wait
        // }
        double diff = processedWeight - medication.getWeight();
        diff = Math.abs(diff);
        if(diff > medication.getWeight() * 0.25) {
            Platform.runLater(
                    () -> weightLabel.setText(
                            "WARNING, WEIGHT"));
            error = true;
        }
        if(!(processedTrips == 1)) {
            Platform.runLater(() ->tripLabel.setText(
                    "WARNING," +
                            " TRIP"));
            error = true;
        }
    }

    @FXML
    public void onKeypadButtonClick(ActionEvent e) {
        String value = ((Button)e.getSource()).getText();
        switch (value) {
            case "GO":
                if(keypadLabel.getText().compareTo(TEST_PIN) == 0) {
                    synchronized (keypadLabel) {
                        keypadLabel.setText("");
                        weightLabel.setText("Please wait...");
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

    public void onFinishedClick(ActionEvent e) {
        error = false;
        synchronized (errorBlock) {
            errorBlock.notify();
        }
    }

    public void onReattemptClick() {
        retry = true;
        synchronized (errorBlock) {
            errorBlock.notify();
        }
    }

    public boolean failed() {
        return failed;
    }

    public boolean error() {
        return error;
    }

    private void closeWindow(ActionEvent e) {
        player.close();
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
    }

    public void processSerial(String s) {
        //process response from arduino to determine what to do next
        String processed = s.replaceAll("([\\r\\n])", "");
        if ("Done".equals(processed)) {
            System.out.println("Arduino dispense done");
        } else if ("R".equals(processed)) {
            System.out.println("Arduino ready");
            synchronized (block) {
                block.notify();
            }
        } else if ("T".equals(processed)) {
            System.err.println("TARE TIMEOUT, CHECK WIRING");
        } else if (processed.contains("#")) {
            String str = processed.replace("#","");
            processedWeight = Double.parseDouble(str);
            System.out.println(processedWeight);
            synchronized (weightBlock) {
                weightBlock.notify();
            }
        } else if (processed.contains("@")){
            String str = processed.replace("@","");
            processedTrips = Integer.parseInt(str);
            System.out.println(processedTrips);
            synchronized (tripBlock) {
                tripBlock.notify();
            }
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

    private void requestTrip() {
        System.out.println("Requesting Trip");
        arduino.write(SerialController.CONTROL_FLAG.makeSignal(
                SerialController.CONTROL_FLAG.ALT,
                SerialController.CONTROL_FLAG.TRIP
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
