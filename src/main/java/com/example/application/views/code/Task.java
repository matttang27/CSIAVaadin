package com.example.application.views.code;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.awt.Color;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
public class Task implements Serializable {
    private String name;
    private LocalDateTime created;
    private LocalDateTime lastEdited;
    private LocalDateTime start;
    private LocalDateTime nextDue;
    private Group group;
    private String cronJob;
    private int priority;
    private boolean done;
    private Color color;
    private String background;
    private String icon;
    private Task parent;
    private String notes;
    private int id;
    private int weight;
    private int estimatedTime;
    private ArrayList<Task> children;
    //originally Group / Task, may change it to entry / task
    private String type;

    //Why do i have 3 constructors? ... that's a good question.

    public Task(String name, LocalDateTime created, LocalDateTime lastEdited, LocalDateTime start, LocalDateTime nextDue, Group group, String cronJob, int priority, boolean done, Color color, String background, String icon, Task parent, String notes, int id, int weight, int estimatedTime, ArrayList<Task> children, String type) {
        this.name = name;
        this.created = created;
        this.lastEdited = lastEdited;
        this.start = start;
        this.nextDue = nextDue;
        this.group = group;
        this.cronJob = cronJob;
        this.priority = priority;
        this.done = done;
        this.color = color;
        this.background = background;
        this.icon = icon;
        this.parent = parent;
        this.notes = notes;
        this.id = id;
        this.weight = weight;
        this.estimatedTime = estimatedTime;
        this.children = children;
        this.type = type;
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
    public Task(String name,LocalDateTime due,int priority,int estimatedTime,Group group) {
        this.name = name;
        this.nextDue = due;
        this.priority = priority;
        this.estimatedTime = estimatedTime;
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

    public int daysTillDue() {
        LocalDate taskDay = this.nextDue.toLocalDate();
        int dayDiff = (int) ChronoUnit.DAYS.between(LocalDate.now(), taskDay);
        return dayDiff;
    }

    public double calculateScore(double a,double b,double c) {
        //currently, the calculation is as follows:
        //Average of Urgency, Priority and Time
        //Urgency: 100-(a^(u-5)), where u is the # days until the task is due
        //Priority: 10*(p^b), where p is the priority from 0-10.
        //Time: 80*(1/(1+(1.1^((t*c)-50))))+20, where t is the estimated time in minutes.
        //a,b,c are constants determined by the user in their settings.
        //graphs can be seen at https://www.desmos.com/calculator/chyzuxo5yo
        if (this.isDone()) {
            return 0;
        }
        double urgency = 100-(Math.pow(a,((double) this.daysTillDue()-5)));
        double priority = 10*Math.pow(this.priority,b);
        double time = 80*(1/(1+Math.pow(1.1,(this.estimatedTime*c)-50))) + 20;
        return (urgency+priority+time) / 3;
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

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getEstimatedTime() {
        return this.estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
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

    
        return new Task(name, created, lastEdited, start, nextDue, group, cronJob, priority, done, color, background, icon, parent, notes, id, weight, estimatedTime, children, type);
    }

}
