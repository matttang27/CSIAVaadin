package com.example.application.views.code;

import java.time.LocalDate;
import java.util.HashMap;

public class ScheduleManager {
    private HashMap<LocalDate,DaySchedule> days;
    private Manager manager;
    public ScheduleManager(Manager manager) {
        this.manager = manager;
        this.days = new HashMap<LocalDate,DaySchedule>();
    }

    public HashMap<LocalDate,DaySchedule> getDays() {
        return this.days;
    }

    public void setDays(HashMap<LocalDate,DaySchedule> days) {
        this.days = days;
    }
    public boolean dayExists(LocalDate day) {
        return days.keySet().contains(day);
    }
    public void addDay(LocalDate day) {
        if (dayExists(day)) {return;};
        DayStat dayStat = new DayStat(day);
        DaySchedule daySchedule = new DaySchedule(day);
        dayStat.setDaySchedule(daySchedule);
        daySchedule.setStats(dayStat);

        days.put(day,daySchedule);
        HashMap<LocalDate,DayStat> data = manager.getStater().getData();
        data.put(day,dayStat);
        
    }

    public DaySchedule getDay(LocalDate day) {
        return days.get(day);
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

}