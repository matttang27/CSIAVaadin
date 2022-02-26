package com.example.application.views.code;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TaskManager {
    ArrayList<Task> tasks;
    

    TaskManager() {
        tasks = new ArrayList<Task>();
        
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
            if (tasks.get(i).id == id) {
                return i;
            }
        }
        return -1;
    }

    public int getIndex(Task task) {
        for (int i=0;i<tasks.size();i++) {
            if (tasks.get(i).id == task.id) {
                return i;
            }
        }
        return -1;
    }

    public void removeTask(Task task) {
        for (int i=0;i<tasks.size();i++) {
            if (tasks.get(i).id == task.id) {
                tasks.remove(i);
            }
        }
    }

    static ArrayList<Task> filterByAlpha(ArrayList<Task> tasks, String alpha, int over) {
        //if over = 1, compare >=
        //if over = -1, compare <=
        //if over = 0, compare = (a.k.a search)
        if (alpha == "") {
            return tasks;
        }
        
        ArrayList<Task> filtered = new ArrayList<Task>();
        tasks.forEach((task) -> {
            //hehe i'm pretty genius
            //if over is true, does >=, but if false, does <=
            //never mind over is now an int haha dammit
            if (over == -1) {
                if (task.name.compareToIgnoreCase(alpha) <= 0) {
                    filtered.add(task);
                }
            }
            else if (over == 0) {
                if (task.name.compareToIgnoreCase(alpha) == 0) {
                    filtered.add(task);
                }
            }
            else if (over == 1) {
                if (task.name.compareToIgnoreCase(alpha) >= 0) {
                    filtered.add(task);
                }
            }
            
        });
        return filtered;

    }

    static ArrayList<Task> filterByDue(ArrayList<Task> tasks, int day, int over) {
        
        LocalDate now = LocalDate.now();
        ArrayList<Task> filtered = new ArrayList<Task>();
        tasks.forEach((task) -> {
            LocalDate taskDay = task.getNextDue().toLocalDate();
            int dayDiff = (int) ChronoUnit.DAYS.between(now, taskDay);
            if (over == -1) {
                if (dayDiff <= day) {
                    filtered.add(task);
                }
            }
            else if (over == 0) {
                if (dayDiff == day) {
                    filtered.add(task);
                }
            }
            else if (over == 1) {
                if (dayDiff >= day) {
                    filtered.add(task);
                }
            }

        });
        return filtered;

    }

    static ArrayList<Task> filterByCreated(ArrayList<Task> tasks, int day, int over) {
        
        LocalDate now = LocalDate.now();
        ArrayList<Task> filtered = new ArrayList<Task>();
        tasks.forEach((task) -> {
            LocalDate taskDay = task.getCreated().toLocalDate();
            int dayDiff = (int) ChronoUnit.DAYS.between(now, taskDay);
            if (over == -1) {
                if (dayDiff <= day) {
                    filtered.add(task);
                }
            }
            else if (over == 0) {
                if (dayDiff == day) {
                    filtered.add(task);
                }
            }
            else if (over == 1) {
                if (dayDiff >= day) {
                    filtered.add(task);
                }
            }

        });
        return filtered;

    }


    static ArrayList<Task> filterByPriority(ArrayList<Task> tasks, int priority, int over) {
        if (priority == 0) {
            return tasks;
        }
        ;
        ArrayList<Task> filtered = new ArrayList<Task>();
        tasks.forEach((task) -> {
            if (over == -1) {
                if (task.priority <= priority) {
                    filtered.add(task);
                }
            }
            else if (over == 0) {
                if (task.priority == priority) {
                    filtered.add(task);
                }
            }
            else if (over == 1) {
                if (task.priority >= priority) {
                    filtered.add(task);
                }
            }
        });
        return filtered;

    }

    /*
     * 
     * TODO: Implement Groups
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

    static ArrayList<Task> sortByAlpha(ArrayList<Task> tasks, Boolean ascending) {
        ArrayList<Task> sorted = TaskManager.cloneTasks(tasks);
        Collections.sort(sorted, new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                return t1.name.compareToIgnoreCase(t2.name);
            }
        });

        if (!ascending) {
            Collections.reverse(sorted);
        }
        return sorted;

    }

    static ArrayList<Task> sortByDue(ArrayList<Task> tasks, Boolean ascending) {
        ArrayList<Task> sorted = tasks;
        Collections.sort(sorted, new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                LocalDateTime d1 = t1.getNextDue();
                LocalDateTime d2 = t2.getNextDue();
                return d1.compareTo(d2);
            }
        });

        if (!ascending) {
            Collections.reverse(sorted);
        }
        return sorted;

    }

    static ArrayList<Task> sortByCreated(ArrayList<Task> tasks, Boolean ascending) {
        ArrayList<Task> sorted = tasks;
        Collections.sort(sorted, new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                LocalDateTime d1 = t1.getCreated();
                LocalDateTime d2 = t2.getCreated();
                return d1.compareTo(d2);
            }
        });

        if (!ascending) {
            Collections.reverse(sorted);
        }
        return sorted;

    }

    static ArrayList<Task> sortByPriority(ArrayList<Task> tasks, Boolean ascending) {
        ArrayList<Task> sorted = tasks;
        Collections.sort(sorted, new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                int p1 = t1.priority;
                int p2 = t2.priority;
                return p1 - p2;
            }
        });

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
            output += String.format("%s - Due: %s - Done: %s - Priority: %d\n", t.name, t.nextDue, t.done, t.priority);
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
            output += String.format("%s - Due: %s - Done: %s - Priority: %d\n", t.name, t.nextDue, t.done, t.priority);
        }
        return output;
    }
    //use when you need to choose a task with user input (numbers) - TEXT BASED ONLY
    static public Task chooseTask(Scanner input,ArrayList<Task> tasks) {
        //hopefully matthew checks for this... but just in case
        if (tasks.size() == 0) {
            return null;
        }
        String output = "";
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            output += String.format("%d) %s - Due: %s - Done: %s - Priority: %d\n", i+1, t.name, t.nextDue, t.done, t.priority);
        }
        System.out.println(output);
        System.out.println("Select a task using the numbers.");
        //oh god is this even allowed
        int a = Main.intInput(input,tasks.size());
        return tasks.get(a-1);

        
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

    //used for task sorting & filtering in the Main.java
    static public ArrayList<Task> taskSortFilter(String sort,Boolean ascending, String[][] filter, ArrayList<Task> original) {
        ArrayList<Task> newTasks = TaskManager.cloneTasks(original);
        //index 0: alpha, 1: day, 2: priority
        //inside array:
        //index 0: -1: <=, 0: ==, 1: >=
        //index 1: actual value

        //1. Sort
        if (sort.equals("alpha")) {
            newTasks = TaskManager.sortByAlpha(newTasks, ascending);
        }
        else if (sort.equals("due")) {
            newTasks = TaskManager.sortByDue(newTasks, ascending);
        }
        else if (sort.equals("priority")) {
            newTasks = TaskManager.sortByPriority(newTasks, ascending);
        }
        else if (sort.equals("created")) {
            newTasks = TaskManager.sortByCreated(newTasks, ascending);
        }

        //2. Filters
        if (!filter[0][0].equals("") && !filter[0][1].equals("")) {
            newTasks = TaskManager.filterByAlpha(newTasks, filter[0][1], Integer.parseInt(filter[0][0]));
        }
        if (!filter[1][0].equals("") && !filter[1][1].equals("")) {
            newTasks = TaskManager.filterByDue(newTasks, Integer.parseInt(filter[1][1]), Integer.parseInt(filter[1][0]));
        }
        if (!filter[2][0].equals("") && !filter[2][1].equals("")) {
            newTasks = TaskManager.filterByPriority(newTasks, Integer.parseInt(filter[2][1]), Integer.parseInt(filter[2][0]));
        }
        if (!filter[3][0].equals("") && !filter[3][1].equals("")) {
            newTasks = TaskManager.filterByCreated(newTasks, Integer.parseInt(filter[3][1]), Integer.parseInt(filter[3][0]));
        }

        return newTasks;
    }
}
