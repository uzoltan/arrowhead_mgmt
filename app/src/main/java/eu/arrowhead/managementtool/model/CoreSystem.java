package eu.arrowhead.managementtool.model;


public class CoreSystem {

	private String systemName;
	private String address;
	private String port;
	private String serviceURI;
	private String authenticationInfo;
	private boolean isSecure;
	
	public CoreSystem(){
	}
	
	public CoreSystem(String systemName, String address, String port, 
				String serviceURI, String authenticationInfo, boolean isSecure) {
		this.systemName = systemName;
		this.address = address;
		this.port = port;
		this.serviceURI = serviceURI;
		this.authenticationInfo = authenticationInfo;
		this.isSecure = isSecure;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	public String getServiceURI() {
		return serviceURI;
	}

	public void setServiceURI(String serviceURI) {
		this.serviceURI = serviceURI;
	}

	public String getAuthenticationInfo() {
		return authenticationInfo;
	}

	public void setAuthenticationInfo(String authenticationInfo) {
		this.authenticationInfo = authenticationInfo;
	}

	public boolean getIsSecure() {
		return isSecure;
	}

	public void setIsSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}


}
