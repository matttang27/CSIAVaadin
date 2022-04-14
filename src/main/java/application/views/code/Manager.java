package application.views.code;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Blob;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

//The big boy - Root Manager
public class Manager implements Serializable {
private static final long serialVersionUID = 1L;
    

    private TaskManager tasker;
    private GroupManager grouper;
    private StatManager stater;
    private User user;
    private StatManager stats;
    private SettingsManager settings;
    private ScheduleManager scheduler;
    private String uid;

    public void save() throws IOException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("users").document(this.uid);
        HashMap<String, Object> putData = new HashMap<String, Object>();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        
        byte[] byteArray = bos.toByteArray();

        putData.put("data", Blob.fromBytes(byteArray));

        ApiFuture<WriteResult> writeFuture = docRef.set(putData);
    }
    public void setScheduler(ScheduleManager scheduler) {
        this.scheduler = scheduler;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public SettingsManager getSettings() {
        return this.settings;
    }

    public void setSettings(SettingsManager settings) {
        this.settings = settings;
    }

    public GroupManager getGrouper() {
        return this.grouper;
    }

    public void setGrouper(GroupManager grouper) {
        this.grouper = grouper;
    }

    public ScheduleManager getScheduler() {
        return this.scheduler;
    }

    public void setSchedule(ScheduleManager scheduler) {
        this.scheduler = scheduler;

    }

    public Manager() {
        tasker = new TaskManager(this);
        grouper = new GroupManager(this);
        stater = new StatManager(this);
        scheduler = new ScheduleManager(this);
        settings = new SettingsManager(this);
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TaskManager getTasker() {
        return this.tasker;
    }

    public void setTasker(TaskManager tasker) {
        this.tasker = tasker;
    }

    @Override
    public String toString() {
        return "{" +
                " tasker='" + getTasker() + "'" +
                "}";
    }

    public StatManager getStater() {
        return this.stater;
    }

    public void setStater(StatManager stater) {
        this.stater = stater;
    }

    
}
