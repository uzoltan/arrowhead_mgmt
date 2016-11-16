package eu.arrowhead.managementtool.model;


import java.io.Serializable;

public class ArrowheadSystem implements Serializable {

	private String systemGroup;
	private String systemName;
	private String address;
	private String port;
	private String authenticationInfo;
	
	public ArrowheadSystem(){	
	}
	
	public ArrowheadSystem(String systemGroup, String systemName, String address, String port,
			String authenticationInfo) {
		this.systemGroup = systemGroup;
		this.systemName = systemName;
		this.address = address;
		this.port = port;
		this.authenticationInfo = authenticationInfo;
	}

	public String getSystemGroup() {
		return systemGroup;
	}

	public void setSystemGroup(String systemGroup) {
		this.systemGroup = systemGroup;
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

	public String getAuthenticationInfo() {
		return authenticationInfo;
	}

	public void setAuthenticationInfo(String authenticationInfo) {
		this.authenticationInfo = authenticationInfo;
	}

	@Override
	public String toString(){
		return "(" + systemGroup + ":" + systemName + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ArrowheadSystem))
			return false;
		ArrowheadSystem other = (ArrowheadSystem) obj;
		if (systemGroup == null) {
			if (other.systemGroup != null)
				return false;
		} else if (!systemGroup.equals(other.systemGroup))
			return false;
		if (systemName == null) {
			if (other.systemName != null)
				return false;
		} else if (!systemName.equals(other.systemName))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((systemGroup == null) ? 0 : systemGroup.hashCode());
		result = prime * result + ((systemName == null) ? 0 : systemName.hashCode());
		return result;
	}
}
