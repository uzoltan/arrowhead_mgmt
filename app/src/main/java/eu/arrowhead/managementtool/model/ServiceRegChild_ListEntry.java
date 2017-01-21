package eu.arrowhead.managementtool.model;


public class ServiceRegChild_ListEntry {

    private String serviceGroup;
    private String serviceDefinition;
    private String serviceUri;

    public ServiceRegChild_ListEntry() {
    }

    public ServiceRegChild_ListEntry(String serviceGroup, String serviceDefinition, String serviceUri) {
        this.serviceGroup = serviceGroup;
        this.serviceDefinition = serviceDefinition;
        this.serviceUri = serviceUri;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getServiceDefinition() {
        return serviceDefinition;
    }

    public void setServiceDefinition(String serviceDefinition) {
        this.serviceDefinition = serviceDefinition;
    }

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceRegChild_ListEntry that = (ServiceRegChild_ListEntry) o;

        if (!serviceGroup.equals(that.serviceGroup)) return false;
        if (!serviceDefinition.equals(that.serviceDefinition)) return false;
        return serviceUri.equals(that.serviceUri);

    }

    @Override
    public int hashCode() {
        int result = serviceGroup.hashCode();
        result = 31 * result + serviceDefinition.hashCode();
        result = 31 * result + serviceUri.hashCode();
        return result;
    }
}
