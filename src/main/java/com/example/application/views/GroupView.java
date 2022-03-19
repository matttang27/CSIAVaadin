package com.example.application.views;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import com.example.application.views.code.*;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.*;
import java.awt.Color;
@Theme(themeFolder = "flowcrmtutorial")
@PageTitle("Groups")
@Route(value = "groups")
public class GroupView extends VerticalLayout {
    private VerticalLayout board;
    private User user;
    private Manager manager;
    private TaskManager taskManager;
    private GroupManager groupManager;
     
    public GroupView() {
        Tab thisTab = new Tab("Groups");
        Tabs tabs = new Tabs(new Tab("Tasks"),thisTab, new Tab("Schedule"),new Tab("Statistics"),new Tab("Tools"));
        tabs.addSelectedChangeListener(listener -> {
            Tab tab = listener.getSelectedTab();
            String tabName = tab.getLabel();
            if (tabName.equals("Tasks")) {
                tabs.getUI().ifPresent(ui -> {
                    Component c = ui.getCurrent();
                    ComponentUtil.setData(c,"manager",manager);
                    ui.navigate("tasks");
                });
            }
        });
        tabs.setSelectedTab(thisTab);
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


        groupManager.addGroup(new Group("Test"));

        board = new VerticalLayout();
        add(board);
        PopulateBoard();

        Dialog addDialog = new Dialog();
        add(addDialog);
        Button addButton = new Button("Add Group",e -> addDialog.open());
        add(addButton);
        

        addDialog.add(addGroupLayout(addDialog));


        

    }
    public void PopulateBoard() {
        UI.getCurrent().getPage().retrieveExtendedClientDetails(receiver -> {
            int screenWidth = receiver.getScreenWidth();
            board.removeAll();
            ArrayList<Group> groups = groupManager.getGroups();
            if (groups.size() != 0) {
                for (int i=0;i<Math.ceil(groups.size()+1/2);i++) {
                    HorizontalLayout row = new HorizontalLayout();
                    row.add(groupToBoard(groups.get(i*2)));
                    if (i*2+1 < groups.size()) {
                        row.add(groupToBoard(groups.get(i*2+1)));
                    }
                    else {
                        row.add(new VerticalLayout());
                    }

                    board.add(row);
                }
            }
        });
        
        
    }

    public VerticalLayout groupToBoard(Group group) {
        if (group == null) {
            return new VerticalLayout();
        }
        VerticalLayout boardPiece = new VerticalLayout();

        VerticalLayout colorPiece = new VerticalLayout();
        colorPiece.getStyle().set("background-color",group.getColor());
        boardPiece.getStyle().set("border","1px solid grey");
        boardPiece.getStyle().set("margin","0px");
        
        H3 title = new H3(group.getName());

        
        Span pending = new Span("Doing");
        pending.getElement().getThemeList().add("badge");

        boardPiece.add(colorPiece,title,pending);

        return new VerticalLayout(colorPiece,boardPiece);
    }

    public VerticalLayout addGroupLayout(Dialog dialog) {
        //TODO: Improve Color Picker
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue("#808080");
        
        H2 title = new H2("Add a new task");

        TextField nameField = new TextField("Task Name");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setErrorMessage("This field is required");

        NumberField goalField = new NumberField("Goal (>0)");
        goalField.setRequiredIndicatorVisible(false);

        VerticalLayout fieldLayout = new VerticalLayout(new HorizontalLayout(nameField,colorPicker),goalField);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Add", e -> {
            //checks whether any of the fields are empty, if true, sends a notification and cancels
            String error = "";
            if (nameField.getValue() == null) {
                error = "Fill in all required fields";
            }
            else if (goalField.getValue() != null && goalField.getValue().intValue() < 1) {
                error = "Priority not in range";
            }

            if (!error.equals("")) {
                Notification errorNotif = Notification.show(error);
                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotif.setDuration(2000);
                return;
            }
            
            Group newGroup = new Group(nameField.getValue());
            newGroup.setLastEdited(LocalDateTime.now());
            newGroup.setColor(colorPicker.getValue());
            groupManager.addGroup(newGroup);
            
            PopulateBoard();

            Notification addTaskNotif = Notification.show("Added your Group!");
            addTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            addTaskNotif.setDuration(2000);
            dialog.close();


            
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton,saveButton);
        
        VerticalLayout dialogLayout = new VerticalLayout(title,fieldLayout,buttonLayout);

        
        return dialogLayout;
    }

}
