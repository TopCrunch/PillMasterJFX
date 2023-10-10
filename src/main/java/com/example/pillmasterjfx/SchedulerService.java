package com.example.pillmasterjfx;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class SchedulerService extends ScheduledService<Integer> {
    @Override
    protected Task<Integer> createTask() {
        return new Task<>() {
            @Override
            protected Integer call() throws Exception {
                //contact server for phone notif here?

                //then return confirmation y/n?
                return 1;
            }
        };
    }


}
