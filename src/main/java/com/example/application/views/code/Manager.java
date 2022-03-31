package com.example.application.views.code;
import java.util.ArrayList;
import java.io.Serializable;
public class Manager implements Serializable {


    //currently this is so that everything can be connected to one spot, but not sure if I really need that
    private TaskManager tasker;
    private GroupManager grouper;
    private StatManager stater;
    private User user;
    private StatManager stats;
    private SettingsManager settings;
    private ScheduleManager scheduler;

    public SettingsManager getSettings() {
        return this.settings;
    }

    public void setSettings(SettingsManager settings) {
        this.settings = settings;
    }

    public GroupManager getGrouper() {
        return this.grouper;
    }

    public void setGrouper(GroupManager grouper) {
        this.grouper = grouper;
    }

    public ScheduleManager getScheduler() {
        return this.scheduler;
    }

    public void setSchedule(ScheduleManager scheduler) {
        this.scheduler = scheduler;
        
    }
    public Manager() {
        tasker = new TaskManager(this);
        grouper = new GroupManager(this);
        stater = new StatManager(this);
        scheduler = new ScheduleManager(this);
        settings = new SettingsManager(this);
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
