package com.example.application.views;
import com.example.application.views.code.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@PWA(name = "Flow CRM Tutorial", shortName = "Flow CRM Tutorial", enableInstallPrompt = false)
@Theme(themeFolder = "flowcrmtutorial")
@PageTitle("list")
@Route(value = "")
public class ListView extends VerticalLayout {

    public ListView() {


        // Sign-in not implemented yet
        // System.out.println("Create an Account:");
        // System.out.println("Enter name:");
        // String name = input.nextLine();
        // System.out.println("Creating account...");
        String name = "Matthew";
        User user = new User(name);
        // might change how this works...do i even need a manager?
        // I'll change it if it ever inconveniences me >:D
        Manager manager = new Manager();
        TaskManager taskManager = manager.getTasker();
        GroupManager groupManager = manager.getGrouper();
        manager.setUser(user);

        Button addButton = new Button("Add Task");
        

        Grid<Task> grid = new Grid<>(Task.class,false);
        grid.addColumn(Task::getName).setHeader("Name");
        grid.addColumn(Task::getNextDue).setHeader("Due");
        grid.addColumn(Task::getPriority).setHeader("Priority");

        grid.setItems(taskManager.getTasks());
        addButton.addClickListener(click -> {
            taskManager.blankTask();
            grid.setItems(taskManager.getTasks());
        });

        add(addButton,grid);
    }

}
