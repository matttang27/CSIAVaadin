package application.views;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import application.views.code.GroupManager;
import application.views.code.Manager;
import application.views.code.NavTab;
import application.views.code.SettingsManager;
import application.views.code.StatManager;
import application.views.code.Task;
import application.views.code.TaskGrid;
import application.views.code.TaskManager;
import application.views.code.User;

@PWA(name = "Flow CRM Tutorial", shortName = "Flow CRM Tutorial", enableInstallPrompt = false)
@Theme(themeFolder = "taskerio")
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
    private String viewMode = "grid";
    private TaskGrid taskGrid;
    private static DateTimeFormatter defDTFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @ClientCallable
    public void windowClosed() {
        try {
			if (manager != null) {manager.save();};
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public TaskView() {
        UI.getCurrent().getPage().executeJs("function closeListener() { $0.$server.windowClosed(); } " +
        "window.addEventListener('beforeunload', closeListener); " +
        "window.addEventListener('unload', closeListener);",getElement());


        

        

        //TODO: ADD RANGE SLIDER



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
        settingsManager = manager.getSettings();


        Tabs tabs = new NavTab(manager,"Tasks");
        add(new VerticalLayout(tabs));


        
        grid = new Grid<>(Task.class,false);
        calendar = FullCalendarBuilder.create().build();

        TaskGrid taskGrid = new TaskGrid(grid, manager, calendar);
        taskGrid.addContextMenu();
        Button addButton = taskGrid.addTaskButton();
        HorizontalLayout sortFilter = taskGrid.addSorts();
        
        calendar.setHeight("500px");
        calendar.setWidth("1000px");
        calendar.setVisible(false);

        MenuBar showGrid = new MenuBar();
        showGrid.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        showGrid.addItem("Grid");
        MenuBar showCalendar = new MenuBar();
        showCalendar.addItem("Calendar");
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
        
        

        add(new HorizontalLayout(new HorizontalLayout(addButton,sortFilter),new HorizontalLayout(showGrid,showCalendar)));
        add(grid,calendar);
        
    }
    
}
