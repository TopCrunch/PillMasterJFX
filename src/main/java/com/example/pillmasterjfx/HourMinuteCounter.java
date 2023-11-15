package com.example.pillmasterjfx;

import javafx.util.Duration;
import java.time.LocalTime;

public class HourMinuteCounter {

    private int minute;
    private int hour;

    public String toString() {
        if(isPM()) {
            return hour + ":" + minute + "PM";
        } else{
            return hour + ":" + minute + "AM";
        }
    }

    public int getTrueTime() {
        return (hour*100) + minute;
    }

    public int getHour() {
        if(isPM() && hour != 12) {
            return hour - 12;
        } else if(hour == 0){
            return 12;
        } else {
            return hour;
        }
    }

    public Duration getDurationToTime() {
        LocalTime time = LocalTime.now();
        Duration now =
                Duration.hours(time.getHour())
                        .add(Duration.minutes(time.getMinute()))
                        .add(Duration.seconds(time.getSecond()));
        Duration duration = Duration.hours(hour).add(Duration.minutes(minute));
        return duration.subtract(now);
    }

    public int getMinute() {
        return minute;
    }

    public boolean isPM() {
        if(hour > 11) {
            return true;
        } else {
            return false;
        }
    }

    public HourMinuteCounter() {
        hour = 0;
        minute = 0;
    }

    //24 hour time 1933 hours = 7:33 PM
    public HourMinuteCounter(int fullTime) {
        this.hour = fullTime/100;
        this.minute = fullTime%100;
    }

    public void incrementHour() {
        if(hour >= 23) {
            hour = 0;
        } else {
            hour++;
        }
    }

    public void decrementHour() {
        if(hour <= 0) {
            hour = 23;
        } else {
            hour--;
        }
    }

    public void incrementMinuteTen() {
        if(minute >= 50) {
            minute = 0;
        } else {
            minute += 10;
        }
    }

    public void decrementMinuteTen() {
        if(minute <= 0) {
            minute = 50;
        } else {
            minute -= 10;
        }
    }
}
