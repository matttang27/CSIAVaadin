package application.views.code;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;

import application.views.GroupView;

//class for making it easier to create Task Grids
//I'm too big brain
public class TaskGrid{
    public Grid<Task> grid;
    private FullCalendar calendar;
    private Manager manager;
    private User user;
    private TaskManager taskManager;
    private GroupManager groupManager;
    private StatManager statManager;
    private SettingsManager settingsManager;
    private String sortType = "taskName";
    private String viewMode = "grid";
    private boolean ascending = true;
    private boolean showDoneB = false;
    private HashMap<String, Icon> icons = GroupView.loadIcons();
    private HashMap<String, Object[]> filter = new HashMap<String, Object[]>() {
        {
            put("taskName", new Object[] { "", null });
            put("day", new Object[] { "", null });
            put("priority", new Object[] { "", null });
            put("created", new Object[] { "", null });
            put("estimatedTime", new Object[] { "", null });
            put("taskScore", new Object[] { "", null });
        }
    };

    private static DateTimeFormatter defDTFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle()
                .set("padding", "var(--lumo-space-xs")
                .set("box-sizing", "border-box");
        return icon;
    }

    public TaskGrid(Grid<Task> grid, Manager manager, FullCalendar calendar) {
        this.grid = grid;
        this.manager = manager;
        this.calendar = calendar;
        user = manager.getUser();
        taskManager = manager.getTasker();
        groupManager = manager.getGrouper();
        statManager = manager.getStater();
        settingsManager = manager.getSettings();
        TaskManager taskManager = manager.getTasker();
        SettingsManager settingsManager = manager.getSettings();
        grid.addComponentColumn(

                task -> {
                    Checkbox checkBox = new Checkbox();

                    checkBox.setValue(task.getDone());
                    checkBox.addClickListener(event -> {
                        // we need to get the taskManager task, not the grid task
                        Task selectedTask = taskManager.getTask(task.getId());

                        selectedTask.setDone(event.getSource().getValue());
                        if (event.getSource().getValue()) {
                            Notification doneNotif = Notification.show("Completed Task!");
                            doneNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            doneNotif.setDuration(2000);
                            DayStat todayStat = statManager.getDay(LocalDate.now());
                            todayStat.setTasksCompleted(todayStat.getTasksCompleted() + 1);

                        }
                        updateGrid();

                    });

                    return checkBox;
                }).setFrozen(true).setKey("done").setHeader("Done");
        grid.addComponentColumn(task -> {
            if (task.getDone()) {
                Span span = new Span(createIcon(VaadinIcon.CHECK), new Span("Done"));
                span.getElement().getThemeList().add("badge success");
                return span;
            }
            else if (taskManager.highestPriority().getId() == task.getId()) {
                Span span = new Span(createIcon(VaadinIcon.BELL), new Span("Priority"));
                span.getElement().getThemeList().add("badge");
                span.getStyle().set("color","#ff9600");
                span.getStyle().set("background-color","#ffe6a1");
                return span;
            }
            else if (task.getNextDue().isBefore(LocalDateTime.now())) {
                Span span = new Span(createIcon(VaadinIcon.EXCLAMATION_CIRCLE_O), new Span("Overdue"));
                span.getElement().getThemeList().add("badge error");
                return span;
            }
            else {
                Span span = new Span(createIcon(VaadinIcon.COG), new Span("To-do"));
                span.getElement().getThemeList().add("badge contrast");
                return span;
            }
        }).setHeader("Status").setKey("status");
        grid.addColumn(Task::getName).setHeader("Name").setKey("name");
        // DONE: Change getNextDue format (preferably without an entire new function)
        grid.addColumn(t -> {
            return t.getNextDue().format(defDTFormat);
        }).setHeader("Due").setKey("due");
        grid.addColumn(Task::getPriority).setHeader("Priority").setKey("priority");
        grid.addColumn(t -> {

            return t.getEstimatedTime();
        }).setHeader("Estimated Time").setKey("estimatedTime");
        grid.addComponentColumn(

                task -> {
                    HorizontalLayout div = new HorizontalLayout();
                    if (task.getGroup() != null) {

                        if (task.getGroup().getIcon() != "") {
                            div.add(icons.get(task.getGroup().getIcon()));
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
        grid.setItems(taskManager.getTasks());

        updateCalendar();
        updateGrid();
    }

    public void updateGrid() {
        TaskManager taskManager = manager.getTasker();
        ArrayList<Task> tasks = taskManager.getTasks();

        tasks = taskManager.taskSortFilter(sortType, ascending, new Boolean[] { showDoneB }, filter, tasks);
        grid.setItems(tasks);
    }

    public void addContextMenu() {
        GridContextMenu<Task> menu = grid.addContextMenu();
        menu.addItem("View", event -> {
            // exits if the user selected empty space
            if (event.getItem().isEmpty()) {return;};
            Task selectedTask = event.getItem().get();
            // we need to get the taskManager task, not the grid task
            selectedTask = manager.getTasker().getTask(selectedTask.getId());
            Dialog itemDialog = new Dialog();
            itemDialog.add(viewItemLayout(itemDialog, grid, selectedTask));
            itemDialog.open();
        });

        menu.addItem("Delete", event -> {
            Task selectedTask = event.getItem().get();

            manager.getTasker().removeTask(selectedTask);
            updateCalendar();

            updateGrid();

        });
    }

    public void updateCalendar() {
        if (calendar == null) {
            return;
        };
        ArrayList<Task> tasks = manager.getTasker().getTasks();

        calendar.removeAllEntries();
        tasks.forEach(newTask -> {
            Entry entry = new Entry();
            entry.setTitle(newTask.getName());
            entry.setAllDay(true);
            entry.setEnd(newTask.getNextDue());
            entry.setStart(newTask.getNextDue());
            calendar.addEntry(entry);
        });

    }

    public Button addTaskButton() {
        Dialog addDialog = new Dialog();

        Button addButton = new Button("Add Task", e -> addDialog.open());
        addDialog.add(addTaskLayout(addDialog, grid));
        return addButton;
    }

    public HorizontalLayout addSorts() {
        Select<String> selectSort = new Select<>();
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();

        selectSort.setItems("Task Name", "Priority", "Due Date", "Time Created", "Group Name", "Estimated Time",
                "Task Score");
        selectSort.setValue("Task Name");
        selectSort.addValueChangeListener(e -> {
            HashMap<String, String> labelToSort = new HashMap<String, String>() {
                {
                    put("Task Name", "taskName");
                    put("Priority", "priorty");
                    put("Due Date", "day");
                    put("Time Created", "created");
                    put("Group Name", "groupName");
                    put("Estimated Time", "estimatedTime");
                    put("Task Score", "taskScore");
                }
            };
            setSortType(labelToSort.get(e.getValue()));
            updateGrid();
        });

        radioGroup.setItems("Ascending", "Descending");
        radioGroup.setValue("Ascending");
        radioGroup.addValueChangeListener(e -> {
            setAscending(e.getValue().equals("Ascending"));
            updateGrid();
        });

        Checkbox showDone = new Checkbox();
        showDone.setLabel("Show Done");
        showDone.setValue(showDoneB);
        showDone.addClickListener(e -> {
            setShowDoneB(e.getSource().getValue());
            updateGrid();
        });

        return new HorizontalLayout(selectSort, radioGroup, showDone);
    }

    public VerticalLayout viewItemLayout(Dialog dialog, Grid<Task> grid, Task selectTask) {

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

        VerticalLayout additionalLayout = new VerticalLayout(new HorizontalLayout(createdField, editField), notesField);

        Details additionalInfo = new Details("Additional Information", additionalLayout);
        additionalInfo.setOpened(false);

        VerticalLayout fieldLayout = new VerticalLayout(new HorizontalLayout(nameField, groupField), dueField,
                new HorizontalLayout(priorityField, timeField), additionalInfo);

        Divider divider = new Divider();
        if (selectTask.getGroup() != null) {
            divider.getStyle().set("background-color", selectTask.getGroup().getColor());
        }

        HorizontalLayout finalLayout = new HorizontalLayout(title, divider, fieldLayout);
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        Button saveButton = new Button("Save", e -> {
            // checks whether any of the fields are empty, if true, sends a notification and
            // cancels
            String error = "";
            if (nameField.getValue().equals("") || dueField.getValue() == null || priorityField.getValue() == null) {
                error = "Fill in all required fields";
            } else if (priorityField.getValue().intValue() > 10 || priorityField.getValue().intValue() < 1) {
                error = "Priority not in range";
            } else if (timeField.getValue().intValue() < 0) {
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

            updateCalendar();
            updateGrid();
            // TODO: ADD ENTRY CLICK LISTENERS TO CALENDAR
            // ComponentEventListener<EntryClickedEvent> entryClick = new
            // ComponentEventListener();
            // entryClick.onComponentEvent(e -> {

            // });
            // calendar.addEntryClickedListener(entryClick);

            Notification addTaskNotif = Notification.show("Task Updated!");
            addTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            addTaskNotif.setDuration(2000);
            dialog.close();

        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);

        VerticalLayout dialogLayout = new VerticalLayout(finalLayout, buttonLayout);

        return dialogLayout;
    }

    public VerticalLayout addTaskLayout(Dialog dialog, Grid<Task> grid) {

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

        VerticalLayout fieldLayout = new VerticalLayout(new HorizontalLayout(nameField, groupField), dueField,
                new HorizontalLayout(priorityField, timeField));

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Add", e -> {
            // checks whether any of the fields are empty, if true, sends a notification and
            // cancels
            String error = "";
            if (nameField.getValue().equals("") || dueField.getValue() == null || priorityField.getValue() == null) {
                error = "Fill in all required fields";
            } else if (priorityField.getValue().intValue() > 10 || priorityField.getValue().intValue() < 1) {
                error = "Priority not in range (1-10)";
            } else if (timeField.getValue().intValue() < 0) {
                error = "Estimated Time cannot be negative";
            }

            if (!error.equals("")) {
                Notification errorNotif = Notification.show(error);
                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotif.setDuration(2000);
                return;
            }

            Task newTask = new Task(nameField.getValue(), dueField.getValue(), priorityField.getValue().intValue(),
                    timeField.getValue().intValue(), groupManager.findByName(groupField.getValue()));
            newTask.setEstimatedTime(timeField.getValue().intValue());
            newTask.setLastEdited(LocalDateTime.now());
            taskManager.addTask(newTask);

            updateGrid();
            updateCalendar();

            DayStat todayStat = statManager.getDay(LocalDate.now());
            todayStat.setTasksCreated(todayStat.getTasksCreated() + 1);

            Notification addTaskNotif = Notification.show("Added your Task!");
            addTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            addTaskNotif.setDuration(2000);
            dialog.close();

        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, saveButton);

        VerticalLayout dialogLayout = new VerticalLayout(title, fieldLayout, buttonLayout);

        return dialogLayout;
    }

    public Grid<Task> getGrid() {
        return this.grid;
    }

    public void setGrid(Grid<Task> grid) {
        this.grid = grid;
    }

    public FullCalendar getCalendar() {
        return this.calendar;
    }

    public void setCalendar(FullCalendar calendar) {
        this.calendar = calendar;
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TaskManager getTaskManager() {
        return this.taskManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public GroupManager getGroupManager() {
        return this.groupManager;
    }

    public void setGroupManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    public StatManager getStatManager() {
        return this.statManager;
    }

    public void setStatManager(StatManager statManager) {
        this.statManager = statManager;
    }

    public SettingsManager getSettingsManager() {
        return this.settingsManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public String getSortType() {
        return this.sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getViewMode() {
        return this.viewMode;
    }

    public void setViewMode(String viewMode) {
        this.viewMode = viewMode;
    }

    public boolean isAscending() {
        return this.ascending;
    }

    public boolean getAscending() {
        return this.ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public boolean isShowDoneB() {
        return this.showDoneB;
    }

    public boolean getShowDoneB() {
        return this.showDoneB;
    }

    public void setShowDoneB(boolean showDoneB) {
        this.showDoneB = showDoneB;
    }

    public HashMap<String, Object[]> getFilter() {
        return this.filter;
    }

    public void setFilter(HashMap<String, Object[]> filter) {
        this.filter = filter;
    }

}
