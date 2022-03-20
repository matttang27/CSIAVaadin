package com.example.application.views;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private FullCalendar calendar;
    private Grid<Task> grid;
    private String sortType = "taskName";
    private String viewMode = "grid";
    private boolean ascending = true;
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

        Tabs tabs = new Tabs(new Tab("Tasks"),new Tab("Groups"), new Tab("Schedule"),new Tab("Statistics"),new Tab("Tools"));
        tabs.addSelectedChangeListener(listener -> {
            Tab tab = listener.getSelectedTab();
            String tabName = tab.getLabel();
            if (tabName.equals("Groups")) {
                tabs.getUI().ifPresent(ui -> {
                    Component c = ui.getCurrent();
                    ComponentUtil.setData(c,"manager",manager);
                    ui.navigate("groups");
                });
            }
        });

        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.setWidth("100%");
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        add(new VerticalLayout(tabs));
        

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
        
        Button addButton = new Button("Add Task",e -> addDialog.open());
        grid = new Grid<>(Task.class,false);

        addDialog.add(addTaskLayout(addDialog,grid));

        Select<String> selectSort = new Select<>();
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();


        selectSort.setItems("Task Name","Priority","Due Date","Time Created","Group Name");
        selectSort.setValue("Task Name");
        selectSort.addValueChangeListener(e -> {
            switch (e.getValue()) {
                case "Task Name":
                    sortType = "taskName";
                    break;
                case "Priority":
                    sortType = "priority";
                    break;
                case "Due Date":
                    sortType = "day";
                    break;
                case "Time Created":
                    sortType = "created";
                    break;
                case "Group Name":
                    sortType = "groupName";
                    break;
            }
            updateGrid();
        });

        
        radioGroup.setItems("Ascending","Descending");
        radioGroup.setValue("Ascending");
        radioGroup.addValueChangeListener(e -> {
            ascending = e.getValue().equals("Ascending");
            updateGrid();
        });
        


        grid.setItems(taskManager.getTasks());
        grid.addComponentColumn(
            
        task -> {
            Checkbox checkBox = new Checkbox();
            checkBox.setValue(task.getDone());
            checkBox.addValueChangeListener(event -> task.setDone(event.getValue())); 
            return checkBox;
        }).setFrozen(true).setKey("done").setHeader("Done");
        
        grid.addColumn(Task::getName).setHeader("Name").setKey("name");
        //DONE: Change getNextDue format (preferably without an entire new function)
        grid.addColumn(Task::getNextDueString).setHeader("Due").setKey("due");
        grid.addColumn(Task::getPriority).setHeader("Priority").setKey("priority");
        grid.addComponentColumn(

        task -> {
            if (task.getGroup() != null) {
                Span groupName = new Span(task.getGroup().getName());
                groupName.getElement().getThemeList().add("badge");
                return groupName;
            }
            else {
                Span groupName = new Span("");
                return groupName;
                
            }
            
        }
        ).setHeader("Group").setKey("group");
        grid.setColumnReorderingAllowed(true);


        
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
        add(new HorizontalLayout(new HorizontalLayout(addButton,selectSort,radioGroup),new HorizontalLayout(showGrid,showCalendar)));
        add(grid,calendar);
        
    }
    //addTaskLayout - creates the Layout for the Add Task Dialog
    private VerticalLayout addTaskLayout(Dialog dialog,Grid grid) {
        
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
        Select<String> groupField = new Select<>();
        groupField.setLabel("Group");
        groupField.setItems(groupManager.getGroupNames());
        groupField.setEmptySelectionAllowed(true);

        VerticalLayout fieldLayout = new VerticalLayout(new HorizontalLayout(nameField,groupField),dueField,priorityField);

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
            
            Task newTask = new Task(nameField.getValue(),dueField.getValue(), priorityField.getValue().intValue(),groupManager.findByName(groupField.getValue()));
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

    private VerticalLayout viewItemLayout(Dialog dialog,Grid grid,Task selectTask) {
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

        VerticalLayout fieldLayout = new VerticalLayout(new HorizontalLayout(nameField,groupField),dueField,priorityField,additionalInfo);

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
        
        VerticalLayout dialogLayout = new VerticalLayout(fieldLayout,buttonLayout);

        
        return dialogLayout;
    }

    //not "update", kinda just delete everything and rebuild from scratch. Hey, if it works, it works!
    private void updateGrid() {
        ArrayList<Task> tasks = taskManager.getTasks();
        
        tasks = TaskManager.taskSortFilter(sortType,ascending,filter,tasks);
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
