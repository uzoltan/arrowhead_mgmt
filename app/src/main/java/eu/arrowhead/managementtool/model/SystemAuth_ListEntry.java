package eu.arrowhead.managementtool.model;


import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class SystemAuth_ListEntry implements ParentListItem {

    private String serviceGroup;
    private String serviceDefition;
    private List<ArrowheadSystem> providers;

    public SystemAuth_ListEntry(String serviceGroup, String serviceDefition, List<ArrowheadSystem> providers) {
        this.serviceGroup = serviceGroup;
        this.serviceDefition = serviceDefition;
        this.providers = providers;
    }

    @Override
    public List<ArrowheadSystem> getChildItemList() {
        return providers;
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

    public List<ArrowheadSystem> getProviders() {
        return providers;
    }

    public void setProviders(List<ArrowheadSystem> providers) {
        this.providers = providers;
    }
}
