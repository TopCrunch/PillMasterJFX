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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    private double weight = 0;

    private int canisterNumber = -1;
    private ArrayList<DayOfWeek> dayOfWeek;

    public Medication(String name, int count, ArrayList<DayOfWeek> dayList,
                      ArrayList<HourMinuteCounter> timeList) {
        this.name = name;
        this.count = count;
        this.dayOfWeek = new ArrayList<>(dayList);
        this.timeList = new ArrayList<>(timeList);
    }

    public Medication(String name, int count, JSONArray dayList,
                      JSONArray timeList) {
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
        this.dayOfWeek = tmpDayList;
        this.timeList = tmpTimeList;
    }

    @Override
    public String toString() {
        return String.format("%s, %d, %.2f, can %d", name,count,
                weight,canisterNumber);
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
        value.put("count", getCount());
        value.put("days", days);
        value.put("hours", hours);
        value.put("canister", canisterNumber);
        value.put("weight", weight);
        return value;
    }

    public ArrayList<HourMinuteCounter> getTimeList() {
        return new ArrayList<>(timeList);
    }

    public ArrayList<DayOfWeek> getDaysOfWeek() {
        return new ArrayList<>(dayOfWeek);
    }

    public void setCanisterNumber(int i) {
        canisterNumber = i;
    }

    public int getCanisterNumber() {
        return canisterNumber;
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
}
