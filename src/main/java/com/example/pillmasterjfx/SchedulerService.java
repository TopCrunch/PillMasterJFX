package com.example.pillmasterjfx;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerService extends ScheduledService<Integer> {
    @Override
    protected Task<Integer> createTask() {
        return new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return 1;
            }
        };
    }
    private static final int NUM_SCHEDULE_THREADS = 1;
    private Calendar nextTime;
    private final ScheduledExecutorService alertScheduler =
            Executors.newScheduledThreadPool(NUM_SCHEDULE_THREADS);

    public void initiate() {
        nextTime = Calendar.getInstance();
        nextTime.add(Calendar.MINUTE, 1);
        /*
        System.out.println(
                nextTime.getTimeInMillis()
                        - Calendar.getInstance().getTimeInMillis());

        */
        final Runnable alert = () -> {
            System.out.println("triggered!");
            Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
            popup.showAndWait();
        };
        final ScheduledFuture<?> alertHandle = alertScheduler.schedule(
                alert, 1000,
                TimeUnit.MILLISECONDS);
    }
}
