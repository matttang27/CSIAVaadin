package com.example.application.views;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
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
    private ArrayList<VaadinIcon> vIcons = new ArrayList<VaadinIcon>(Arrays.asList(VaadinIcon.values()));
    private Collection<Icon> icons = new ArrayList<Icon>();
    private static DateTimeFormatter defDTFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
     
    public GroupView() {

        for (int i=0;i<vIcons.size();i++) {
            icons.add(new Icon(vIcons.get(i)));
        };
        
        
        
        
        
        
        

        //import data
        Component c = UI.getCurrent();
        manager = (Manager) ComponentUtil.getData(c,"manager");
        if (manager == null) {
            manager = new Manager();
            manager.setUser(new User("Matthew"));
        }

        Tabs tabs = new NavTab(manager,"Groups");
        add(new VerticalLayout(tabs));

        user = manager.getUser();
        taskManager = manager.getTasker();
        groupManager = manager.getGrouper();


        groupManager.addGroup(new Group("Test"));

        board = new VerticalLayout();
        
        PopulateBoard();

        Dialog addDialog = new Dialog();
        
        Button addButton = new Button("Add Group",e -> addDialog.open());
        add(addButton,board,addDialog);
        

        addDialog.add(addGroupLayout(addDialog));


        

    }
    public void PopulateBoard() {
        UI.getCurrent().getPage().retrieveExtendedClientDetails(receiver -> {
            //int screenWidth = receiver.getScreenWidth();
            board.removeAll();
            ArrayList<Group> groups = groupManager.getGroups();
            if (groups.size() != 0) {
                //how many items per row
                final int ROW = 4;
                for (int i=0;i<(groups.size()-1)/ROW + 1;i++) {
                    HorizontalLayout row = new HorizontalLayout();
                    row.setWidthFull();
                    row.add(groupToBoard(groups.get(i*ROW)));
                    for (int j=1;j<ROW;j++) {
                        if (i*ROW+j < groups.size()) {
                            row.add(groupToBoard(groups.get(i*ROW+j)));
                        }
                        else {
                            row.add(new VerticalLayout());
                            
                        }
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

        Div titlePart = new Div();
        titlePart.getStyle().set("display","inline-block");
        if (group.getIcon() != null) {
            titlePart.add(group.getIcon());
        }
        titlePart.add(title);
        boardPiece.add(titlePart,pending);
        VerticalLayout finalLayout = new VerticalLayout(colorPiece,boardPiece);
        ContextMenu groupMenu = new ContextMenu();
        groupMenu.setTarget(finalLayout);
        groupMenu.addItem("View", event -> {
            Dialog itemDialog = new Dialog();
            itemDialog.add(showGroupLayout(itemDialog,group));
            itemDialog.open();
        });
        //TODO: figure out how to color GridContextMenu options
        groupMenu.addItem("Delete", event -> {
            groupManager.removeGroup(group);
            PopulateBoard();
            
            
        });

        return finalLayout;
    }

    public VerticalLayout addGroupLayout(Dialog dialog) {
        //TODO: Improve Color Picker
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue("#808080");
        
        H2 title = new H2("Add a new group");

        ComboBox<Icon> selectIcon = new ComboBox<>();
        selectIcon.setItemLabelGenerator(i -> {
            
            return i.getElement().getAttribute("icon").split(":")[1];
        });
        selectIcon.setRenderer(new ComponentRenderer<>(icon -> {
            Div div = new Div();
            div.add(icon);
            div.add(new Text(icon.getElement().getAttribute("icon").split(":")[1]));
        
            return div;
        }));
        selectIcon.setLabel("Icon");
        
        
        selectIcon.setItems(icons);
        

        TextField nameField = new TextField("Task Name");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setErrorMessage("This field is required");

        NumberField goalField = new NumberField("Goal (>0)");
        goalField.setRequiredIndicatorVisible(false);
        goalField.setValue(0.0);

        VerticalLayout fieldLayout = new VerticalLayout(new HorizontalLayout(nameField,selectIcon),colorPicker,goalField);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Add", e -> {
            //checks whether any of the fields are empty, if true, sends a notification and cancels
            String error = "";
            if (nameField.getValue() == null) {
                error = "Fill in all required fields";
            }
            else if (goalField.getValue() != null && goalField.getValue().intValue() < 0) {
                error = "Priority must be 0 or greater";
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
            newGroup.setIcon(selectIcon.getValue());
            newGroup.setGoal(goalField.getValue().intValue());
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

    public VerticalLayout showGroupLayout(Dialog dialog,Group group) {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(group.getColor());
        
        H1 title = new H1(group.getName());

        ComboBox<Icon> selectIcon = new ComboBox<>();
        selectIcon.setItemLabelGenerator(i -> {
            
            return i.getElement().getAttribute("icon").split(":")[1];
        });
        selectIcon.setRenderer(new ComponentRenderer<>(icon -> {
            Div div = new Div();
            
            div.add(icon);
            div.add(new Text(icon.getElement().getAttribute("icon").split(":")[1]));
        
            return div;
        }));
        selectIcon.setLabel("Icon");
        
        
        selectIcon.setItems(icons);
        if (group.getIcon() != null) {
            selectIcon.setValue(group.getIcon());
        }
        

        TextField nameField = new TextField("Task Name");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setErrorMessage("This field is required");
        if (group.getName() != null) {
            nameField.setValue(group.getName());
        }
        NumberField goalField = new NumberField("Goal (>0)");
        goalField.setRequiredIndicatorVisible(false);
        goalField.setValue((double) group.getGoal());

        

        TextField createdField = new TextField("Date created");
        createdField.setReadOnly(true);
        createdField.setValue(group.getCreated().format(defDTFormat));
        TextField editField = new TextField("Last edited");
        editField.setValue(group.getLastEdited().format(defDTFormat));
        editField.setReadOnly(true);


        TextArea notesField = new TextArea("Notes");
        notesField.setValue(group.getNotes());

        VerticalLayout additionalLayout = new VerticalLayout(new HorizontalLayout(createdField,editField),notesField);

        Details additionalInfo = new Details("Additional Information",additionalLayout);
        additionalInfo.setOpened(false);


        VerticalLayout fieldLayout = new VerticalLayout(new HorizontalLayout(nameField,selectIcon),colorPicker,goalField,additionalInfo);

        Divider divider = new Divider();
        divider.getStyle().set("background-color",group.getColor());
        HorizontalLayout finalLayout = new HorizontalLayout(title,divider,fieldLayout);
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Save", e -> {
            //checks whether any of the fields are empty, if true, sends a notification and cancels
            String error = "";
            if (nameField.getValue() == null) {
                error = "Fill in all required fields";
            }
            else if (goalField.getValue() != null && goalField.getValue().intValue() < 0) {
                error = "Priority must be 0 or greater";
            }

            if (!error.equals("")) {
                Notification errorNotif = Notification.show(error);
                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotif.setDuration(2000);
                return;
            }
            group.setName(nameField.getValue());
            group.setLastEdited(LocalDateTime.now());
            group.setColor(colorPicker.getValue());
            group.setIcon(selectIcon.getValue());
            group.setNotes(notesField.getValue());
            
            PopulateBoard();

            Notification addTaskNotif = Notification.show("Group updated!");
            addTaskNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            addTaskNotif.setDuration(2000);
            dialog.close();


            
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton,saveButton);
        
        VerticalLayout dialogLayout = new VerticalLayout(finalLayout,buttonLayout);

        
        return dialogLayout;
    }

}
