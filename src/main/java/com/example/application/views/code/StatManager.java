package com.example.application.views.code;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map.Entry;

public class StatManager {
    private HashMap<LocalDate,DayStat> data;
    private Manager manager;
    public StatManager(Manager manager) {
        data = new HashMap<LocalDate,DayStat>();
        this.manager = manager;
    }

    public HashMap<LocalDate,DayStat> getData() {
        return this.data;
    }

    public void setData(HashMap<LocalDate,DayStat> data) {
        this.data = data;
    }

    public boolean dayExists(LocalDate day) {
        return data.keySet().contains(day);
    }

    public void addDay(LocalDate day) {
        if (dayExists(day)) {return;};
        DayStat dayStat = new DayStat(day);
        DaySchedule daySchedule = new DaySchedule(day);
        dayStat.setDaySchedule(daySchedule);
        daySchedule.setStats(dayStat);

        data.put(day,dayStat);
        HashMap<LocalDate,DaySchedule> scheduleData = manager.getScheduler().getDays();
        scheduleData.put(day,daySchedule);
    }
    

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
    
}
