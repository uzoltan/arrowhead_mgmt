package eu.arrowhead.managementtool.model;


public class InterCloudAuthorization {

	private ArrowheadCloud cloud;
	private ArrowheadService service;
	
	public InterCloudAuthorization() {
	}

	public InterCloudAuthorization(ArrowheadCloud cloud, ArrowheadService service) {
		this.cloud = cloud;
		this.service = service;
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

	
}
