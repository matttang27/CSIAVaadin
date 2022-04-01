package com.example.application.views.code;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Event implements Serializable,Cloneable {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime doingStart = null;
    private Duration timeSpent = Duration.ZERO;
    private boolean doing;
    private Task task;
    private int estimatedTime;
    private int priority;
    private int id;
    

    public Event(String name, LocalTime startTime, LocalTime endTime, Task task, Duration timeSpent) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeSpent = timeSpent;
        this.task = task;
    }
    public Event() {

    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEstimatedTime() {
        return this.estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public LocalDateTime getDoingStart() {
        return this.doingStart;
    }

    public void setDoingStart(LocalDateTime doingStart) {
        this.doingStart = doingStart;
    }
    public Event copy() {
        try {
            return (Event) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
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
    public String getTimeSpentString() {
        long s = this.timeSpent.getSeconds();
        return String.format("%02d:%02d", s / 3600, (s % 3600) / 60);
    }

    public void setTimeSpent(Duration timeSpent) {
        this.timeSpent = timeSpent;
    }
    
}
