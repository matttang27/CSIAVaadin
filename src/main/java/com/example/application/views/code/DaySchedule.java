package com.example.application.views.code;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;


public class DaySchedule implements Serializable {
    private LocalDate day;
    private DayStat dayStat;
    private ArrayList<Event> events = new ArrayList<Event>();
    private DayStat stats;
    public DaySchedule(LocalDate day) {
        this.day = day;
    }

    public LocalDate getDay() {
        return this.day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public ArrayList<Event> getEvents() {
        ArrayList<Event> copy = new ArrayList<Event>();
        for (Event e: events) {
            copy.add(e);
        }
        copy.sort((e1,e2) -> {
            return (int) Duration.between(e2.getStartTime(),e1.getStartTime()).toMillis();
        });
        return copy;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        event.setId(this.getEvents().size());
        events.add(event);
    }

    public void deleteEvent(Event event) {
        for (Event e: events) {
            if (e.getId() == event.getId()) {
                events.remove(e);
            }
        }
    }
    public Event getFromId(int id) {
        for (Event e: events) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    public DayStat getStats() {
        return this.stats;
    }

    public void setStats(DayStat stats) {
        this.stats = stats;
    }
    public ArrayList<Event> copyEvents() {
        ArrayList<Event> copy = new ArrayList<Event>();
        for (Event e: events) {
            copy.add(e.copy());
        }
        return copy;
    }


    public Event timeFree(LocalTime startTime,LocalTime endTime) {
        for (int i=0;i<events.size();i++) {
            if (events.get(i).getStartTime().isBefore(startTime) && events.get(i).getEndTime().isAfter(startTime) || events.get(i).getStartTime().isBefore(endTime) && events.get(i).getEndTime().isAfter(endTime)) {
                return events.get(i);
            };
        }
        return null;
    }
}
