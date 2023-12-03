package com.example.pillmasterjfx;

import javafx.animation.*;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import jssc.SerialPortException;

public class DispenserViewController {
    public ProgressBar countdownBar;
    public Button dispenseButton;
    private Medication medication;
    private ArduinoController arduino;
    Timeline countdownTimer;
    final Object block = new Object();

    private boolean failed = false;
    private boolean ready = false;

    public DispenserViewController() {
    }

    public void initialize() {

    }

    public void startCountdown() {
        countdownTimer = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(countdownBar.progressProperty(), 1)),
                new KeyFrame(Duration.seconds(10), e-> {
                    failed = true;
                    System.out.println("Countdown over. FAILED");
                }, new KeyValue(countdownBar.progressProperty(), 0))
        );
        countdownTimer.setCycleCount(1);
        countdownTimer.play();
    }

    @FXML
    public void onDispenseButtonClick() {
        //initiate dispensing process
        countdownTimer.stop();
        Service<Integer> service = new Service<>() {
            @Override
            protected Task<Integer> createTask() {
                return new Task<>() {
                    @Override
                    protected Integer call() throws Exception {
                        requestReady();
                        waitForArduino();
                        dispense();
                        requestReady();
                        waitForArduino();
                        requestWeight();
                        return null;
                    }
                };
            }
        };
        service.start();
    }

    public boolean failed() {
        return failed;
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

    public void notifyMobile() {
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
