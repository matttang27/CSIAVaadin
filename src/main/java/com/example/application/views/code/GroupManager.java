package com.example.application.views.code;
import java.util.ArrayList;
//honestly I'm not sure if I even need a GroupManager
public class GroupManager {
    ArrayList<Group> groups;

    public GroupManager() {
        groups = new ArrayList<Group>();
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
    


}
