package application.views.code;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;


public class DaySchedule implements Serializable {
private static final long serialVersionUID = 1L;
    private LocalDate day;
    private DayStat dayStat;
    private ArrayList<Event> events = new ArrayList<Event>();
    private DayStat stats;
    public DaySchedule(LocalDate day) {
        this.day = day;
    }

    public LocalDate getDay() {
        return this.day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public ArrayList<Event> getEvents() {
        sortEvents();
        ArrayList<Event> copy = new ArrayList<Event>();
        for (Event e: events) {
            copy.add(e);
        }
        return copy;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        event.setId(this.getEvents().size());
        events.add(event);
    }

    public void deleteEvent(Event event) {
        for (Event e: events) {
            if (e.getId() == event.getId()) {
                events.remove(e);
            }
        }
    }
    public Event getFromId(int id) {
        for (Event e: events) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    public DayStat getStats() {
        return this.stats;
    }

    public void setStats(DayStat stats) {
        this.stats = stats;
    }
    public ArrayList<Event> copyEvents() {
        ArrayList<Event> copy = new ArrayList<Event>();
        for (Event e: events) {
            copy.add(e.copy());
        }
        return copy;
    }


    public Event timeFree(LocalTime startTime,LocalTime endTime) {
        for (int i=0;i<events.size();i++) {
            if (events.get(i).getStartTime().isBefore(startTime) && events.get(i).getEndTime().isAfter(startTime) || events.get(i).getStartTime().isBefore(endTime) && events.get(i).getEndTime().isAfter(endTime)) {
                return events.get(i);
            };
        }
        return null;
    }

    public void sortEvents() {
        events.sort((e1,e2) -> {
            return (int) Duration.between(e2.getStartTime(),e1.getStartTime()).toMillis();
        });
    } 

    public void generatePomodoro(int pomodoros, TaskManager taskManager) {
        
        //step 1: sort events
        sortEvents();
        //step 2: fill gaps

        ArrayList<LocalTime> gapStarts = new ArrayList<>();
        ArrayList<LocalTime> gapEnds = new ArrayList<>();
        ArrayList<Integer> gapPotential = new ArrayList<>();
        for (int i=0;i<events.size();i++) {
            if (events.get(i).getEndTime().equals(LocalTime.of(23,59))) {break;};
            
            if (i+1 == events.size()) {
                if (Duration.between(events.get(i).getEndTime(),LocalTime.MAX).toMinutes() >= 30) {
                    gapStarts.add(events.get(i).getEndTime());
                    gapEnds.add(LocalTime.MAX);
                }
                
            }
            else {
                if (Duration.between(events.get(i).getEndTime(),events.get(i+1).getStartTime()).toMinutes() >= 30) {
                    gapStarts.add(events.get(i).getEndTime());
                    gapEnds.add(events.get(i+1).getStartTime());
                }
                
            }
            
        }
        //find the potential pomodoros for each "gap"
        for (int i=0;i<gapStarts.size();i++) {
            int a = (int) Duration.between(gapStarts.get(i),gapEnds.get(i)).toMillis() / 60000;
            int potential = a / 30;
            //for every 4 pomodoros we need a break
            potential = potential / 5 * 4 + potential % 5;
            gapPotential.add(potential);
            
        }
        //get list of tasks, sorted by task score
        ArrayList<Task> copy = taskManager.sort(taskManager.getTasks(),"taskScore",false);
        copy = taskManager.removeDone(copy);
        for (Task t: copy) {
            int required = (int) Math.ceil(t.getEstimatedTime() / 25.0);
            int counter = 1;
            
            for (int j=0;j<gapStarts.size();j++) {
                int c = required;
                for (int i=0;i<c;i++) {
                    if (pomodoros == 0) {
                        return;
                    }
                    if (gapPotential.get(j) == 0) {
                        break;
                    }
                    gapPotential.set(j,gapPotential.get(j) - 1);
                    Event newEvent = new Event("Pomodoro #" + counter + " for " + t.getName(),gapStarts.get(j),gapStarts.get(j).plus(Duration.ofMinutes(30)));
                    newEvent.setTask(t);
                    events.add(newEvent);
                    counter += 1;
                    pomodoros -= 1;
                    required -= 1;

                    gapStarts.set(j,gapStarts.get(j).plus(Duration.ofMinutes(30)));
                    if (gapPotential.get(j) == 0) {
                        gapStarts.remove(j);
                        gapPotential.remove(j);
                        gapEnds.remove(j);
                    }
                }
                    
                    
                    
            }
        }
        

        




        
    }
}
