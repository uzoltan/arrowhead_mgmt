package eu.arrowhead.managementtool.model.messages;


import eu.arrowhead.managementtool.model.ArrowheadCloud;
import eu.arrowhead.managementtool.model.ArrowheadService;

public class InterCloudAuthRequest {
	
	private ArrowheadCloud cloud;
	private ArrowheadService service;
	private boolean generateToken;
	
	public InterCloudAuthRequest(){
	}

	public InterCloudAuthRequest(ArrowheadCloud cloud, ArrowheadService service, boolean generateToken) {
		this.cloud = cloud;
		this.service = service;
		this.generateToken = generateToken;
	}

	public ArrowheadCloud getCloud() {
		return cloud;
	}

	public void setCloud(ArrowheadCloud cloud) {
		this.cloud = cloud;
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
