package com.example.pillmasterjfx;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PillmasterController {
    private static final int HOURS_IN_DAY = 60;
    private static final int NIGHT_HOUR = 45;
    private static final int MIDDAY_HOUR = 30;
    private static final int MORNING_HOUR = 15;
    private final HashMap<Medication, Boolean> runningMedicationMap;
    public Button newMedButton;
    SchedulerService sched;
    private int numFailed = 0;

    PauseTransition adherencePause = new PauseTransition(Duration.seconds(5));

    public PillmasterController() {
        runningMedicationMap = new HashMap<>();
    }

    @FXML
    public void initialize() {
        System.out.println("FXML file loaded!");

        adherencePause.setOnFinished(se -> {
            numFailed++;
        });

        /*
        sched = new SchedulerService();
        sched.setOnRunning(e -> {
            System.out.println("Starting alert");
            Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
            popup.setOnHidden(he -> {
                System.out.println("Alert done!");
                pause.stop();
            });
            popup.show();
            pause.play();
        });
        sched.setDelay(javafx.util.Duration.seconds(5));
        sched.setPeriod(javafx.util.Duration.seconds(5));
        sched.start();

         */


    }

    public void scheduleMedication(Medication medication) {
        sched = new SchedulerService();
        sched.setOnRunning(e -> {
            System.out.println("Alert for " + medication);
            Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
            popup.setOnHidden(he -> {
                adherencePause.stop();
            });
            popup.show();
            adherencePause.play();
        });
        //get the current seconds in current minute
        int currentSeconds = LocalDateTime.now().getSecond();
        switch(medication.getSchedule()) {
            case DAILY:
                int tmp = MIDDAY_HOUR - currentSeconds;
                if(tmp > 0) {
                    sched.setDelay(javafx.util.Duration.seconds(tmp));
                } else {
                    //add negative value to loop-around to meet next time
                    sched.setDelay(javafx.util.Duration.seconds(60 + tmp));
                }
                    sched.setPeriod(javafx.util.Duration.seconds(60));
                sched.start();
                System.out.println(medication + " scheduled");
                break;
            case TWICE:
                break;
            case THRICE:
                break;
            default:
        }
    }

    public void addMedication(Medication medication) {
        runningMedicationMap.put(medication, false);
        touchMedicationList();
    }

    public void touchMedicationList() {
        for (Map.Entry<Medication, Boolean> entry
                : runningMedicationMap.entrySet()){
            if(!entry.getValue()) {
                scheduleMedication(entry.getKey());
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
}