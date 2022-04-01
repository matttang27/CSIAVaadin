package application.views.code;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TaskManager implements Serializable {
    ArrayList<Task> tasks;
    Manager manager;

    TaskManager(Manager manager) {
        tasks = new ArrayList<Task>();
        this.manager = manager;
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        task.setId(tasks.size());
        tasks.add(task);
    }

    public Task blankTask() {
        Task t = new Task();
        t.setId(tasks.size());
        tasks.add(t);
        return t;
    }


    public int getIndex(int id) {
        for (int i=0;i<tasks.size();i++) {
            if (tasks.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public int getIndex(Task task) {
        for (int i=0;i<tasks.size();i++) {
            if (tasks.get(i).getId() == task.getId()) {
                return i;
            }
        }
        return -1;
    }
    

    public Task getTask(int id) {
        for (int i=0;i<tasks.size();i++) {
            if (tasks.get(i).getId() == id) {
                return tasks.get(i);
            }
        }
        return null;
    }

    public void removeTask(Task task) {
        for (int i=0;i<tasks.size();i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.remove(i);
            }
        }
    }

    

    /*
     * 
     * 
     * static ArrayList<Task> filterByGroup(ArrayList<Task> tasks, ArrayList<String>
     * groups) {
     * if (groups.equals("")) {return tasks;};
     * ArrayList<Task> filtered = new ArrayList<Task>();
     * tasks.forEach((task) -> {
     * if (groups.contains(task.group)) {
     * filtered.add(task);
     * }
     * });
     * return filtered;
     * 
     * }
     */


    ArrayList<Task> sort(ArrayList<Task> tasks, String sortType, Boolean ascending) {
        ArrayList<Task> sorted = tasks;


        //uses bubble sort
        boolean flag = false;

        //for taskScore only
        SettingsManager settings = this.manager.getSettings();
        double a = settings.getScoreA();
        double b = settings.getScoreB();
        double c = settings.getScoreC();

        for (int i=0;i<tasks.size()-1;i++) {
            flag = false;

            for (int j=0;j<tasks.size()-1;j++) {
                System.out.println(String.format("%d,%d",i,j));
                boolean greater = false;
                if (sortType == "taskName") {
                    greater = tasks.get(j).getName().compareToIgnoreCase(tasks.get(j+1).getName()) > 0;
                }
                else if (sortType == "day") {
                    greater = tasks.get(j).getNextDue().compareTo(tasks.get(j+1).getNextDue()) > 0;
                }
                else if (sortType == "priority") {
                    greater = tasks.get(j).getPriority() > tasks.get(j+1).getPriority();
                }
                else if (sortType == "created") {
                    greater = tasks.get(j).getCreated().compareTo(tasks.get(j+1).getCreated()) > 0;
                }
                else if (sortType == "estimatedTime") {
                    greater = tasks.get(j).getEstimatedTime() > tasks.get(j+1).getEstimatedTime();
                }
                else if (sortType == "taskScore") {
                    
                    greater = tasks.get(j).calculateScore(a,b,c) > tasks.get(j+1).calculateScore(a,b,c);
                }
                else {
                    System.out.println("sortType was not one of the specified sortTypes");
                }

                if (greater) {
                    Task temp = tasks.get(j);
                    tasks.set(j,tasks.get(j+1));
                    tasks.set(j+1,temp);
                    flag = true;
                }

            }
            if (!flag) {
                break;
            }
        }

        if (!ascending) {
            Collections.reverse(sorted);
        }

        return sorted;

    }

    @Override
    public String toString() {
        return "{" +
                " tasks='" + getTasks() + "'" +
                "}";
    }

    static public String printTasks(ArrayList<Task> tasks) {
        if (tasks.size() == 0) {
            return "No tasks!";
        }
        String output = "";
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            output += String.format("%s - Due: %s - Done: %s - Priority: %d\n", t.getName(), t.getNextDue(), t.getDone(), t.getPriority());
        }
        return output;
    }

    public String printTasks() {
        ArrayList<Task> tasks = this.tasks;
        if (tasks.size() == 0) {
            return "No tasks!";
        }
        String output = "";
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            output += String.format("%s - Due: %s - Done: %s - Priority: %d\n", t.getName(), t.getNextDue(), t.getDone(), t.getPriority());
        }
        return output;
    }

    //creates a deep copy (i think that's the term?) of the tasks
    public ArrayList<Task> cloneTasks() {
        ArrayList<Task> a = new ArrayList<Task>(tasks.size());
        for (Task t: tasks) {
            a.add((Task) t.clone());
        }
        return a;
    }

    static public ArrayList<Task> cloneTasks(ArrayList<Task> tasks) {
        ArrayList<Task> a = new ArrayList<Task>(tasks.size());
        for (Task t: tasks) {
            a.add((Task) t.clone());
        }
        return a;
    }
    public ArrayList<Task> removeDone(ArrayList<Task> tasks) {
        ArrayList<Task> a = new ArrayList<Task>();
        for (Task t: tasks) {
            if (!t.getDone()) {
                a.add(t);
            } 
        }
        return a;
    }

    public ArrayList<Task> keepGroup(ArrayList<Task> tasks, String groupName) {
        ArrayList<Task> a = new ArrayList<Task>();
        for (Task t: tasks) {
            if (t.getGroup() != null && t.getGroup().getName().equals(groupName)) {
                a.add(t);
            }
        }
        return a;
    }

    //used for task sorting & filtering in the Main.java
    public ArrayList<Task> taskSortFilter(String sort,Boolean ascending, Boolean[] booleans,HashMap<String, Object[]> filter, ArrayList<Task> original) {
        ArrayList<Task> newTasks = TaskManager.cloneTasks(original);
        //index 0: alpha, 1: day, 2: priority
        //inside array:
        //index 0: -1: <=, 0: ==, 1: >=
        //index 1: actual value

        //1. Sort
        newTasks = this.sort(newTasks,sort,ascending);

        //2. boolean filters: (this will turn into a normal filter if i have the time)
        if (booleans[0]) {
            newTasks = this.removeDone(newTasks);
        }

        //3. Filters
        //TODO: Add global filters
        //newTasks = this.filter(newTasks,filter);

        return newTasks;
    }
}
