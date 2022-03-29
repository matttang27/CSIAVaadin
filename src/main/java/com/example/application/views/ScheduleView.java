package com.example.application.views;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.example.application.views.code.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
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

        Dialog addTaskDialog = new Dialog();
        
        Button addTaskButton = new Button("Add Task",e -> addTaskDialog.open());
        addTaskDialog.add(addTaskLayout(addTaskDialog));
        Dialog addEventDialog = new Dialog();
        Button addEventButton = new Button("Add Event",e -> addEventDialog.open());

        DatePicker daySchedule = new DatePicker("Day");
        daySchedule.setValue(LocalDate.now());
        daySchedule.addValueChangeListener(e -> {
            date = e.getValue();
            updateGrid();
        });
        add(new HorizontalLayout(addTaskButton,addEventButton,daySchedule));
        
        scheduleGrid = new Grid<>(Event.class,false);
        scheduleGrid.addComponentColumn(event -> {
            RadioButtonGroup<String> radioButton = new RadioButtonGroup<>();
            radioButton.add("");
            radioButton.setValue("");
            return radioButton;
        }).setKey("doing").setHeader("Doing");
        updateGrid();
        add(scheduleGrid);
        

    }

    private void updateGrid() {
        scheduleGrid.setItems(scheduleManager.getDay(date).getEvents());
    }

    private VerticalLayout addTaskLayout(Dialog dialog) {
        dialog.setWidth("50%");
        H2 title = new H2("Select task:");
        Grid<Task> grid = new Grid<>(Task.class,false);
        
        TaskGrid taskGrid = new TaskGrid(grid,manager,null);
        taskGrid.setSortType("taskScore");
        taskGrid.setAscending(false);
        taskGrid.updateGrid();
        GridContextMenu<Task> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Select", e -> {
            System.out.println(e);
        });
        

        

        return new VerticalLayout(title,grid);

    }
}