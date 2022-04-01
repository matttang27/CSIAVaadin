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
@Route(value = "login")
public class LoginView extends VerticalLayout {
    
    public LoginView() {

        FileInputStream serviceAccount;
        try {
            FirebaseApp.getInstance();
        }
        catch (IllegalStateException e) {
            try {
                serviceAccount = new FileInputStream("src/main/java/application/key.json");
            
            
            FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();
            FirebaseApp.initializeApp(options);
            
            String UID = "qoswA226dgZygV45kgPZqX3QKnI2";
            FirebaseAuth instance = FirebaseAuth.getInstance();
            
            //UserRecord userRecord = instance.generateSignInWithEmailLink("matttang27@gmail.com", ActionCodeSettings.builder().build());
            CreateRequest newUser = new CreateRequest();
            newUser.setEmail("matttangclone1@gmail.com");
            newUser.setPassword("Test1234");
            instance.createUser(newUser);

            Firestore db = FirestoreClient.getFirestore();
            // See the UserRecord reference doc for the contents of userRecord.
            //System.out.println("Successfully fetched user data: " + userRecord.getUid());
            DocumentReference docRef = db.collection("users").document(UID);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            // ...
            // future.get() blocks on response
            DocumentSnapshot document = future.get();
            if (document.exists()) {
            System.out.println("Document data: " + document.getData());
            } else {
            System.out.println("No such document!");
            }
            }
            catch (IOException | InterruptedException | ExecutionException e1) {
                
                e1.printStackTrace();
            } catch (FirebaseAuthException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        


        
        //Fake login for now
        LoginForm loginForm = new LoginForm();
        add(loginForm);
        loginForm.addLoginListener(listener -> {
            
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
                Notification notif = Notification.show("Login failed");
                listener.getSource().setEnabled(true);
            }
            
        });
        
        

    }
}
