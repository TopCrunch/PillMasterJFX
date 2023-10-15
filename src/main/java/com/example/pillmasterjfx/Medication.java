package com.example.pillmasterjfx;

import org.json.JSONObject;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;

public class Medication {
    private final String name;
    private int count;
    private ArrayList<DayOfWeek> dayOfWeek;
    public enum Schedule {
        DAILY,
        TWICE(),
        THRICE
    }
    private Schedule schedule;

    public Medication(String name, int count, Schedule schedule) {
        this.name = name;
        this.count = count;
        this.schedule = schedule;
        if(schedule == Schedule.DAILY) {
            dayOfWeek = new ArrayList<>(Arrays.asList(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
            ));
        }
    }

    public Medication(String name, int count, String schedule) {
        this(name, count, Schedule.valueOf(schedule));
    }

    @Override
    public String toString() {
        return String.format("%s, %d, %s",name,count,schedule.toString());
    }

    public JSONObject toJSON() {
        JSONObject value = new JSONObject();
        value.put("name", getName());
        value.put("count", getCount());
        value.put("schedule", getSchedule().toString());
        return value;
    }

    public ArrayList<DayOfWeek> getDaysOfWeek() {
        return new ArrayList<>(dayOfWeek);
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void popCount() {
        count--;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
