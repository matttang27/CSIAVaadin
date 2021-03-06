package application.views.code;
import java.util.ArrayList;

import java.io.Serializable;
import java.time.LocalDateTime;

//Manages the groups of the Application
public class GroupManager implements Serializable {
private static final long serialVersionUID = 1L;
    
    ArrayList<Group> groups;
    Manager manager;
    public GroupManager(Manager manager) {
        groups = new ArrayList<Group>();
        this.manager = manager;
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public ArrayList<Group> getGroups() {
        return this.groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public void addGroup(Group group) {
        group.setId(groups.size());
        groups.add(group);
    }

    public void removeGroup(Group group) {
        for (int i=0;i<groups.size();i++) {
            if (groups.get(i).getId() == group.getId()) {
                groups.remove(i);
            }
        }
    }

    public ArrayList<String> getGroupNames() {
        ArrayList<String> groupNames = new ArrayList<>();
        groups.forEach(group -> {groupNames.add(group.getName());});
        return groupNames;
    }

    public Group findByName(String name) {
        Group found = null;

        for (int i=0;i<groups.size();i++) {
            if (groups.get(i).getName().equals(name)) {
                return groups.get(i);
            }
        }
        
        return null;
    }

    public int getStatus(Group group) {
        
        //check status of group
        //0: Done, 1: Todo, 2: Overdue
        int status = 0;
        for (Task t: manager.getTasker().getTasks()) {
            
            if (!t.getDone() && t.getNextDue().isBefore(LocalDateTime.now()) && t.getGroup() == group) {
                return 2;
            }
            else if (!t.getDone() && status < 1 && t.getGroup() == group) {
                status = 1;
            }
        }
        return status;
    }
    


}
