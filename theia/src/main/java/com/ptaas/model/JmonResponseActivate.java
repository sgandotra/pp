package com.ptaas.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
"services",
"jmonresponse",
"timestamp"
})
public class JmonResponseActivate {

	private List<String> jmonresponse;
	private String timestamp;
	private Error error;
	private Services services;
	
	public static class Error {
		public Error() {
			super();
		}
	}
	
	public static class Services {
		
		private List<Valid> valid;
		
		private List<Invalid> invalid;
		
		public Services() {
			super();
		}

		public List<Valid> getValid() {
			return valid;
		}

		public void setValid(List<Valid> valid) {
			this.valid = valid;
		}

		public List<Invalid> getInvalid() {
			return invalid;
		}

		public void setInvalid(List<Invalid> invalid) {
			this.invalid = invalid;
		}
		
		public static class Valid {
			private String name;
			private String type;
			private String serviceDeploymentFolder;
			private String state;
			private String port;
			
			public Valid() {
				super();
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			public String getServiceDeploymentFolder() {
				return serviceDeploymentFolder;
			}

			public void setServiceDeploymentFolder(String serviceDeploymentFolder) {
				this.serviceDeploymentFolder = serviceDeploymentFolder;
			}

			public String getState() {
				return state;
			}

			public void setState(String state) {
				this.state = state;
			}

			public String getPort() {
				return port;
			}

			public void setPort(String port) {
				this.port = port;
			}
			
			
		}
		
		public static class Invalid {
			
			private String name;
			private String type;
			private String serviceDeploymentFolder;
			private String state;
			private String port;
			
			public Invalid() {
				super();
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

			public String getServiceDeploymentFolder() {
				return serviceDeploymentFolder;
			}

			public void setServiceDeploymentFolder(String serviceDeploymentFolder) {
				this.serviceDeploymentFolder = serviceDeploymentFolder;
			}

			public String getState() {
				return state;
			}

			public void setState(String state) {
				this.state = state;
			}

			public String getPort() {
				return port;
			}

			public void setPort(String port) {
				this.port = port;
			}
			
		}
		
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public Services getServices() {
		return services;
	}

	public void setServices(Services services) {
		this.services = services;
	}

	public List<String> getJmonresponse() {
		return jmonresponse;
	}

	public void setJmonresponse(List<String> jmonresponse) {
		this.jmonresponse = jmonresponse;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
}
