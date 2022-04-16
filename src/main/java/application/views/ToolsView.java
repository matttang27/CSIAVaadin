package application.views;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;

import org.vaadin.olli.FileDownloadWrapper;

import application.views.code.GroupManager;
import application.views.code.Manager;
import application.views.code.NavTab;
import application.views.code.StatManager;
import application.views.code.TaskManager;
import application.views.code.User;
@Theme(themeFolder = "flowcrmtutorial")
@PageTitle("Tools")
@Route(value = "tools")
public class ToolsView extends VerticalLayout {
    private Manager manager;
    private TaskManager taskManager;
    private GroupManager groupManager;
    private StatManager statManager;
    private SettingsManager settings;
    private User user;
    @ClientCallable
    public void windowClosed() {
        try {
			if (manager != null) {manager.save();};
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public ToolsView() {

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
        settings = manager.getSettings();

        NavTab tabs = new NavTab(manager,"Tools");
        add(new VerticalLayout(tabs));

        Button exportButton = new Button("Export");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;

        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(manager);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        byte [] data = bos.toByteArray();
        FileDownloadWrapper buttonWrapper = new FileDownloadWrapper("textfield.txt", () -> data);
        buttonWrapper.wrapComponent(exportButton);
        add(buttonWrapper);

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload singleFileUpload = new Upload(memoryBuffer);

        singleFileUpload.addSucceededListener(event -> {
            // Get information about the uploaded file
            InputStream fileData = memoryBuffer.getInputStream();
            String fileName = event.getFileName();
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();

            try (fileData) {
                byte[] bytes = fileData.readAllBytes();
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInput in = null;
                try {
                    in = new ObjectInputStream(bis);
                    Manager manager = (Manager) in.readObject();
                    this.manager = manager;
                    tabs.setManager(this.manager);
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
            
            
            
        });

        add(singleFileUpload);


        
        

        
        


        
        

    }
}