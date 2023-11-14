package com.example.pillmasterjfx;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class MedicationScheduler{
    public static final String JSON_PATH = "sample-meds.json";
    private static final int SECONDS_IN_DAY = 86400;
    private static final int MINUTES_IN_DAY = 1440;
    public static boolean demoMode = false;
    private static final int DEMO_INTERVAL_SECONDS = 72;
    private final Timeline timeline;
    private final HashMap<String, Medication> medicationMap;
    private final PauseTransition adherencePause;


    public MedicationScheduler() {
        medicationMap = new HashMap<>();
        timeline = new Timeline();
        timeline.setCycleCount(1);
        adherencePause = new PauseTransition(Duration.seconds(5));
        adherencePause.setOnFinished(se -> {
            System.out.println("ADHERENCE FAILED");
        });
    }

    public JSONArray pullMedicationArray() throws IOException {
        //TODO change to contact server for daily update DONE?
        //JSONObject content = openJSONFile(JSON_PATH);
        JSONObject content = requestJSONFile();

        if(content != null) {
            if(content.has("medication")) {
                return content.getJSONArray("medication");
            } else {
                System.out.println("!!! Incorrect JSON !!!");
                return null;
            }
        } else {
            System.out.println("There was an error reading the JSON " +
                    "file...");
            return null;
        }
    }

    public static Duration timeToMidnight() {
        Duration duration;
        int mid = getSecondsSinceMidnight();
        System.out.println(mid + " is the current time");
        if(demoMode) {
            //this is only for demo timings
            duration = Duration.seconds(
                            DEMO_INTERVAL_SECONDS
                                    - (mid % DEMO_INTERVAL_SECONDS)
                    );

        } else {
            LocalTime time = LocalTime.now();
            Duration now = Duration.hours(time.getHour())
                            .add(Duration.minutes(time.getMinute()))
                            .add(Duration.seconds(time.getSecond()));
            duration =  Duration.hours(24).subtract(now);
        }
        //Debug value output
        System.out.println(
                duration.toSeconds() + " seconds until next interval");

        return duration;
    }

    private static int getSecondsSinceMidnight() {
        LocalDateTime current = LocalDateTime.now();
        return (current.getSecond()
                + current.getMinute()*60
                + current.getHour()*3600
        );
    }

    private static boolean hasDayToday(ArrayList<DayOfWeek> days) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        for(DayOfWeek day : days) {
            if(today.compareTo(day) == 0) {
                return true;
            }
        }
        return false;
    }

    public void checkDailyMeds() {
        timeline.getKeyFrames().clear();
        medicationMap.forEach((String s, Medication m) -> {
            //check if every day
            if(m.getCount() > 0 && hasDayToday(m.getDaysOfWeek())) {
                for (HourMinuteCounter time : m.getTimeList()) {
                    Duration duration = time.getDurationToTime();
                    if (Duration.ZERO.compareTo(duration) < 0) {
                        timeline.getKeyFrames().add(new KeyFrame(
                                duration,
                                m.getName(),
                                (ActionEvent event) -> {
                                    String medName = m.getName();
                                    System.out.println("Alert for " + medName);
                                    Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
                                    popup.setOnHidden(he -> {
                                        adherencePause.stop();

                                        //TODO placeholder for dispensing process
                                        medicationMap.get(medName).popCount();
                                        try {
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
                }
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

    public void populateMedicationMap(JSONArray content) {
        for (var obj : content) {
            if (obj instanceof JSONObject) {
                JSONObject entry = (JSONObject) obj;
                Medication medication = new Medication(
                        entry.getString("name"),
                        entry.getInt("count"),
                        entry.getString("schedule"),
                        entry.getJSONArray("days"),
                        entry.getJSONArray("hours")
                );
                medicationMap.put(medication.getName(), medication);
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

    private JSONObject requestJSONFile() {
        NetworkClient client = new NetworkClient();
        try {
            String response = client.requestMedicationFile();
            if(response.length() > 0) {
                return new JSONObject(response);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
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


}
