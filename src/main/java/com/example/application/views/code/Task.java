package com.example.application.views.code;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.awt.Color;
import java.time.Instant;
import java.time.LocalDate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
public class Task {
    String name;
    LocalDateTime created;
    LocalDateTime lastEdited;
    LocalDateTime start;
    LocalDateTime nextDue;

    Group group;
    String cronJob;
    int priority;
    boolean done;
    Color color;
    String background;
    String icon;
    Task parent;
    String notes;
    int id;
    ArrayList<Task> children;
    //originally Group / Task, may change it to entry / task
    String type;
    //respective entry in calendar
    Entry entry;

    

    //Why do i have 3 constructors? ... that's a good question.

    public Task(String name, LocalDateTime created, LocalDateTime lastEdited, LocalDateTime start, LocalDateTime nextDue, String cronJob, int priority, boolean done, Color color, String background, String icon, Task parent, String notes, int id, ArrayList<Task> children, String type, Entry entry, Group group) {
        this.name = name;
        this.created = created;
        this.lastEdited = lastEdited;
        this.start = start;
        this.nextDue = nextDue;
        this.cronJob = cronJob;
        this.priority = priority;
        this.done = done;
        this.color = color;
        this.background = background;
        this.icon = icon;
        this.parent = parent;
        this.notes = notes;
        this.id = id;
        this.children = children;
        this.type = type;
        this.entry = entry;
        this.group = group;
    }
    
    public Task() {
        name="Blank Task";
        color=null;
        background="";
        icon="";
        notes="";
        created = LocalDateTime.now();
        lastEdited = LocalDateTime.now();
        done = false;
        children = new ArrayList<Task>();
        type = "task";
        start = LocalDateTime.now();
    }
    //creation from Add Task Button
    public Task(String name,LocalDateTime due,int priority,Group group) {
        this.name = name;
        this.nextDue = due;
        this.priority = priority;
        this.group = group;
        color=null;
        background="";
        icon="";
        notes="";
        created = LocalDateTime.now();
        lastEdited = LocalDateTime.now();
        start = LocalDateTime.now();
        done = false;
        children = new ArrayList<Task>();
        type = "task";
    }

    public Group getGroup() {
        return this.group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastEdited() {
        return this.lastEdited;
    }

    public void setLastEdited(LocalDateTime lastEdited) {
        this.lastEdited = lastEdited;
    }

    public LocalDateTime getNextDue() {
        return this.nextDue;
    }

    public String getNextDueString() {
        if (this.nextDue == null) {
            return "";
        }
        return this.nextDue.toString();
    }

    public void setNextDue(LocalDateTime nextDue) {
        this.nextDue = nextDue;
    }

    public void setNextDue(LocalDate nextDue) {
        this.nextDue = nextDue.atTime(23,59,59);
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setStart(LocalDate start) {
        this.start = start.atTime(0,0,0);
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public Entry getEntry() {
        return this.entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }


    public String getCronJob() {
        return this.cronJob;
    }

    public void setCronJob(String cronJob) {
        this.cronJob = cronJob;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return this.done;
    }

    public boolean getDone() {
        return this.done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getBackground() {
        return this.background;
    }


    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", created='" + getCreated() + "'" +
            ", lastEdited='" + getLastEdited() + "'" +
            ", start='" + getStart() + "'" +
            ", nextDue='" + getNextDue() + "'" +
            ", group='" + getGroup() + "'" +
            ", cronJob='" + getCronJob() + "'" +
            ", priority='" + getPriority() + "'" +
            ", done='" + isDone() + "'" +
            ", color='" + getColor() + "'" +
            ", background='" + getBackground() + "'" +
            ", icon='" + getIcon() + "'" +
            ", parent='" + getParent() + "'" +
            ", notes='" + getNotes() + "'" +
            ", id='" + getId() + "'" +
            ", children='" + getChildren() + "'" +
            ", type='" + getType() + "'" +
            ", entry='" + getEntry() + "'" +
            "}";
    }


    public void setBackground(String background) {
        this.background = background;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Task getParent() {
        return this.parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public ArrayList<Task> getChildren() {
        return this.children;
    }

    public void setChildren(ArrayList<Task> children) {
        this.children = children;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Task clone(){ 
        //thank god for vscode
        return new Task(name, created, lastEdited, start, nextDue, cronJob, priority, done, color, background, icon, parent, notes, id, children, type,entry,group);
    }

}
