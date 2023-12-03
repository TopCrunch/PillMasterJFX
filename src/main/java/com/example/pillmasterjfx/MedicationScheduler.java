package com.example.pillmasterjfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jssc.SerialPortException;
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
import java.util.Iterator;

public class MedicationScheduler{
    public static final String JSON_PATH = "PM-Local-Backup.json";
    public static final int NUMBER_OF_CANISTERS = 5;
    private static final int SECONDS_IN_DAY = 86400;
    private static final int MINUTES_IN_DAY = 1440;

    private int numScheduled = 0;
    public static boolean demoMode = false;
    private static final int DEMO_INTERVAL_SECONDS = 72;
    private final Timeline timeline;
    private final Medication[] medicationArray;


    public MedicationScheduler() {
        medicationArray = new Medication[NUMBER_OF_CANISTERS];
        timeline = new Timeline();
        timeline.setCycleCount(1);
    }

    public JSONObject pullMedicationArray() throws IOException {
        JSONObject content = requestJSONFile();
        if(content == null) {
            content = openJSONFile(JSON_PATH);
        }

        if(content != null) {
            if(content.has("medication")) {
                return (JSONObject) content.get("medication");
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

    public String getNextScheduled() {
        return timeline.getKeyFrames().get(0).getName();
    }

    public int getNumScheduled() {
        return numScheduled;
    }

    public boolean triggerNextKeyframe(){
        if(numScheduled > 0) {
            timeline.pause();
            Duration playhead = timeline.getCurrentTime();
            timeline.stop();
            KeyFrame next = timeline.getKeyFrames().remove(0);
            numScheduled--;
            timeline.getKeyFrames().add(
                    new KeyFrame(
                            Duration.seconds(1).add(playhead),
                            next.getName(),
                            next.getOnFinished()
                    ));
            timeline.playFrom(playhead);
        } else {
            return false;
        }
        return true;
    }

    public void checkDailyMeds() {
        timeline.getKeyFrames().clear();
        for(int i = 0; i < medicationArray.length; i++) {
            if(medicationArray[i] == null) {
                continue;
            }
            Medication m = medicationArray[i];
            if(m.getCount() > 0 && hasDayToday(m.getDaysOfWeek())) {
                for (HourMinuteCounter time : m.getTimeList()) {
                    Duration duration = time.getDurationToTime();
                    if (Duration.ZERO.compareTo(duration) < 0) {
                        int finalIndex = i;
                        timeline.getKeyFrames().add(new KeyFrame(
                                duration,
                                m.getName(),
                                (ActionEvent event) -> {
                                    try {
                                        FXMLLoader fxmlLoader = new FXMLLoader(PillmasterApp.class.getResource(
                                                "dispenser-view.fxml")
                                        );
                                        Scene scene = new Scene(fxmlLoader.load(), 800, 400);
                                        DispenserViewController controller = fxmlLoader.getController();
                                        controller.bindArduino(PillmasterController.getArduinoController());
                                        controller.bindMedication(m);
                                        Stage popup = new Stage();
                                        popup.setScene(scene);
                                        popup.initModality(Modality.APPLICATION_MODAL);
                                        popup.setOnShown(se -> {
                                            controller.startCountdown();
                                        });
                                        popup.setOnHidden(he -> {
                                            try {
                                                controller.clearListener();
                                            } catch (SerialPortException e) {
                                                throw new RuntimeException(e);
                                            }
                                            if(!controller.failed()) {
                                                medicationArray[finalIndex].popCount();
                                            } else {
                                                uploadFailure(m.getName());
                                            }
                                            try {
                                                uploadJSON();
                                            } catch (IOException e) {
                                                System.out.println("writing error");
                                            }
                                        });
                                        popup.show();
                                    }catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        ));
                        System.out.println(
                                m.getName()
                                        + " Scheduled for "
                                        + time
                                        + " (In "
                                        + duration.toSeconds() +
                                        " seconds...)"
                        );
                        numScheduled++;
                    }
                }
            }
        }
        if(!timeline.getKeyFrames().isEmpty()) {
            timeline.playFromStart();
        }
    }

    private void uploadFailure(String name) {
        LocalDateTime now = LocalDateTime.now();
        NetworkClient client = new NetworkClient();
        if(client.postMedicationFail(name, now)) {

        }
    }

    public int checkoutCanister() {
        for(int i = 0; i < medicationArray.length; i++) {
            if(medicationArray[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void addNewMedication(Medication medication) {
        medicationArray[medication.getCanisterNumber()] = medication;
        try {
            uploadJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populateMedicationMap(JSONObject content) {
        Iterator<String> keys = content.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            if (content.get(key) instanceof JSONObject entry) {
                Medication medication = new Medication(
                        key,
                        entry.getInt("count"),
                        entry.getJSONArray("days"),
                        entry.getJSONArray("hours")
                );
                medication.setCanisterNumber(entry.getInt("canister"));
                medicationArray[medication.getCanisterNumber()] = medication;
            }
        }
    }

    private JSONObject openJSONFile(String path) throws IOException {
            String stream = new String(Files.readAllBytes(Paths.get(path)));
            if(stream.length() > 0) {
                return new JSONObject(stream);
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

    public JSONObject wrapMedicationsToJSON() {
        JSONObject sub = new JSONObject();
        for(Medication value: medicationArray) {
            if(value != null) {
                sub.put(value.getName(), value.toJSON());
            }
        }
        return sub;
    }

    public JSONObject wrapMedicationsToFullJSON() {
        JSONObject sub = new JSONObject();
        JSONObject top = new JSONObject();
        for(Medication value: medicationArray) {
            if(value != null) {
                sub.put(value.getName(), value.toJSON());
            }
        }
        top.put("medication", sub);
        return top;
    }

    public void writeToLocalJSON() throws IOException {
        FileWriter writer = new FileWriter(JSON_PATH);
        writer.write(wrapMedicationsToFullJSON().toString());
        writer.close();
    }

    public void uploadJSON() throws IOException {
        NetworkClient client = new NetworkClient();
        if(client.putMedicationFile(wrapMedicationsToJSON())) {
            System.out.println("Upload complete!");
        } else {
            System.out.println("Upload failed! Writing to local file...");
            writeToLocalJSON();
        }
    }


}
