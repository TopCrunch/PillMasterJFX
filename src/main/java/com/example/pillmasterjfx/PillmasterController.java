package com.example.pillmasterjfx;

import javafx.animation.PauseTransition;
import javafx.concurrent.ScheduledService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PillmasterController {
    SchedulerService sched;
    private int numFailed = 0;

    @FXML
    public void initialize() {
        System.out.println("FXML file loaded!");

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(se -> {
            numFailed++;
        });

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


    }
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText(Integer.toString(numFailed));
    }
}