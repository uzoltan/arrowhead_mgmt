package eu.arrowhead.managementtool.model;


import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

public class ServiceRegParent_ListEntry implements ParentListItem {

    private String systemGroup;
    private String systemName;
    private List<ServiceRegChild_ListEntry> services = new ArrayList<>();

    public ServiceRegParent_ListEntry() {
    }

    public ServiceRegParent_ListEntry(String systemGroup, String systemName, List<ServiceRegChild_ListEntry> services) {
        this.systemGroup = systemGroup;
        this.systemName = systemName;
        this.services = services;
    }

    @Override
    public List<?> getChildItemList() {
        return services;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getSystemGroup() {
        return systemGroup;
    }

    public void setSystemGroup(String systemGroup) {
        this.systemGroup = systemGroup;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public List<ServiceRegChild_ListEntry> getServices() {
        return services;
    }

    public void setServices(List<ServiceRegChild_ListEntry> services) {
        this.services = services;
    }


}
