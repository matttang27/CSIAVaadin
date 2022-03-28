package com.example.application.views.code;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

public class NavTab extends Tabs {
    private Manager manager;
    public NavTab(Manager manager, String selected) {
        this.manager = manager;
        add(new Tab("Tasks"),new Tab("Groups"), new Tab("Schedule"),new Tab("Statistics"),new Tab("Tools"));
        addSelectedChangeListener(listener -> {
            Tab tab = listener.getSelectedTab();
            if (tab == null) {
                return;
            }
            String tabName = tab.getLabel();
            
            getUI().ifPresent(ui -> {
                Component c = ui.getCurrent();
                ComponentUtil.setData(c,"manager",this.manager);
                ui.navigate(tabName.toLowerCase());
            });
        });
        
        setSelectedTab(findTab(selected));
        setOrientation(Tabs.Orientation.HORIZONTAL);
        setWidth("100%");
        addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
    }
    public Tab findTab(String finder) {
        for (int i=0;i<getComponentCount();i++) {
            if (getComponentAt(i).getElement().getText() == finder) {
                return (Tab) getComponentAt(i);
            }
        }
        return null;
    }
}

