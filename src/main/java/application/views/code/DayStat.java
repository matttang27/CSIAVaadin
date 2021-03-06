package application.views.code;

import java.io.Serializable;
import java.time.LocalDate;

public class DayStat implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate day;
    private DaySchedule daySchedule;
    private int tasksCompleted = 0;
    private int tasksCreated = 0;

    public DaySchedule getDaySchedule() {
        return this.daySchedule;
    }

    public void setDaySchedule(DaySchedule daySchedule) {
        this.daySchedule = daySchedule;
    }

    public DayStat(LocalDate day) {
        this.day = day;
    }

    public DayStat(LocalDate day, int tasksCompleted, int tasksCreated) {
        this.day = day;
        this.tasksCompleted = tasksCompleted;
        this.tasksCreated = tasksCreated;
    }

    public LocalDate getDay() {
        return this.day;
    }

    public void setDay(LocalDate day) {
        this.day = day;

    }

    public int getTasksCompleted() {
        return this.tasksCompleted;
    }

    public void setTasksCompleted(int tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }

    public int getTasksCreated() {
        return this.tasksCreated;
    }

    public void setTasksCreated(int tasksCreated) {
        this.tasksCreated = tasksCreated;
    }

}
