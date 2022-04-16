package application.views.code;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;

public class StatManager implements Serializable {
private static final long serialVersionUID = 1L;
    
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
    public DayStat getDay (LocalDate day) {
        DayStat findDay = data.get(day);
        if (findDay == null) {
            addDay(day);
            findDay = data.get(day);
        }
        return findDay;

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

    public int getTotalCompleted() {
        int completed = 0;
        for (DayStat d: data.values()) {
            completed += d.getTasksCompleted();
        }
        return completed;
    }

    public int getTotalCreated() {
        int created = 0;
        for (DayStat d: data.values()) {
            created += d.getTasksCreated();
        }
        return created;
    }

    //gets streak starting from specific day
    public int getStreak(LocalDate day) {
        int streak = 0;
        
        while (true) {
            DayStat loopStat = getDay(day.minusDays(streak));
            if (loopStat.getTasksCompleted() > 0 || loopStat.getTasksCreated() > 0) {
                streak += 1;
            }
            else {
                return streak;
            }
        }
    }

    public LocalDate getLongestStreak() {
        int longest = 0;
        LocalDate longestStat = LocalDate.now();
        for (DayStat d: data.values()) {
            if (getStreak(d.getDay()) > longest) {
                longestStat = d.getDay();
                longest = getStreak(d.getDay());
            }
        }
        return longestStat;
    }
    
}
