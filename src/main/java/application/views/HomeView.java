package application.views;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
@Theme(themeFolder = "taskerio")
@PageTitle("Home")
@Route(value = "")

public class HomeView extends VerticalLayout {
    
    public HomeView() {

        Button loginButton = new Button("Login",e-> {e.getSource().getUI().get().navigate("login");});
        
        Button signUpButton = new Button("Sign Up", e-> {e.getSource().getUI().get().navigate("login/signup");});
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
        H1 author = new H1("By Matthew Tang");
        author.getStyle().set("color","grey").set("font-size","40px");
        
        VerticalLayout titlePart = new VerticalLayout(enterButtons,titleBackground,subtitle,author);
        titlePart.setAlignItems(FlexComponent.Alignment.CENTER);
        add(titlePart);

    }
}
