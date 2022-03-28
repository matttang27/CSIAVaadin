package com.example.application.views.code;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

//class for making it easier to create Task Grids
public class TaskGrid {
    public Grid<Task> grid;
    private String sortType = "taskName";
    private String viewMode = "grid";
    private boolean ascending = true;
    private boolean showDoneB = false;
    private String[][] filter = { { "", "" }, { "", "" }, { "", "" }, { "", "" } };

    private static DateTimeFormatter defDTFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public Grid<Task> setup(Grid<Task> grid, Manager manager) {
        TaskManager taskManager = manager.getTasker();
        SettingsManager settingsManager = manager.getSettings();
        grid.addComponentColumn(

                task -> {
                    Checkbox checkBox = new Checkbox();

                    checkBox.setValue(task.getDone());
                    checkBox.addClickListener(event -> {
                        // we need to get the taskManager task, not the grid task
                        Task selectedTask = taskManager.getTask(task.getId());
                        System.out.println(event.getSource().getValue());

                        selectedTask.setDone(event.getSource().getValue());
                        if (event.getSource().getValue()) {
                            Notification doneNotif = Notification.show("Completed Task!");
                            doneNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            doneNotif.setDuration(2000);

                        }
                        updateGrid(manager);

                    });

                    return checkBox;
                }).setFrozen(true).setKey("done").setHeader("Done");

        grid.addColumn(Task::getName).setHeader("Name").setKey("name");
        // DONE: Change getNextDue format (preferably without an entire new function)
        grid.addColumn(t -> {
            return t.getNextDue().format(defDTFormat);
        }).setHeader("Due").setKey("due");
        grid.addColumn(Task::getPriority).setHeader("Priority").setKey("priority");
        grid.addColumn(t -> {
            System.out.println(t.getEstimatedTime());
            return t.getEstimatedTime();
        }).setHeader("Estimated Time").setKey("estimatedTime");
        grid.addComponentColumn(

                task -> {
                    HorizontalLayout div = new HorizontalLayout();
                    if (task.getGroup() != null) {

                        if (task.getGroup().getIcon() != null) {
                            div.add(task.getGroup().getIcon());
                        }
                        Div colorDiv = new Div();
                        colorDiv.getStyle().set("background-color", task.getGroup().getColor());
                        colorDiv.getStyle().set("border", "1px solid grey");
                        colorDiv.setWidth("20px");

                        Span groupName = new Span(task.getGroup().getName());
                        div.add(colorDiv, groupName);
                    } else {
                        Span groupName = new Span("");
                        div.add(groupName);

                    }
                    return div;

                }).setHeader("Group").setKey("group");
        grid.addColumn(t -> {
            double score = t.calculateScore(settingsManager.getScoreA(), settingsManager.getScoreB(),
                    settingsManager.getScoreC());
            score = Math.round(score * 100.0) / 100.0;
            return score;
        }).setHeader("Task Score").setKey("score");
        return grid;
    }
    private void updateGrid(Manager manager) {
        TaskManager taskManager = manager.getTasker();
        ArrayList<Task> tasks = taskManager.getTasks();
        
        tasks = taskManager.taskSortFilter(sortType,ascending,showDoneB,filter,tasks);
        grid.setItems(tasks);
    }
}
