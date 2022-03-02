package com.example.application.views;
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
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.*;
@Theme(themeFolder = "flowcrmtutorial")
@PageTitle("login")
@Route(value = "")
public class LoginView extends VerticalLayout {
    public LoginView() {
        //Fake login for now
        LoginForm loginForm = new LoginForm();
        add(loginForm);
        loginForm.addLoginListener(listener -> {
            System.out.println(listener);
            String u = listener.getUsername();
            String p = listener.getPassword();

            Manager manager = new Manager();
            manager.setUser(new User(u));
            if (u.equals("matttang27") && p.equals("1234")) {
                loginForm.getUI().ifPresent(ui -> {
                    Component c = ui.getCurrent();
                    ComponentUtil.setData(c,"manager",manager);
                    ui.navigate("tasks");
                });
                
                

            }
            else {
                Notification notif = new Notification();
                notif.show("Login failed");
                listener.getSource().setEnabled(true);
            }
            
        });
        
        

    }
}
