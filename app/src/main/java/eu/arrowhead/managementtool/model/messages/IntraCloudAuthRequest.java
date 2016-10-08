package eu.arrowhead.managementtool.model.messages;


import java.util.ArrayList;
import java.util.Collection;

import eu.arrowhead.managementtool.model.ArrowheadService;
import eu.arrowhead.managementtool.model.ArrowheadSystem;

public class IntraCloudAuthRequest {
	
	private ArrowheadSystem consumer;
	private Collection<ArrowheadSystem> providers = new ArrayList<ArrowheadSystem>();
	private ArrowheadService service;
	private boolean generateToken;	
	
	public IntraCloudAuthRequest() {
	}

	public IntraCloudAuthRequest(ArrowheadSystem consumer, Collection<ArrowheadSystem> providers,
			ArrowheadService service, boolean generateToken) {
		this.consumer = consumer;
		this.providers = providers;
		this.service = service;
		this.generateToken = generateToken;
	}
	
	public ArrowheadSystem getConsumer() {
		return consumer;
	}

	public void setConsumer(ArrowheadSystem consumer) {
		this.consumer = consumer;
	}

	public Collection<ArrowheadSystem> getProviders() {
		return providers;
	}

	public void setProviders(Collection<ArrowheadSystem> providers) {
		this.providers = providers;
	}

	public ArrowheadService getService() {
		return service;
	}

	public void setService(ArrowheadService service) {
		this.service = service;
	}

	public boolean isGenerateToken() {
		return generateToken;
	}

	public void setGenerateToken(boolean generateToken) {
		this.generateToken = generateToken;
	}
	
	
}
