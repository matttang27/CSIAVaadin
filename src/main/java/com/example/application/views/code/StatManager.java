package com.example.application.views.code;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map.Entry;

public class StatManager {
    private HashMap<LocalDate,DayStat> data;
    private Manager manager;
    public StatManager(Manager manager) {
        data = new HashMap<LocalDate,DayStat>();
        this.manager = manager;
    }

    public HashMap<LocalDate,DayStat> getData() {
        return this.data;
    }

    public void setData(HashMap<LocalDate,DayStat> data) {
        this.data = data;
    }
    

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
    
}
