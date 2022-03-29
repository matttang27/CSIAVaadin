package com.example.application.views;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.example.application.views.code.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import org.vaadin.stefan.fullcalendar.CalendarView;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.SchedulerView;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.timepicker.TimePicker;
@Theme(themeFolder = "flowcrmtutorial")
@PageTitle("Schedule")
@Route(value = "schedule")
public class ScheduleView extends VerticalLayout {
    private Manager manager;
    private TaskManager taskManager;
    private GroupManager groupManager;
    private StatManager statManager;
    private ScheduleManager scheduleManager;
    private User user;
    private FullCalendar calendar;
    private Event doing;
    private LocalDate date = LocalDate.now();
    private Grid<Event> scheduleGrid;
    private DaySchedule daySchedule;
    private Task selectTask;
    public ScheduleView() {

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
        scheduleManager = manager.getScheduler();

        NavTab tabs = new NavTab(manager,"Schedule");
        add(new VerticalLayout(tabs));

        //if daySchedule doesn't exist, create one
        if (!scheduleManager.dayExists(date)) {
            scheduleManager.addDay(date);
            daySchedule = scheduleManager.getDay(date);
        }
        
        Dialog addEventDialog = new Dialog();
        Button addEventButton = new Button("Add Event",e -> addEventDialog.open());
        addEventDialog.add(addEventLayout(addEventDialog));

        DatePicker datePicker = new DatePicker("Day");
        datePicker.setValue(LocalDate.now());
        datePicker.addValueChangeListener(e -> {
            date = e.getValue();
            daySchedule = scheduleManager.getDay(date);
            if (daySchedule == null) {
                scheduleManager.addDay(date);
            }
            updateGrid();
        });
        add(new HorizontalLayout(addEventButton,datePicker));
        
        scheduleGrid = new Grid<>(Event.class,false);
        scheduleGrid.addComponentColumn(event -> {
            RadioButtonGroup<String> radioButton = new RadioButtonGroup<>();
            radioButton.add("");
            radioButton.setValue("");
            return radioButton;
        }).setKey("doing").setHeader("Doing");
        scheduleGrid.addColumn(Event::getName).setHeader("Name").setKey("name");
        scheduleGrid.setItems();
        updateGrid();
        add(scheduleGrid);
        

    }

    private void updateGrid() {
        if (!scheduleManager.dayExists(date)) {
            scheduleManager.addDay(date);
        }
        scheduleGrid.setItems(scheduleManager.getDay(date).getEvents());
    }

    private VerticalLayout selectTaskLayout(Dialog dialog) {
        dialog.setWidth("50%");
        H2 title = new H2("Select task:");
        Grid<Task> grid = new Grid<>(Task.class,false);
        
        TaskGrid taskGrid = new TaskGrid(grid,manager,null);
        taskGrid.setSortType("taskScore");
        taskGrid.setAscending(false);
        taskGrid.updateGrid();
        GridContextMenu<Task> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Select", e -> {
            if (e.getItem().isPresent()) {
                selectTask = e.getItem().get();
                dialog.close();
            }
        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        return new VerticalLayout(title,grid,cancelButton);

    }
    private VerticalLayout addEventLayout(Dialog dialog) {

        selectTask = null;

        dialog.setWidth("50%");
        H2 title = new H2("Add Event:");
        TimePicker startField = new TimePicker("Start Time");
        TimePicker endField = new TimePicker("End Time");
        TextField nameField = new TextField("Name");
        
        Dialog selectTaskDialog = new Dialog();
        Button selectTaskButton = new Button("Select Task",e -> selectTaskDialog.open());
        selectTaskDialog.add(selectTaskLayout(selectTaskDialog));

        HorizontalLayout nameLayout = new HorizontalLayout(selectTaskButton,new H4("Or"),nameField);
        nameLayout.setAlignItems(Alignment.CENTER);
        
        
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Save", e -> {
            // checks whether any of the fields are empty, if true, sends a notification and
            // cancels
            String error = "";

            if (startField.getValue() == null || endField.getValue() == null || (nameField.getValue() == null && selectTask == null)) {
                error = "Please fill all fields.";
            }
            else if (startField.getValue().compareTo(endField.getValue()) > 0 || startField.getValue() == endField.getValue()) {
                error = "Start time cannot be same or later than end time";
            }

            Event newEvent = new Event();
            newEvent.setName(selectTask != null ? selectTask.getName() : nameField.getValue());
            newEvent.setStartTime(startField.getValue());
            newEvent.setEndTime(endField.getValue());
            newEvent.setTask(selectTask);
            newEvent.setDoing(false);

            daySchedule.addEvent(newEvent);
            


            if (!error.equals("")) {
                Notification errorNotif = Notification.show(error);
                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotif.setDuration(2000);
                return;
            }

            updateGrid();
            // TODO: ADD ENTRY CLICK LISTENERS TO CALENDAR
            // ComponentEventListener<EntryClickedEvent> entryClick = new
            // ComponentEventListener();
            // entryClick.onComponentEvent(e -> {

            // });
            // calendar.addEntryClickedListener(entryClick);

            Notification selectTaskNotif = Notification.show("Added Event!");
            selectTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            selectTaskNotif.setDuration(2000);
            dialog.close();

        });

        VerticalLayout finalLayout = new VerticalLayout(title,nameLayout,new HorizontalLayout(startField,endField),new HorizontalLayout(cancelButton,saveButton));

        return finalLayout;
    }
}