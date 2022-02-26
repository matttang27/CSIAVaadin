package com.example.application.views.code;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Group {
    String name;
    ArrayList<Task> tasks;
    LocalDateTime created;
    //optional
    int goal;
    
    public Group(String name) {
        this.name = name;
        this.tasks = new ArrayList<Task>();
        this.created = LocalDateTime.now();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

}
