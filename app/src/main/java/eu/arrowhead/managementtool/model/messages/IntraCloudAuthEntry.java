package eu.arrowhead.managementtool.model.messages;


import java.util.ArrayList;

import eu.arrowhead.managementtool.model.ArrowheadService;
import eu.arrowhead.managementtool.model.ArrowheadSystem;

public class IntraCloudAuthEntry {
	
    private ArrowheadSystem consumer;
	private ArrayList<ArrowheadSystem> providerList = new ArrayList<ArrowheadSystem>();
    private ArrayList<ArrowheadService> serviceList = new ArrayList<ArrowheadService>();
	
    public IntraCloudAuthEntry(){
    }

	public IntraCloudAuthEntry(ArrowheadSystem consumer, ArrayList<ArrowheadSystem> providerList,
			ArrayList<ArrowheadService> serviceList) {
		this.consumer = consumer;
		this.providerList = providerList;
		this.serviceList = serviceList;
	}
    
	public ArrowheadSystem getConsumer() {
		return consumer;
	}

	public void setConsumer(ArrowheadSystem consumer) {
		this.consumer = consumer;
	}

	public ArrayList<ArrowheadSystem> getProviderList() {
		return providerList;
	}

	public void setProviderList(ArrayList<ArrowheadSystem> providerList) {
		this.providerList = providerList;
	}

	public ArrayList<ArrowheadService> getServiceList() {
		return serviceList;
	}

	public void setServiceList(ArrayList<ArrowheadService> serviceList) {
		this.serviceList = serviceList;
	}


}
