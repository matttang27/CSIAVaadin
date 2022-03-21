package com.example.application.views.code;

import java.time.LocalDate;

public class DayStat {
    private LocalDate day;
    private int tasksCompleted = 0;
    private int tasksCreated = 0;


    public DayStat(LocalDate day) {
        this.day = day;
    }

    public DayStat(LocalDate day, int tasksCompleted, int tasksCreated) {
        this.day = day;
        this.tasksCompleted = tasksCompleted;
        this.tasksCreated = tasksCreated;
    }

    public LocalDate getDay() {
        return this.day;
    }

    public void setDay(LocalDate day) {
        this.day = day;

    
    }

    public int getTasksCompleted() {
        return this.tasksCompleted;
    }

    public void setTasksCompleted(int tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }

    public int getTasksCreated() {
        return this.tasksCreated;
    }

    public void setTasksCreated(int tasksCreated) {
        this.tasksCreated = tasksCreated;
    }

}
