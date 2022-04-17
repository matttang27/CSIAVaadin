package application.views;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;

import application.views.code.GroupManager;
import application.views.code.Manager;
import application.views.code.NavTab;
import application.views.code.StatManager;
import application.views.code.TaskManager;
import application.views.code.User;
@Theme(themeFolder = "taskerio")
@PageTitle("Statistics")
@Route(value = "statistics")

public class StatsView extends VerticalLayout {
    private Manager manager;
    private TaskManager taskManager;
    private GroupManager groupManager;
    private StatManager statManager;
    private User user;
    @ClientCallable
    public void windowClosed() {
        try {
			if (manager != null) {manager.save();};
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public StatsView() {

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

        NavTab tabs = new NavTab(manager,"Statistics");
        add(new VerticalLayout(tabs));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String current = Integer.toString(statManager.getStreak(LocalDate.now()));
        String currentRange = LocalDate.now().minusDays(statManager.getStreak(LocalDate.now())).format(dateFormatter) + " - " + LocalDate.now().format(dateFormatter);
        String longest = Integer.toString(statManager.getStreak(statManager.getLongestStreak()));
        String longestRange = LocalDate.now().minusDays(statManager.getStreak(statManager.getLongestStreak())).format(dateFormatter) + " - " + statManager.getLongestStreak().format(dateFormatter);
        add(new HorizontalLayout(new VerticalLayout(new H2("Current Streak:"),new H2(current),new H3(currentRange)),new VerticalLayout(new H2("Longest Streak:"),new H2(longest),new H3(longestRange))));


        DatePicker datePicker = new DatePicker(LocalDate.now());
        
        String taskCompleted = Integer.toString(statManager.getDay(datePicker.getValue()).getTasksCompleted());
        String taskCreated = Integer.toString(statManager.getDay(datePicker.getValue()).getTasksCreated());
        
        TextArea showCompleted = new TextArea("Tasks Completed");
        TextArea showCreated = new TextArea("Tasks Created");
        showCompleted.setValue(taskCompleted);
        showCreated.setValue(taskCreated);

        datePicker.addValueChangeListener(e -> {
            showCompleted.setValue(Integer.toString(statManager.getDay(datePicker.getValue()).getTasksCompleted()));
            showCreated.setValue(Integer.toString(statManager.getDay(datePicker.getValue()).getTasksCreated()));
        });

        add(new VerticalLayout(datePicker,new HorizontalLayout(showCompleted,showCreated)));

        
        

    }
}