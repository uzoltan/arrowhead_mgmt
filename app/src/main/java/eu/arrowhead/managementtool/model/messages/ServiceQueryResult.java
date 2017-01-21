package eu.arrowhead.managementtool.model.messages;


import java.util.ArrayList;
import java.util.List;

import eu.arrowhead.managementtool.model.ProvidedService;

public class ServiceQueryResult {

    private List<ProvidedService> serviceQueryData = new ArrayList<ProvidedService>();

    public ServiceQueryResult() {
    }

    public ServiceQueryResult(List<ProvidedService> serviceQueryData) {
        this.serviceQueryData = serviceQueryData;
    }

    public List<ProvidedService> getServiceQueryData() {
        return serviceQueryData;
    }

    public void setServiceQueryData(List<ProvidedService> serviceQueryData) {
        this.serviceQueryData = serviceQueryData;
    }

    public boolean isPayloadEmpty(){
        return serviceQueryData == null || serviceQueryData.isEmpty();
    }


}
