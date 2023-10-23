package com.example.pillmasterjfx;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;

public class MedicationScheduler extends ScheduledService<Integer> {
    public static final String JSON_PATH = "sample-meds.json";
    private static final int SECONDS_IN_DAY = 86400;
    private static final int DEMO_INTERVAL_SECONDS = 72;
    private final Timeline timeline;
    private JSONArray medArray;
    private final HashMap<String, Medication> medicationMap;
    private final PauseTransition adherencePause;
    @Override
    protected Task<Integer> createTask() {
        return new Task<>() {
            @Override
            protected Integer call() throws Exception {
                //TODO change to contact server for daily update
                JSONObject content = openJSONFile(JSON_PATH);

                if(content != null) {
                    if(content.has("medication")) {
                        medArray = content.getJSONArray("medication");
                    } else {
                        System.out.println("!!! Incorrect JSON !!!");
                        return 0;
                    }
                } else {
                    System.out.println("There was an error reading the JSON " +
                            "file...");
                    return 0;
                }

                //TODO have task update its own period to be midnight next
                // and test for restarting tweaks

                populateMedicationMap(medArray);
                timeline.setCycleCount(1);
                checkDailyMeds();

                //reschedule self to next midnight (unless demo)
                setPeriod(timeToMidnight());

                return 1;
            }
        };
    }

    private Duration timeToMidnight() {
        //this is only for demo timings
        int currentSecond = LocalDateTime.now().getSecond();
        int currentMinute = LocalDateTime.now().getMinute();
        int currentHour = LocalDateTime.now().getHour();
        int secondsSinceMidnight =
                currentSecond + currentMinute*60 + currentHour*3600;
        int value = secondsSinceMidnight % DEMO_INTERVAL_SECONDS;

        //Debug value output
        System.out.println(value + " seconds until next interval");

        return Duration.seconds(value);
    }

    private void checkDailyMeds() {
        timeline.getKeyFrames().clear();
        medicationMap.forEach((String s, Medication m) -> {
            //check if every day
            if(m.getCount() > 0
                    && m.getDaysOfWeek().size() >= 7) {
                timeline.getKeyFrames().add(new KeyFrame(
                        Duration.seconds(30),
                        m.getName(),
                        (ActionEvent event) -> {
                            String medName = m.getName();
                            System.out.println("Alert for " + medName);
                            Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
                            popup.setOnHidden(he -> {
                                adherencePause.stop();

                                //TODO placeholder for dispensing process
                                medicationMap.get(medName).popCount();
                                try{
                                    writeToJSON();
                                } catch (IOException e) {
                                    System.out.println("writing error");
                                }
                                //---------------------------------------
                            });
                            popup.show();
                            adherencePause.play();
                        }
                ));
            }
        });
        if(!timeline.getKeyFrames().isEmpty()) {
            timeline.playFromStart();
        }
    }

    public void addNewMedication(Medication medication) {
        medicationMap.put(medication.getName(), medication);
        try {
            writeToJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateMedicationMap(JSONArray content) {
        for(var obj: content) {
            if(obj instanceof JSONObject) {
                JSONObject entry = (JSONObject) obj;
                Medication medication = new Medication(
                    entry.getString("name"),
                    entry.getInt("count"),
                    entry.getString("schedule"),
                    entry.getJSONArray("days"),
                    entry.getJSONArray("hours")
                );
                medicationMap.put(medication.getName(),medication);
            }
        }
    }

    private JSONObject openJSONFile(String path) throws IOException {
            String stream = new String(Files.readAllBytes(Paths.get(path)));
            if(stream.length() > 0) {
                JSONObject tmp = new JSONObject(stream);
                return tmp;
            } else {
                return null;
            }
    }

    public void writeToJSON() throws IOException {
        JSONArray array = new JSONArray();
        JSONObject top = new JSONObject();
        for(Medication value:medicationMap.values()){
            array.put(value.toJSON());
        }
        top.put("medication", array);
        FileWriter writer = new FileWriter(JSON_PATH);
        writer.write(top.toString());
        writer.close();
    }

    public MedicationScheduler() {
        medicationMap = new HashMap<>();
        timeline = new Timeline();
        adherencePause = new PauseTransition(Duration.seconds(5));
        adherencePause.setOnFinished(se -> {
            System.out.println("ADHERENCE FAILED");
        });
    }


}
