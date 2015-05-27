package com.ptaas.model;

import java.util.Collection;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"test",
"hosts"
})
public class MonitoredHostsModel {
	
	@NotNull
	private String configName;
	
	private boolean isFavorite;
	
	private boolean isRunning;
	
	@Valid
	@JsonProperty("hosts")
	private Map<String,Host> hosts;
	
	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    
    public Map<String,Host> getHosts() {
        return hosts;
    }

    public void setHosts(Map<String,Host> hosts) {
        this.hosts = hosts;
    }   

    public MonitoredHostsModel() {
		super();
	}

	public static class Host {
	
		@JsonProperty("hostName")
		private String hostName;
		
		@JsonProperty("error")
		private String error;

		private boolean nmon;
		
		private long nmonStart;
		
		private long nmonEnd;
		
		@Valid
		@JsonProperty("monitoredServices")
		private Collection<MonitoredService> monitoredServices;
		
		public Host() {
			super();
		}
		
		
		public static class MonitoredService {
		
			public static enum Type {
				JAVA,
				NODE,
				UNKNOWN
			}
			
			private String name;
			
			private String location;
			
			private Type type;
			
			private String hostName;
			
			public MonitoredService( ) {
				super();
			}
		
			public String getName() {
				return name;
			}
		
			public void setName(String name) {
				this.name = name;
			}
		
			public String getLocation() {
				return location;
			}
		
			public void setLocation(String location) {
				this.location = location;
			}
		
			public Type getType() {
				return type;
			}
		
			public void setType(Type type) {
				this.type = type;
			}

			public String getHostName() {
				return hostName;
			}

			public void setHostName(String hostName) {
				this.hostName = hostName;
			}
			
			
		}
		
		
	
		public String getError() {
			return error;
		}
	
		public void setError(String error) {
			this.error = error;
		}
	
		public String getHostName() {
			return hostName;
		}
	
		public void setHostName(String hostName) {
			this.hostName = hostName;
		}
	
		public Collection<MonitoredService> getMonitoredServices() {
			return monitoredServices;
		}
	
		public void setMonitoredServices(Collection<MonitoredService> monitoredServices) {
			this.monitoredServices = monitoredServices;
		}

		public boolean isNmon() {
			return nmon;
		}

		public void setNmon(boolean nmon) {
			this.nmon = nmon;
		}

		public long getNmonStart() {
			return nmonStart;
		}

		public void setNmonStart(long nmonStart) {
			this.nmonStart = nmonStart;
		}

        public long getNmonEnd() {
            return nmonEnd;
        }

        public void setNmonEnd(long nmonEnd) {
            this.nmonEnd = nmonEnd;
        }
		
	}
	
}
