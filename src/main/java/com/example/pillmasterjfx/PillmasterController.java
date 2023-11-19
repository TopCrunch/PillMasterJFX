package com.example.pillmasterjfx;

import javafx.animation.AnimationTimer;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jssc.SerialPortException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PillmasterController {
    private static final int HOURS_IN_DAY = 60;
    private static final int NIGHT_HOUR = 45;
    private static final int MIDDAY_HOUR = 30;
    private static final int MORNING_HOUR = 15;
    private static final String TEST_JSON_FILE = "sample-meds.json";
    private final HashMap<Medication, Boolean> runningMedicationMap;
    public Button newMedButton;
    public Label timeLabel;
    public Button skipButton;
    public Label nextLabel;
    MedicationScheduler medicationScheduler;
    ScheduledService<Integer> service;
    File jsonFile;

    SerialController serialController;
    public PillmasterController() {
        runningMedicationMap = new HashMap<>();
    }

    @FXML
    public void initialize() throws IOException {
        System.out.println("FXML file loaded!");
        try {
            serialController = new SerialController();
        } catch(SerialPortException e) {
            System.out.println(e.getMessage());
        }

        jsonFile = new File(TEST_JSON_FILE);

        if(jsonFile.createNewFile()){
            System.out.println(jsonFile.getName() + " created!");
        }
        medicationScheduler = new MedicationScheduler();
        initScheduler();
        startService();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                timeLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E MM-dd-yyyy\nHH:mm:ss a")));
            }
        };
        timer.start();

    }

    public void initScheduler() {
        service = new ScheduledService<>() {
            @Override
            protected Task<Integer> createTask() {
                return new Task<>() {
                    @Override
                    protected Integer call(){
                        try {
                            medicationScheduler.populateMedicationMap(
                                    medicationScheduler.pullMedicationArray()
                            );
                            medicationScheduler.checkDailyMeds();
                            setPeriod(MedicationScheduler.timeToMidnight());
                        } catch (IOException e) {
                            e.printStackTrace(System.err);
                        }
                        return 1;
                    }
                };
            }
        };
    }

    public void startService() {
        service.setOnSucceeded(e -> {
            System.out.println("Scheduler completed successfully...");
            if(medicationScheduler.getNumScheduled() > 0) {
                nextLabel.setText(
                        "Up next: "
                                + medicationScheduler.getNextScheduled()
                );
            } else {
                nextLabel.setText("No medication scheduled today...");
                skipButton.setDisable(true);
            }
        });
        service.setOnFailed(e -> {
            System.err.println("Scheduler failed!");
            service.getException().printStackTrace(System.err);
            System.err.println("Retrying...");
            service.setPeriod(Duration.seconds(5));
        });
        service.setRestartOnFailure(true);
        service.start();
    }

    public void addMedication(Medication medication) {
        //runningMedicationMap.put(medication, false);
        //touchMedicationList();
        medicationScheduler.addNewMedication(medication);
    }

    public void touchMedicationList() {
        for (Map.Entry<Medication, Boolean> entry
                : runningMedicationMap.entrySet()){
            if(!entry.getValue()) {
                //startSchedulerService(entry.getKey());
            }
        }
    }

    @FXML
    protected void onNewMedButtonClick() throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(PillmasterApp.class.getResource(
                        "medication-form.fxml")
                );
        Scene scene = new Scene(fxmlLoader.load(), 600, 300);
        Stage stage = new Stage();
        stage.setTitle("Pillmaster-Demo");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);

        MedicationFormController controller = fxmlLoader.getController();
        stage.setOnHidden(we -> {
            Medication tmp = controller.getMedication();
            if(tmp != null) {
                System.out.println(tmp + " added");
                addMedication(tmp);
            } else {
                System.out.println("Entry canceled -> NullData");
            }
        });

        stage.showAndWait();
    }

    @FXML
    protected void onSkipButtonClick() {
        if(medicationScheduler.triggerNextKeyframe()) {
            nextLabel.setText(
                    "Up next: "
                    + medicationScheduler.getNextScheduled()
            );
        } else {
            nextLabel.setText("No medication scheduled today...");
            skipButton.setDisable(true);
        }
    }

    @FXML
    protected void onTestButtonClick() throws SerialPortException {
        // TODO: 10/21/2023 move serial usage to Dispenser.java
        //serialController.writeToPort('b');
        Dispenser dispenser = new Dispenser();
        try {
            dispenser.testMethod();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}