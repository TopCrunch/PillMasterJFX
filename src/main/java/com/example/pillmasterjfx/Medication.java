package com.example.pillmasterjfx;

public class Medication {
    private final String name;
    private int count;
    public enum Schedule {
        DAILY,
        TWICE,
        THRICE
    }
    private Schedule schedule;

    public Medication(String name, int count, Schedule schedule) {
        this.name = name;
        this.count = count;
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return String.format("%s, %d, %s",name,count,schedule.toString());
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
