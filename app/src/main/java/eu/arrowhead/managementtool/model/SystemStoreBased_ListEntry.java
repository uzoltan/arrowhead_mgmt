package eu.arrowhead.managementtool.model;


import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class SystemStoreBased_ListEntry implements ParentListItem {

    private String serviceGroup;
    private String serviceDefition;
    private List<OrchestrationStore> storeEntries;

    public SystemStoreBased_ListEntry(String serviceGroup, String serviceDefition, List<OrchestrationStore> storeEntries) {
        this.serviceGroup = serviceGroup;
        this.serviceDefition = serviceDefition;
        this.storeEntries = storeEntries;
    }

    @Override
    public List<OrchestrationStore> getChildItemList() {
        return storeEntries;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getServiceDefition() {
        return serviceDefition;
    }

    public void setServiceDefition(String serviceDefition) {
        this.serviceDefition = serviceDefition;
    }

    public List<OrchestrationStore> getStoreEntries() {
        return storeEntries;
    }

    public void setStoreEntries(List<OrchestrationStore> storeEntries) {
        this.storeEntries = storeEntries;
    }
}
