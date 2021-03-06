package eu.arrowhead.managementtool.model;


public class IntraCloudAuthorization {

	private int id;
	private ArrowheadSystem consumer;
	private ArrowheadSystem provider;
	private ArrowheadService service;

	public IntraCloudAuthorization() {
	}

	public IntraCloudAuthorization(ArrowheadSystem consumer, ArrowheadSystem provider, 
			ArrowheadService service) {
		this.consumer = consumer;
		this.provider = provider;
		this.service = service;
	}

	public int getId() {
		return id;
	}

    public void setId(int id) {
        this.id = id;
    }

    public ArrowheadSystem getConsumer() {
		return consumer;
	}

	public void setConsumer(ArrowheadSystem consumer) {
		this.consumer = consumer;
	}

	public ArrowheadSystem getProvider() {
		return provider;
	}
	
	public void setProvider(ArrowheadSystem providers) {
		this.provider = providers;
	}

	public ArrowheadService getService() {
		return service;
	}

	public void setService(ArrowheadService service) {
		this.service = service;
	}

	
}
