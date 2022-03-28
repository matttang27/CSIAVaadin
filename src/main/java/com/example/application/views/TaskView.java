package com.example.application.views;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.example.application.views.code.*;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.EntryClickedEvent;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.zhe.PaperRangeSlider;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;

@PWA(name = "Flow CRM Tutorial", shortName = "Flow CRM Tutorial", enableInstallPrompt = false)
@Theme(themeFolder = "flowcrmtutorial")
@PageTitle("tasks")
@Route(value = "tasks")
public class TaskView extends VerticalLayout {
    private User user;
    private Manager manager;
    private TaskManager taskManager;
    private GroupManager groupManager;
    private StatManager statManager;
    private SettingsManager settingsManager;
    private FullCalendar calendar;
    private Grid<Task> grid;
    private String sortType = "taskName";
    private String viewMode = "grid";
    private boolean ascending = true;
    private boolean showDoneB = false;
    private String[][] filter = { { "", "" }, { "", "" }, { "", "" }, { "", "" } };
    private static DateTimeFormatter defDTFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public TaskView() {
        

        
        // Sign-in not implemented yet
        // System.out.println("Create an Account:");
        // System.out.println("Enter name:");
        // String name = input.nextLine();
        // System.out.println("Creating account...");
        // might change how this works...do i even need a manager?
        // I'll change it if it ever inconveniences me >:D

        //TODO: ADD RANGE SLIDER
        // PaperRangeSlider slider = new PaperRangeSlider(-100, 100, -50, 50);
        // slider.setVisible(true);
        // add(slider);

        // PaperSlider slider2 = new PaperSlider();
        // add(slider2);



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
        statManager = manager.getStater();
        settingsManager = manager.getSettings();


        Tabs tabs = new NavTab(manager,"Tasks");
        add(new VerticalLayout(tabs));


        Dialog addDialog = new Dialog();
        
        Button addButton = new Button("Add Task",e -> addDialog.open());
        grid = new Grid<>(Task.class,false);

        addDialog.add(addTaskLayout(addDialog,grid));

        Select<String> selectSort = new Select<>();
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();


        selectSort.setItems("Task Name","Priority","Due Date","Time Created","Group Name","Estimated Time","Task Score");
        selectSort.setValue("Task Name");
        selectSort.addValueChangeListener(e -> {
            HashMap<String,String> labelToSort = new HashMap<String,String>() {{
                put("Task Name","taskName");
                put("Priority","priorty");
                put("Due Date","day");
                put("Time Created","created");
                put("Group Name","groupName");
                put("Estimated Time","estimatedTime");
                put("Task Score","taskScore");
            }};
            sortType = labelToSort.get(e.getValue());
            updateGrid();
        });

        
        radioGroup.setItems("Ascending","Descending");
        radioGroup.setValue("Ascending");
        radioGroup.addValueChangeListener(e -> {
            ascending = e.getValue().equals("Ascending");
            updateGrid();
        });
        
        Checkbox showDone = new Checkbox();
        showDone.setLabel("Show Done");
        
        showDone.addClickListener(e -> {
            showDoneB = e.getSource().getValue();
            updateGrid();
        });


        grid.setItems(taskManager.getTasks());
        grid.addComponentColumn(
            
        task -> {
            Checkbox checkBox = new Checkbox();
            
            checkBox.setValue(task.getDone());
            checkBox.addClickListener(event -> {
                //we need to get the taskManager task, not the grid task
                Task selectedTask = taskManager.getTask(task.getId());
                System.out.println(event.getSource().getValue());
                
                selectedTask.setDone(event.getSource().getValue());
                if (event.getSource().getValue()) {
                    Notification doneNotif = Notification.show("Completed Task!");
                    doneNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    doneNotif.setDuration(2000);

                }
                updateGrid();
                
            }); 
            
            return checkBox;
        }).setFrozen(true).setKey("done").setHeader("Done");
        
        grid.addColumn(Task::getName).setHeader("Name").setKey("name");
        //DONE: Change getNextDue format (preferably without an entire new function)
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
                colorDiv.getStyle().set("background-color",task.getGroup().getColor());
                colorDiv.getStyle().set("border","1px solid grey");
                colorDiv.setWidth("20px");

                Span groupName = new Span(task.getGroup().getName());
                div.add(colorDiv,groupName);
            }
            else {
                Span groupName = new Span("");
                div.add(groupName);
                
            }
            return div;
            
        }
        ).setHeader("Group").setKey("group");
        grid.addColumn(t -> {
            double score = t.calculateScore(settingsManager.getScoreA(), settingsManager.getScoreB(), settingsManager.getScoreC());
            score = Math.round(score*100.0)/100.0;
            return score;
        }).setHeader("Task Score").setKey("score");


        
        GridContextMenu<Task> menu = grid.addContextMenu();
        menu.addItem("View", event -> {
            //exits if the user selected empty space
            if (event.getItem() == null) {return;}
            Task selectedTask = event.getItem().get();
            //we need to get the taskManager task, not the grid task
            selectedTask = taskManager.getTask(selectedTask.getId());
            Dialog itemDialog = new Dialog();
            itemDialog.add(viewItemLayout(itemDialog,grid,selectedTask));
            itemDialog.open();
        });
        //TODO: figure out how to color GridContextMenu options
        menu.addItem("Delete", event -> {
            Task selectedTask = event.getItem().get();
            
            taskManager.removeTask(selectedTask);
            updateCalendar();
            updateGrid();
            
        });


        calendar = FullCalendarBuilder.create().build();


        
        calendar.setHeight("500px");
        calendar.setWidth("1000px");
        calendar.setVisible(false);

        MenuBar showGrid = new MenuBar();
        showGrid.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        showGrid.addItem("Grid");
        MenuBar showCalendar = new MenuBar();
        showCalendar.addItem("Calendar");
        //TODO: Improve the view switch function
        showGrid.getItems().get(0).addClickListener(e -> {
            //not sure if there's a better way to do this
            //maybe i should've just copied navbar lol
            
            if (!viewMode.equals("grid")) {
                showCalendar.removeThemeVariants(MenuBarVariant.LUMO_PRIMARY);
                showGrid.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
                grid.setVisible(true);
                calendar.setVisible(false);
                viewMode = "grid";
                
            }
            
            
        });
        showCalendar.getItems().get(0).addClickListener(e -> {
            //not sure if there's a better way to do this
            //maybe i should've just copied navbar lol
            if (!viewMode.equals("calendar")) {
                showGrid.removeThemeVariants(MenuBarVariant.LUMO_PRIMARY);
                showCalendar.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
                grid.setVisible(false);
                
                calendar.setVisible(true);
                calendar.setWidthFull();
                viewMode = "calendar";

            }
            
            
        });
        
        

        add(addDialog);
        add(new HorizontalLayout(new HorizontalLayout(addButton,selectSort,radioGroup,showDone),new HorizontalLayout(showGrid,showCalendar)));
        add(grid,calendar);
        
    }
    //addTaskLayout - creates the Layout for the Add Task Dialog
    private VerticalLayout addTaskLayout(Dialog dialog,Grid<Task> grid) {
        
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
        priorityField.setValue(1.0);
        NumberField timeField = new NumberField("Estimated Minutes");
        timeField.setRequiredIndicatorVisible(true);
        timeField.setErrorMessage("This field is required");
        timeField.setValue(15.0);
        Select<String> groupField = new Select<>();
        groupField.setLabel("Group");
        groupField.setItems(groupManager.getGroupNames());
        groupField.setEmptySelectionAllowed(true);

        VerticalLayout fieldLayout = new VerticalLayout(new HorizontalLayout(nameField,groupField),dueField,new HorizontalLayout(priorityField,timeField))  ;

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Add", e -> {
            //checks whether any of the fields are empty, if true, sends a notification and cancels
            String error = "";
            if (nameField.getValue() == null || dueField.getValue() == null || priorityField.getValue() == null) {
                error = "Fill in all required fields";
            }
            else if (priorityField.getValue().intValue() > 10 || priorityField.getValue().intValue() < 1) {
                error = "Priority not in range (1-10)";
            }
            else if (timeField.getValue().intValue() < 0) {
                error = "Estimated Time cannot be negative";
            }

            if (!error.equals("")) {
                Notification errorNotif = Notification.show(error);
                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotif.setDuration(2000);
                return;
            }
            
            Task newTask = new Task(nameField.getValue(),dueField.getValue(), priorityField.getValue().intValue(),timeField.getValue().intValue(),groupManager.findByName(groupField.getValue()));
            newTask.setEstimatedTime(timeField.getValue().intValue());
            newTask.setLastEdited(LocalDateTime.now());
            taskManager.addTask(newTask);
            

            updateGrid();
            updateCalendar();

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

    private VerticalLayout viewItemLayout(Dialog dialog,Grid<Task> grid,Task selectTask) {
        
        H1 title = new H1(selectTask.getName());

        TextField nameField = new TextField("Task Name");
        nameField.setValue(selectTask.getName());
        nameField.setRequiredIndicatorVisible(true);
        nameField.setErrorMessage("This field is required");
        Select<String> groupField = new Select<>();
        groupField.setItems(groupManager.getGroupNames());
        groupField.setLabel("Group");
        groupField.setValue(selectTask.getGroup() != null ? selectTask.getGroup().getName() : "");
        groupField.setEmptySelectionAllowed(true);
        DateTimePicker dueField = new DateTimePicker("Due Time");
        dueField.setValue(selectTask.getNextDue());
        dueField.setRequiredIndicatorVisible(true);
        dueField.setErrorMessage("This field is required");
        NumberField priorityField = new NumberField("Priority (1-10)");
        priorityField.setValue((double) selectTask.getPriority());
        priorityField.setRequiredIndicatorVisible(true);
        priorityField.setErrorMessage("This field is required");
        NumberField timeField = new NumberField("Estimated Minutes");
        timeField.setRequiredIndicatorVisible(true);
        timeField.setErrorMessage("This field is required");
        timeField.setValue((double) selectTask.getEstimatedTime());
        TextField createdField = new TextField("Date created");
        createdField.setReadOnly(true);
        createdField.setValue(selectTask.getCreated().format(defDTFormat));
        TextField editField = new TextField("Last edited");
        editField.setValue(selectTask.getLastEdited().format(defDTFormat));
        editField.setReadOnly(true);
        


        TextArea notesField = new TextArea("Notes");
        notesField.setValue(selectTask.getNotes());

        VerticalLayout additionalLayout = new VerticalLayout(new HorizontalLayout(createdField,editField),notesField);

        Details additionalInfo = new Details("Additional Information",additionalLayout);
        additionalInfo.setOpened(false);

        VerticalLayout fieldLayout = new VerticalLayout(new HorizontalLayout(nameField,groupField),dueField,new HorizontalLayout(priorityField,timeField),additionalInfo);

        Divider divider = new Divider();
        if (selectTask.getGroup() != null) {
            divider.getStyle().set("background-color",selectTask.getGroup().getColor());
        }
        
        HorizontalLayout finalLayout = new HorizontalLayout(title,divider,fieldLayout);
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        selectTask.setName("TEST");
        Button saveButton = new Button("Save", e -> {
            //checks whether any of the fields are empty, if true, sends a notification and cancels
            String error = "";
            if (nameField.getValue() == null || dueField.getValue() == null || priorityField.getValue() == null) {
                error = "Fill in all required fields";
            }
            else if (priorityField.getValue().intValue() > 10 || priorityField.getValue().intValue() < 1) {
                error = "Priority not in range";
            }
            else if (timeField.getValue().intValue() < 0) {
                error = "Estimated Time cannot be negative";
            }

            if (!error.equals("")) {
                Notification errorNotif = Notification.show(error);
                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotif.setDuration(2000);
                return;
            }
            
            selectTask.setName(nameField.getValue());
            selectTask.setNextDue(dueField.getValue());
            selectTask.setStart(dueField.getValue().withHour(0).withMinute(0).withSecond(1));
            selectTask.setPriority(priorityField.getValue().intValue());
            selectTask.setEstimatedTime(timeField.getValue().intValue());
            selectTask.setLastEdited(LocalDateTime.now());
            selectTask.setNotes(notesField.getValue());
            selectTask.setGroup(groupManager.findByName(groupField.getValue()));
            System.out.println(selectTask);
            
            

            updateCalendar();
            updateGrid();
            //TODO: ADD ENTRY CLICK LISTENERS TO CALENDAR
            // ComponentEventListener<EntryClickedEvent> entryClick = new ComponentEventListener();
            // entryClick.onComponentEvent(e -> {

            // });
            // calendar.addEntryClickedListener(entryClick);
            
            

            Notification addTaskNotif = Notification.show("Task Updated!");
            addTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            addTaskNotif.setDuration(2000);
            dialog.close();
            
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton,saveButton);
        
        VerticalLayout dialogLayout = new VerticalLayout(finalLayout,buttonLayout);

        
        return dialogLayout;
    }

    //not "update", kinda just delete everything and rebuild from scratch. Hey, if it works, it works!
    private void updateGrid() {
        ArrayList<Task> tasks = taskManager.getTasks();
        
        tasks = taskManager.taskSortFilter(sortType,ascending,showDoneB,filter,tasks);
        grid.setItems(tasks);
    }

    private void updateCalendar() {
        ArrayList<Task> tasks = taskManager.getTasks();
        
        calendar.removeAllEntries();
        tasks.forEach(newTask -> {
            Entry entry = new Entry();
            entry.setTitle(newTask.getName());
            entry.setAllDay(true);
            entry.setEnd(newTask.getNextDue());
            entry.setStart(newTask.getNextDue());
            calendar.addEntry(entry);
            newTask.setEntry(entry);
        });
        
    }
    
}
