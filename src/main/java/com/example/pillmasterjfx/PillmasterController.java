package com.example.pillmasterjfx;

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
    MedicationScheduler medicationScheduler;
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
        startSchedulerService();

    }

    public void startSchedulerService() {
        medicationScheduler = new MedicationScheduler();
        //get the current seconds in current minute
        int currentSeconds = LocalDateTime.now().getSecond();
        System.out.println(currentSeconds);
        //medicationScheduler.setDelay(Duration.seconds(60 - currentSeconds));
        System.out.println("Waiting " + (60 - currentSeconds) + " seconds");
        System.out.println(System.getProperty("user.dir"));
        medicationScheduler.setPeriod(Duration.minutes(1));
        medicationScheduler.setOnSucceeded(e -> {
            System.out.println("Scheduler completed successfully...");
        });
        medicationScheduler.start();
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
    private Label valueLabel;

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
    protected void onTestButtonClick() throws SerialPortException {
        // TODO: 10/21/2023 move serial usage to Dispenser.java
        //serialController.writeToPort('b');
        Dispenser dispenser = new Dispenser();
        dispenser.dispense();
    }
}