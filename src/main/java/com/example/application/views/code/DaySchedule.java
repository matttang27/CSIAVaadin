package com.example.application.views.code;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class DaySchedule {
    private LocalDate day;
    private ArrayList<Event> events = new ArrayList<Event>();
    private DayStat stats;
    private Manager manager;
    public DaySchedule(Manager manager, LocalDate day) {
        this.manager = manager;
        this.day = day;
    }

    public LocalDate getDay() {
        return this.day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public ArrayList<Event> getEvents() {
        return this.events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public DayStat getStats() {
        return this.stats;
    }

    public void setStats(DayStat stats) {
        this.stats = stats;
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Event timeFree(LocalTime time) {
        for (int i=0;i<events.size();i++) {
            if (events.get(i).getStartTime().isBefore(time) && events.get(i).getEndTime().isAfter(time)) {
                return events.get(i);
            };
        }
        return null;
    }
}
