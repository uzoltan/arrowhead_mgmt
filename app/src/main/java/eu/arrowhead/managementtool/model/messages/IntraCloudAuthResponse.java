package eu.arrowhead.managementtool.model.messages;


import java.util.HashMap;

import eu.arrowhead.managementtool.model.ArrowheadSystem;

public class IntraCloudAuthResponse {
	
	private HashMap<ArrowheadSystem, Boolean> authorizationState = new HashMap<ArrowheadSystem, Boolean>();
	
	public IntraCloudAuthResponse() {
	}

	public IntraCloudAuthResponse(HashMap<ArrowheadSystem, Boolean> authorizationState) {
		this.authorizationState = authorizationState;
	}
	
	public HashMap<ArrowheadSystem, Boolean> getAuthorizationMap() {
		return authorizationState;
	}

	public void setAuthorizationMap(HashMap<ArrowheadSystem, Boolean> authorizationState) {
		this.authorizationState = authorizationState;
	}


}
