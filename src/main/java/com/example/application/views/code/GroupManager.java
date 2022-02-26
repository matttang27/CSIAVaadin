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

    


}
