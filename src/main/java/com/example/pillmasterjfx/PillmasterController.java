package com.example.pillmasterjfx;

import javafx.concurrent.ScheduledService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PillmasterController {
    private static final int NUM_SCHEDULE_THREADS = 1;
    private final ScheduledExecutorService alertScheduler =
            Executors.newScheduledThreadPool(NUM_SCHEDULE_THREADS);
    private ArrayList<Calendar> alertCalendar;
    SchedulerService sched;


    @FXML
    public void initialize() {
        System.out.println("FXML file loaded!");
        sched = new SchedulerService();
        sched.setOnSucceeded(e -> {
            System.out.println("Starting alert");
            Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
            popup.showAndWait();
            System.out.println("Alert done!");
        });
        sched.setDelay(javafx.util.Duration.seconds(5));
        sched.setPeriod(javafx.util.Duration.seconds(5));
        sched.start();
    }
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("The button works!");
    }
}