package com.example.application.views;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.example.application.views.code.*;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
@PWA(name = "Flow CRM Tutorial", shortName = "Flow CRM Tutorial", enableInstallPrompt = false)
@Theme(themeFolder = "flowcrmtutorial")
@PageTitle("tasks")
@Route(value = "tasks")
public class TaskView extends VerticalLayout {
    private static User user;
    private static Manager manager;
    private static TaskManager taskManager;
    private static GroupManager groupManager;
    private static DateTimeFormatter defDTFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public TaskView() {


        // Sign-in not implemented yet
        // System.out.println("Create an Account:");
        // System.out.println("Enter name:");
        // String name = input.nextLine();
        // System.out.println("Creating account...");
        // might change how this works...do i even need a manager?
        // I'll change it if it ever inconveniences me >:D

        


        //import data
        Component c = UI.getCurrent();
        manager = (Manager) ComponentUtil.getData(c,"manager");
        if (manager == null) {
            manager = new Manager();
            manager.setUser(new User("Matthew"));
        }

        user = manager.getUser();
        taskManager = manager.getTasker();
        groupManager = manager.getGrouper();

        
        user = manager.getUser();
        taskManager = manager.getTasker();
        groupManager = manager.getGrouper();

        Dialog addDialog = new Dialog();
        add(addDialog);
        Button addButton = new Button("Add Task",e -> addDialog.open());
        Grid<Task> grid = new Grid<>(Task.class,false);

        addDialog.add(addTaskLayout(addDialog,grid));

        grid.setItems(taskManager.getTasks());
        grid.addComponentColumn(
            
        task -> {
            Checkbox checkBox = new Checkbox();
            checkBox.setValue(task.getDone());
            checkBox.addValueChangeListener(event -> task.setDone(event.getValue())); 
            return checkBox;
        }).setFrozen(true).setKey("done");
        
        grid.addColumn(Task::getName).setHeader("Name").setKey("name");
        //TODO: Change getNextDue format (preferably without an entire new function)
        grid.addColumn(Task::getNextDue).setHeader("Due").setKey("due");
        grid.addColumn(Task::getPriority).setHeader("Priority").setKey("priority");
        grid.setColumnReorderingAllowed(true);
        GridContextMenu<Task> menu = grid.addContextMenu();
        menu.addItem("View", event -> {
            Task selectedTask = event.getItem().get();
            Dialog itemDialog = new Dialog();
            itemDialog.add(viewItemLayout(itemDialog,grid,selectedTask));
            itemDialog.open();
        });
        //TODO: figure out how to color GridContextMenu options
        menu.addItem("Delete", event -> {
            Task selectedTask = event.getItem().get();
            taskManager.removeTask(selectedTask);
            grid.setItems(taskManager.getTasks());
        });


        
        
        add(addButton,grid);
    }
    //addTaskLayout - creates the Layout for the Add Task Dialog
    private static VerticalLayout addTaskLayout(Dialog dialog,Grid grid) {
        
        H2 title = new H2("Add a new task");

        TextField nameField = new TextField("Task Name");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setErrorMessage("This field is required");
        DateTimePicker dueField = new DateTimePicker("Due Time");
        dueField.setRequiredIndicatorVisible(true);
        dueField.setErrorMessage("This field is required");
        dueField.setValue(LocalDateTime.now().withHour(23).withMinute(59));
        NumberField priorityField = new NumberField("Priority (1-10)");
        priorityField.setRequiredIndicatorVisible(true);
        priorityField.setErrorMessage("This field is required");

        VerticalLayout fieldLayout = new VerticalLayout(nameField,dueField,priorityField);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Add", e -> {
            //checks whether any of the fields are empty, if true, sends a notification and cancels
            String error = "";
            if (nameField.getValue() == null || dueField.getValue() == null || priorityField.getValue() == null) {
                error = "Fill in all required fields";
            }
            else if (priorityField.getValue().intValue() > 10 || priorityField.getValue().intValue() < 1) {
                error = "Priority not in range";
            }

            if (!error.equals("")) {
                Notification errorNotif = Notification.show(error);
                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotif.setDuration(2000);
                return;
            }
            
            Task newTask = new Task(nameField.getValue(),dueField.getValue(), priorityField.getValue().intValue());
            newTask.setLastEdited(LocalDateTime.now());
            taskManager.addTask(newTask);
            grid.setItems(taskManager.getTasks());

            Notification addTaskNotif = Notification.show("Added your Task!");
            addTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            addTaskNotif.setDuration(2000);
            dialog.close();
            
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton,saveButton);
        
        VerticalLayout dialogLayout = new VerticalLayout(title,fieldLayout,buttonLayout);

        
        return dialogLayout;
    }

    private static VerticalLayout viewItemLayout(Dialog dialog,Grid grid,Task task) {

        TextField nameField = new TextField("Task Name");
        nameField.setValue(task.getName());
        nameField.setRequiredIndicatorVisible(true);
        nameField.setErrorMessage("This field is required");
        DateTimePicker dueField = new DateTimePicker("Due Time");
        dueField.setValue(task.getNextDue());
        dueField.setRequiredIndicatorVisible(true);
        dueField.setErrorMessage("This field is required");
        NumberField priorityField = new NumberField("Priority (1-10)");
        priorityField.setValue((double) task.getPriority());
        priorityField.setRequiredIndicatorVisible(true);
        priorityField.setErrorMessage("This field is required");
        TextField createdField = new TextField("Date created");
        createdField.setReadOnly(true);
        createdField.setValue(task.getCreated().format(defDTFormat));
        TextField editField = new TextField("Last edited");
        editField.setValue(task.getLastEdited().format(defDTFormat));
        editField.setReadOnly(true);


        TextArea notesField = new TextArea("Notes");
        notesField.setValue(task.getNotes());

        VerticalLayout additionalLayout = new VerticalLayout(new HorizontalLayout(createdField,editField),notesField);

        Details additionalInfo = new Details("Additional Information",additionalLayout);
        additionalInfo.setOpened(false);

        VerticalLayout fieldLayout = new VerticalLayout(nameField,dueField,priorityField,additionalInfo);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Save", e -> {
            //checks whether any of the fields are empty, if true, sends a notification and cancels
            String error = "";
            if (nameField.getValue() == null || dueField.getValue() == null || priorityField.getValue() == null) {
                error = "Fill in all required fields";
            }
            else if (priorityField.getValue().intValue() > 10 || priorityField.getValue().intValue() < 1) {
                error = "Priority not in range";
            }

            if (!error.equals("")) {
                Notification errorNotif = Notification.show(error);
                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotif.setDuration(2000);
                return;
            }
            
            task.setName(nameField.getValue());
            task.setNextDue(dueField.getValue());
            task.setPriority(priorityField.getValue().intValue());
            task.setLastEdited(LocalDateTime.now());
            task.setNotes(notesField.getValue());
            grid.setItems(taskManager.getTasks());

            Notification addTaskNotif = Notification.show("Task Updated!");
            addTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            addTaskNotif.setDuration(2000);
            dialog.close();
            
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton,saveButton);
        
        VerticalLayout dialogLayout = new VerticalLayout(fieldLayout,buttonLayout);

        
        return dialogLayout;
    }
}
