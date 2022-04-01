package application.views;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import application.views.code.*;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.cloud.FirestoreClient;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
@PageTitle("Home")
@Route(value = "")
public class HomeView extends VerticalLayout {
    
    public HomeView() {

        Button loginButton = new Button("Login",e-> {e.getSource().getUI().get().navigate("login");});
        
        Button signUpButton = new Button("Sign Up", e-> {e.getSource().getUI().get().navigate("login");});
        HorizontalLayout enterButtons = new HorizontalLayout(loginButton,signUpButton);
        enterButtons.setWidthFull();
        enterButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        H1 title = new H1("Welcome to Taskerio!");
        title.getStyle().set("font-size","60px");
        title.getStyle().set("color","white");
        
        HorizontalLayout titleBackground = new HorizontalLayout(title);
        titleBackground.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        titleBackground.getStyle().set("background-color","#a500b8");
        titleBackground.setWidthFull();
        
        H1 subtitle = new H1("Your personal task management program.");
        subtitle.getStyle().set("color","#474747");
        VerticalLayout titlePart = new VerticalLayout(enterButtons,titleBackground,subtitle);
        titlePart.setAlignItems(FlexComponent.Alignment.CENTER);
        add(titlePart);

    }
}
