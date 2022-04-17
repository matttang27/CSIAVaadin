package application.views;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;

import application.views.code.FireBaseAuth;
import application.views.code.Manager;
@Theme(themeFolder = "taskerio")
@PageTitle("login")
@Route(value = "login")

public class LoginView extends VerticalLayout implements HasUrlParameter<String> {
    private boolean login = true;

    @Override
    public void setParameter(BeforeEvent event,
            @OptionalParameter String parameter) {
        
        if (parameter == null || !parameter.equals("signup")) {
            this.login = true;
            setup();
        }
        else {
            this.login = false;
            setup();
        }
    }
    public LoginView() {

        
        
        

    }
    public void setup() {
        InputStream serviceAccount;
        try {
            
            serviceAccount = this.getClass().getClassLoader().getResourceAsStream("key.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        catch (IllegalStateException e2) {
            System.out.println("Already has FirebaseApp instance!");
        }
            
        
        
        FirebaseAuth instance = FirebaseAuth.getInstance();
            
        
        // CreateRequest newUser = new CreateRequest();
        // newUser.setEmail("matttangclone1@gmail.com");
        // newUser.setPassword("Test1234");
        // instance.createUser(newUser);

        LoginI18n i18n = LoginI18n.createDefault();
        
        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setUsername("Email Address");
        
        LoginForm loginForm = new LoginForm();
        loginForm.setI18n(i18n);
        
        loginForm.setForgotPasswordButtonVisible(false);

        if (!login) {
            i18nForm.setTitle("Sign up");
            i18nForm.setSubmit("Sign up");
            i18n.setForm(i18nForm);
            loginForm.setI18n(i18n);
            login = false;
        }

        add(loginForm);
        loginForm.addLoginListener(listener -> {
            loginForm.setEnabled(true);


            String u = listener.getUsername();
            String p = listener.getPassword();

            if (p.length() < 6) {
                Notification notif = Notification.show("Password must be at least 6 characters long");
                notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                
                return;
            }

            if (login) {
                loginForm.setEnabled(true);
                
                FireBaseAuth auth = FireBaseAuth.getInstance();
                try {
                    String token = auth.auth(u, p);
                    if (token == null) {
                        Notification notif = Notification.show("Login failed, or account does not exist.");
                        notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        
                        return;
                    }
                    JsonObject userData = auth.getAccountInfo(token);
                    String localId = userData.get("localId").getAsString();
                    
                    System.out.println(userData);
                    
                    Firestore db = FirestoreClient.getFirestore();
                    DocumentReference docRef = db.collection("users").document(localId);
                    ApiFuture<DocumentSnapshot> future = docRef.get();
                    DocumentSnapshot document = future.get();
                    if (document.exists()) {
                        Map<String,Object> userDocument = document.getData();
                        
                        byte[] importData = document.getBlob("data").toBytes();
                        System.out.println("Document data: " + document.getData());
                        try {
                            ByteArrayInputStream bis = new ByteArrayInputStream(importData);
                            ObjectInput in = null;
                            try {
                                in = new ObjectInputStream(bis);
                                Manager manager = (Manager) in.readObject();
                                getUI().ifPresent(ui -> {
                                    Component c = ui.getCurrent();
                                    ComponentUtil.setData(c,"manager",manager);
                                    ui.navigate("tasks");
                                    
                                });
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } finally {
                            try {
                                if (in != null) {
                                in.close();
                                }
                            } catch (IOException ex) {
                                // ignore close exception
                            }
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    } 
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                
                CreateRequest newUser = new CreateRequest();
                newUser.setEmail(u);
                newUser.setPassword(p);
                try {
                    instance.createUser(newUser);
                    String uId = instance.getUserByEmail(u).getUid();

                    Manager manager = new Manager();
                    manager.setUid(uId);
                    if (manager != null) {manager.save();};
                    Notification notif = Notification.show("Account has been created!");
                    notif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (FirebaseAuthException e1) {
                    Notification notif = Notification.show("Email already has a registered account");
                    notif.addThemeVariants(NotificationVariant.LUMO_ERROR);


                } catch (IOException e1) {
                    Notification notif = Notification.show("Registration failed.");
                    notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
            
        });

        Button signUp = new Button("Sign up");
        if (!login) {
            signUp.setText("Login");
        }
        signUp.addClickListener(e -> {
            if (login) {
                i18nForm.setTitle("Sign up");
                i18nForm.setSubmit("Sign up");
                i18n.setForm(i18nForm);
                loginForm.setI18n(i18n);
                login = false;
                signUp.setText("Login");
            }
            else {
                i18nForm.setTitle("Login");
                i18nForm.setSubmit("Login");
                i18n.setForm(i18nForm);
                loginForm.setI18n(i18n);
                login = true;
                signUp.setText("Sign up");
            }
            
        });

        add(signUp);
    }
}
