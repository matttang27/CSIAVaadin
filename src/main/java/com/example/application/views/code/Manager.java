package com.example.application.views.code;
import java.util.ArrayList;
public class Manager {


    //currently this is so that everything can be connected to one spot, but not sure if I really need that
    TaskManager tasker;
    GroupManager grouper;
    StatManager stater;
    User user;
    StatManager stats;

    public GroupManager getGrouper() {
        return this.grouper;
    }

    public void setGrouper(GroupManager grouper) {
        this.grouper = grouper;
    }

    public ArrayList<Schedule> getSchedule() {
        return this.schedule;
    }

    public void setSchedule(ArrayList<Schedule> schedule) {
        this.schedule = schedule;
    }
    ArrayList<Schedule> schedule;
    public Manager() {
        tasker = new TaskManager(this);
        grouper = new GroupManager(this);
        stater = new StatManager(this);
        schedule = new ArrayList<Schedule>();
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TaskManager getTasker() {
        return this.tasker;
    }

    public void setTasker(TaskManager tasker) {
        this.tasker = tasker;
    }
    
    @Override
    public String toString() {
        return "{" +
            " tasker='" + getTasker() + "'" +
            "}";
    }

    public StatManager getStater() {
        return this.stater;
    }

    public void setStater(StatManager stater) {
        this.stater = stater;
    }

    public StatManager getStats() {
        return this.stats;
    }

    public void setStats(StatManager stats) {
        this.stats = stats;
    }

}
