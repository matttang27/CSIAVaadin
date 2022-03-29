package com.example.application.views.code;

import java.time.Duration;
import java.time.LocalTime;

public class Event {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private Duration timeSpent;
    private boolean doing;
    

    public Event(String name, LocalTime startTime, LocalTime endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        timeSpent = Duration.ZERO;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isDoing() {
        return this.doing;
    }

    public boolean getDoing() {
        return this.doing;
    }

    public void setDoing(boolean doing) {
        this.doing = doing;
    }


    public Duration getTimeSpent() {
        return this.timeSpent;
    }

    public void setTimeSpent(Duration timeSpent) {
        this.timeSpent = timeSpent;
    }
    
}
