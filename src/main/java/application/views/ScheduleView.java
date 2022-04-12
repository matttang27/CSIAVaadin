package application.views;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import application.views.code.*;

import com.vaadin.flow.component.ClientCallable;
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
import com.vaadin.flow.router.PreserveOnRefresh;
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
    private LocalDate back = LocalDate.now();
    @ClientCallable
    public void windowClosed() {
        try {
			if (manager != null) {manager.save();};
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public ScheduleView() {


        //import data
        Component c = UI.getCurrent();
        manager = (Manager) ComponentUtil.getData(c,"manager");
        if (manager == null) {

            UI ui = UI.getCurrent();
            ui.navigate("");
            ui.getPage().reload();
        } else {
            setup();
        }
    }
    public void setup() {
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
            
        }
        daySchedule = scheduleManager.getDay(date);
        
        Dialog addEventDialog = new Dialog();
        Button addEventButton = new Button("Add Event",e -> addEventDialog.open());
        addEventDialog.add(addEventLayout(addEventDialog,null));

        DatePicker datePicker = new DatePicker("Day");
        datePicker.setValue(LocalDate.now());
        datePicker.addValueChangeListener(e -> {
            date = e.getValue();
            if (!scheduleManager.dayExists(date)) {
                scheduleManager.addDay(date);
            }
            daySchedule = scheduleManager.getDay(date);
            updateGrid();
        });

        Button resetButton = new Button("Reset",e -> {
            scheduleManager.resetDay(date);
            updateGrid();
        });

        Button defaultButton = new Button("Default",e -> {
            if (e.getSource().getText().equals("Default")) {
                back = date;
                date = LocalDate.of(2000,1,1);
                datePicker.setValue(date);
                daySchedule = scheduleManager.getDay(date);
                e.getSource().setText("Go back");
                updateGrid();
            }
            else {
                date = back;
                datePicker.setValue(date);
                daySchedule = scheduleManager.getDay(date);
                e.getSource().setText("Default");
                updateGrid();
            }
            
        });

        Button generateSchedule = new Button("Generate Pomodoros",e -> {
            Dialog generateDialog = new Dialog();
            generateDialog.add(generateLayout(generateDialog));
            generateDialog.open();
        });
        
        add(new HorizontalLayout(addEventButton,datePicker,resetButton,defaultButton,generateSchedule));
        
        scheduleGrid = new Grid<>(Event.class,false);
        
        GridContextMenu<Event> contextMenu = scheduleGrid.addContextMenu();
        
        contextMenu.addItem("View",e -> {
            if (!e.getItem().isPresent()) {return;}
            Event selectEvent = this.daySchedule.getFromId(e.getItem().get().getId());
            Dialog itemDialog = new Dialog();
            itemDialog.add(addEventLayout(itemDialog, selectEvent));
            itemDialog.open();
        });
        contextMenu.addItem("Delete",e -> {
            if (!e.getItem().isPresent()) {return;}
            Event selectEvent = this.daySchedule.getFromId(e.getItem().get().getId());
            if (selectEvent != null) {
                daySchedule.deleteEvent(selectEvent);
            }
            
            updateGrid();
            updateGrid();

        });
        scheduleGrid.addComponentColumn(event -> {
            Checkbox checkBox = new Checkbox();
            if (event.getDoing()) {
                checkBox.setValue(true);
            }
            if (!date.isEqual(LocalDate.now())) {
                checkBox.setReadOnly(true);
            }
            
            checkBox.addValueChangeListener(e -> {
                for (Event ev: daySchedule.getEvents()) {
                    if (ev.getDoing()) {
                        ev.setTimeSpent(ev.getTimeSpent().plus(Duration.between(ev.getDoingStart(), LocalDateTime.now())));
                    }
                    ev.setDoing(false);
                }
                
                event.setDoing(e.getValue());
                event.setDoingStart(LocalDateTime.now());
                updateGrid();
            });
            
            return checkBox;
        }).setKey("doing").setHeader("Doing");
        scheduleGrid.addColumn(event -> {
            return String.format("%s-%s",event.getStartTime(),event.getEndTime());
        }).setHeader("Time").setKey("time");
        scheduleGrid.addColumn(Event::getName).setHeader("Name").setKey("name");
        scheduleGrid.addColumn(e -> {
            return e.getTimeSpentString();
        }).setHeader("Time Spent").setKey("timeSpent");
        scheduleGrid.addColumn(Event::getEstimatedTime).setHeader("Estimated Time").setKey("estTime");
        updateGrid();
        add(scheduleGrid);
        

    }

    private void updateGrid() {
        if (!scheduleManager.dayExists(date)) {
            scheduleManager.addDay(date);
        }
        scheduleGrid.setItems(scheduleManager.getDay(date).getEvents());
    }

    private VerticalLayout selectTaskLayout(Dialog dialog, TextField nameField, NumberField estTimeField) {
        dialog.setWidth("50%");
        H2 title = new H2("Select task:");
        Grid<Task> grid = new Grid<>(Task.class,false);
        
        TaskGrid taskGrid = new TaskGrid(grid,manager,null);
        taskGrid.setSortType("taskScore");
        taskGrid.setAscending(false);
        taskGrid.updateGrid();
        grid.getColumnByKey("done").setVisible(false);
        
        GridContextMenu<Task> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Select", e -> {
            if (e.getItem().isPresent()) {
                selectTask = e.getItem().get();
                nameField.setValue(selectTask.getName());
                estTimeField.setValue((double) selectTask.getEstimatedTime());
                dialog.close();
            }
        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        return new VerticalLayout(title,grid,cancelButton);

    }
    private VerticalLayout generateLayout(Dialog dialog) {
        H2 title = new H2("Generate your pomodoro schedule!");
        NumberField pomodoroField = new NumberField("Maximum Pomodoro's");
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Save", e -> {
            // checks whether any of the fields are empty, if true, sends a notification and
            // cancels
            String error = "";

            if (pomodoroField.getValue() < 1) {
                error = "Must do at least 1 pomodoro";
            }

            if (!error.equals("")) {
                Notification errorNotif = Notification.show(error);
                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotif.setDuration(2000);
                return;
            }

            daySchedule.generatePomodoro((int) Math.round(pomodoroField.getValue()),taskManager);
            Notification selectTaskNotif = Notification.show("Generated Pomodoros!");
            selectTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            selectTaskNotif.setDuration(2000);
            updateGrid();
            dialog.close();
            

        });
        return new VerticalLayout(title,pomodoroField,new HorizontalLayout(cancelButton,saveButton));
    }
    private VerticalLayout addEventLayout(Dialog dialog, Event event) {

        selectTask = null;

        dialog.setWidth("50%");
        H2 title = event == null ? new H2("Add Event:") : new H2(event.getName());
        TimePicker startField = new TimePicker("Start Time");
        TimePicker endField = new TimePicker("End Time");
        TextField nameField = new TextField("Name");
        NumberField estTimeField = new NumberField("Estimated Time");
        if (event != null) {
            startField.setValue(event.getStartTime());
            endField.setValue(event.getEndTime());
            nameField.setValue(event.getName());
            estTimeField.setValue((double) event.getEstimatedTime());
        }
        Dialog selectTaskDialog = new Dialog();
        Button selectTaskButton = new Button("Select Task",e -> selectTaskDialog.open());
        selectTaskDialog.add(selectTaskLayout(selectTaskDialog,nameField,estTimeField));
        selectTaskDialog.addDialogCloseActionListener(e -> {
            if (selectTask != null) {
                nameField.setValue(selectTask.getName());
                estTimeField.setValue((double) selectTask.getEstimatedTime());
            }
        });


        HorizontalLayout nameLayout = new HorizontalLayout(selectTaskButton,new H4("Or"),nameField,estTimeField);
        nameLayout.setAlignItems(Alignment.CENTER);
        
        
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Save", e -> {
            // checks whether any of the fields are empty, if true, sends a notification and
            // cancels
            String error = "";

            if (startField.getValue() == null || endField.getValue() == null || (nameField.getValue().equals("") && selectTask == null)) {
                error = "Please fill all fields.";
            }
            else if (startField.getValue().compareTo(endField.getValue()) > 0 || startField.getValue() == endField.getValue()) {
                error = "Start time cannot be same or later than end time";
            }
            else if (daySchedule.timeFree(startField.getValue(),endField.getValue()) != null) {
                error = "Times overlap with existing events!";
            }
            


            if (!error.equals("")) {
                Notification errorNotif = Notification.show(error);
                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotif.setDuration(2000);
                return;
            }

            if (estTimeField.getValue() == null) {
                estTimeField.setValue(0.0);
            }

            if (event == null) {
                Event newEvent = new Event();
                newEvent.setName(selectTask != null ? selectTask.getName() : nameField.getValue());
                newEvent.setStartTime(startField.getValue());
                newEvent.setEndTime(endField.getValue());
                newEvent.setEstimatedTime((int) Math.round(estTimeField.getValue()));
                newEvent.setTask(selectTask);
                newEvent.setDoing(false);

                daySchedule.addEvent(newEvent);
                Notification selectTaskNotif = Notification.show("Added Event!");
                selectTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                selectTaskNotif.setDuration(2000);
            }
            else {
                event.setName(selectTask != null ? selectTask.getName() : nameField.getValue());
                event.setStartTime(startField.getValue());
                event.setEndTime(endField.getValue());
                event.setEstimatedTime((int) Math.round(estTimeField.getValue()));
                event.setTask(selectTask);
                event.setDoing(false);

                Notification selectTaskNotif = Notification.show("Added Event!");
                selectTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                selectTaskNotif.setDuration(2000);
            }

            

            updateGrid();
            // TODO: ADD ENTRY CLICK LISTENERS TO CALENDAR
            // ComponentEventListener<EntryClickedEvent> entryClick = new
            // ComponentEventListener();
            // entryClick.onComponentEvent(e -> {

            // });
            // calendar.addEntryClickedListener(entryClick);

            
            dialog.close();

        });

        VerticalLayout finalLayout = new VerticalLayout(title,nameLayout,new HorizontalLayout(startField,endField),new HorizontalLayout(cancelButton,saveButton));

        return finalLayout;
    }
    
}