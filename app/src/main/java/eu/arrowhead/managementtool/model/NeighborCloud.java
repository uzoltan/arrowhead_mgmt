package eu.arrowhead.managementtool.model;


public class NeighborCloud {

	private ArrowheadCloud cloud;
	
	public NeighborCloud(){
	}
	
	public NeighborCloud(ArrowheadCloud cloud) {
		this.cloud = cloud;
	}
	
	public ArrowheadCloud getCloud() {
		return cloud;
	}

	public void setCloud(ArrowheadCloud cloud) {
		this.cloud = cloud;
	}

	
}
