package com.example.pillmasterjfx;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;

public class Medication {
    private ArrayList<HourMinuteCounter> timeList;
    private final String name;
    private int count;
    private ArrayList<DayOfWeek> dayOfWeek;
    public enum Schedule {
        DAILY,
        TWICE(),
        THRICE
    }
    private Schedule schedule;

    public Medication(String name, int count,
                      Schedule schedule, ArrayList<DayOfWeek> dayList,
                      ArrayList<HourMinuteCounter> timeList) {
        this.name = name;
        this.count = count;
        this.schedule = schedule;
        this.dayOfWeek = new ArrayList<>(dayList);
        this.timeList = new ArrayList<>(timeList);
    }

    public Medication(String name, int count, String schedule,
                      ArrayList<DayOfWeek> dayList,
                      ArrayList<HourMinuteCounter> timeList) {
        this.name = name;
        this.count = count;
        this.schedule = Schedule.valueOf(schedule);
        this.dayOfWeek = new ArrayList<>(dayList);
        this.timeList = new ArrayList<>(timeList);
    }

    public Medication(String name, int count,
                      String schedule, JSONArray dayList, JSONArray timeList) {
        ArrayList<DayOfWeek> tmpDayList = new ArrayList<>();
        ArrayList<HourMinuteCounter> tmpTimeList = new ArrayList<>();
        for(int i = 0; i < dayList.length(); i++) {
            tmpDayList.add(DayOfWeek.valueOf(dayList.get(i).toString()));
        }
        for(int i = 0; i < timeList.length(); i++) {
            tmpTimeList.add(
                    new HourMinuteCounter(
                            Integer.parseInt(timeList.get(i).toString())
                    )
            );
        }
        this.name = name;
        this.count = count;
        this.schedule = Schedule.valueOf(schedule);
        this.dayOfWeek = tmpDayList;
        this.timeList = tmpTimeList;
    }

    @Override
    public String toString() {
        return String.format("%s, %d, %s",name,count,schedule.toString());
    }

    public JSONObject toJSON() {
        JSONObject value = new JSONObject();
        JSONArray days = new JSONArray();
        for(DayOfWeek d : dayOfWeek) {
            days.put(d.toString());
        }
        JSONArray hours = new JSONArray();
        for(HourMinuteCounter h : timeList){
            hours.put(h.getTrueTime());
        }
        value.put("name", getName());
        value.put("count", getCount());
        value.put("schedule", getSchedule().toString());
        value.put("days", days);
        value.put("hours", hours);
        return value;
    }

    public ArrayList<HourMinuteCounter> getTimeList() {
        return new ArrayList<>(timeList);
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
