package application.views.code;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleManager implements Serializable {
private static final long serialVersionUID = 1L;
    
    private HashMap<LocalDate,DaySchedule> days;
    private Manager manager;
    private LocalDate DEFAULT = LocalDate.of(2000, 1, 1);
    public ScheduleManager(Manager manager) {
        this.manager = manager;
        this.days = new HashMap<LocalDate,DaySchedule>();
        this.days.put(DEFAULT,new DaySchedule(DEFAULT));
        
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
        daySchedule.setEvents(days.get(DEFAULT).copyEvents());
        daySchedule.setStats(dayStat);

        days.put(day,daySchedule);
        HashMap<LocalDate,DayStat> data = manager.getStater().getData();
        data.put(day,dayStat);
        
    }

    public void resetDay(LocalDate day) {
        if (!dayExists(day)) {return;}
        DaySchedule daySchedule = this.getDay(day);
        for (Event e: days.get(DEFAULT).getEvents()) {
            daySchedule.addEvent(e.copy());
        }
        
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