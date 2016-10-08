package eu.arrowhead.managementtool.model.messages;


import java.util.ArrayList;
import java.util.Collection;

import eu.arrowhead.managementtool.model.ArrowheadCloud;
import eu.arrowhead.managementtool.model.ArrowheadService;

public class InterCloudAuthEntry {
	
	private ArrowheadCloud cloud;
    private Collection<ArrowheadService> serviceList = new ArrayList<ArrowheadService>();
	
    public InterCloudAuthEntry(){
    }
    
	public InterCloudAuthEntry(ArrowheadCloud cloud, Collection<ArrowheadService> serviceList) {
		this.cloud = cloud;
		this.serviceList = serviceList;
	}

	public ArrowheadCloud getCloud() {
		return cloud;
	}

	public void setCloud(ArrowheadCloud cloud) {
		this.cloud = cloud;
	}

	public Collection<ArrowheadService> getServiceList() {
		return serviceList;
	}

	public void setServiceList(Collection<ArrowheadService> serviceList) {
		this.serviceList = serviceList;
	}
	
	
}
