package eu.arrowhead.managementtool.model;


import java.util.Date;

public class OrchestrationStore{

	private ArrowheadSystem consumer;
	private ArrowheadService service;
	private ArrowheadSystem providerSystem;
	private ArrowheadCloud providerCloud;
	private Integer priority;
	private boolean isActive;
	private String name;
	private Date lastUpdated;
	private String orchestrationRule;

    //Need this boolean to decide which System details (consumer or provider) I show in the store based list child views.
    private boolean isConsumerSide;

	public OrchestrationStore(){
	}
	
	public OrchestrationStore(ArrowheadSystem consumer, ArrowheadService service,
			ArrowheadSystem providerSystem, ArrowheadCloud providerCloud, Integer priority) {
		this.consumer = consumer;
		this.service = service;
		this.providerSystem = providerSystem;
		this.providerCloud = providerCloud;
		this.priority = priority;
	}

	public OrchestrationStore(ArrowheadSystem consumer, ArrowheadService service, 
			ArrowheadSystem providerSystem, ArrowheadCloud providerCloud, Integer priority, 
			boolean isActive, String name, Date lastUpdated, String orchestrationRule) {
		this.consumer = consumer;
		this.service = service;
		this.providerSystem = providerSystem;
		this.providerCloud = providerCloud;
		this.priority = priority;
		this.isActive = isActive;
		this.name = name;
		this.lastUpdated = lastUpdated;
		this.orchestrationRule = orchestrationRule;
	}

	public ArrowheadSystem getConsumer() {
		return consumer;
	}

	public void setConsumer(ArrowheadSystem consumer) {
		this.consumer = consumer;
	}

	public ArrowheadService getService() {
		return service;
	}

	public void setService(ArrowheadService service) {
		this.service = service;
	}

	public ArrowheadSystem getProviderSystem() {
		return providerSystem;
	}

	public void setProviderSystem(ArrowheadSystem providerSystem) {
		this.providerSystem = providerSystem;
	}

	public ArrowheadCloud getProviderCloud() {
		return providerCloud;
	}

	public void setProviderCloud(ArrowheadCloud providerCloud) {
		this.providerCloud = providerCloud;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getOrchestrationRule() {
		return orchestrationRule;
	}

	public void setOrchestrationRule(String orchestrationRule) {
		this.orchestrationRule = orchestrationRule;
	}

    public boolean isConsumerSide() {
        return isConsumerSide;
    }

    public void setConsumerSide(boolean consumerSide) {
        isConsumerSide = consumerSide;
    }
}
