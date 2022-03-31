package com.example.application.views.code;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.vaadin.flow.component.icon.Icon;

public class Group implements Serializable {
    private String name = "";
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime lastEdited = LocalDateTime.now();
    //optional
    private int goal = 0;
    private String color = "#808080";
    private int id;
    private Icon icon;
    private String notes = "";

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }



    public Icon getIcon() {
        return this.icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Group() {
    }

    public Group(String name) {
        this.name = name;
        this.created = LocalDateTime.now();
        this.lastEdited = LocalDateTime.now();
    }

    public Group(String name, ArrayList<Task> tasks, LocalDateTime created, LocalDateTime lastEdited, int goal, String color) {
        this.name = name;
        this.tasks = tasks;
        this.created = created;
        this.lastEdited = lastEdited;
        this.goal = goal;
        this.color = color;
    }
    
    public LocalDateTime getLastEdited() {
        return this.lastEdited;
    }

    public void setLastEdited(LocalDateTime lastEdited) {
        this.lastEdited = lastEdited;
    }

    public int getGoal() {
        return this.goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
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
