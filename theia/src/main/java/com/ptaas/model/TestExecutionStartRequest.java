package com.ptaas.model;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonPropertyOrder({
    "sut",
"jmeter"
})
@JsonRootName("testExecutionRequest")
public class TestExecutionStartRequest {

    private String systemConfiguration;
    
    @NotNull
    private String description;
    
    @NotNull
    private String corpId;
    
	@Valid
	@NotNull
	@JsonProperty("sut")
	private Sut sut;
	
	@Valid
	@NotNull
	@JsonProperty("jmeter")
	private Jmeter jmeter;
	
	public static class Sut {
		
		@NotNull
		private String stageName;
		
		public Sut() {
			
		}

		public String getStageName() {
			return stageName;
		}

		public void setStageName(String stageName) {
			this.stageName = stageName;
		}
		
		@Override
		public String toString() {
			return String.format("stagename=%s", stageName);
		}
		
	}
	
	public static class Jmeter {
		
		@NotNull
		private String machineName;
		
		@Valid
		private Config config;

        public static class Config {
			
			@NotNull
			private String scriptName;
			
			@NotNull
			private int Vusers;
			
			@NotNull
			@Min(value=300)
			private int duration;
			
			private String params;
						
			public Config() {}

			public String getScriptName() {
				return scriptName;
			}

			public void setScriptName(String scriptName) {
				this.scriptName = scriptName;
			}

			public int getVusers() {
				return Vusers;
			}

			public void setVusers(int vusers) {
				Vusers = vusers;
			}

			public int getDuration() {
				return duration;
			}

			public void setDuration(int duration) {
				this.duration = duration;
			}

			public String getParams() {
				return params;
			}

			public void setParams(String params) {
				this.params = params;
			}
			
			
			@Override
			public String toString() {
				return String.format("scriptname=%s , vusers=%s , duration=%d , params = %s", scriptName,Vusers,duration,params);
			}
		}

		public String getMachineName() {
			return machineName;
		}

		public void setMachineName(String machineName) {
			this.machineName = machineName;
		}
		
		public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }
		
		@Override
		public String toString() {
		    return String.format("machineName=%s , config=%s",machineName,config);
		}
	}

	public Sut getSut() {
		return sut;
	}

	public void setSut(Sut sut) {
		this.sut = sut;
	}

	public Jmeter getJmeter() {
		return jmeter;
	}

	public void setJmeter(Jmeter jmeter) {
		this.jmeter = jmeter;
	}
	
	

	public String getSystemConfiguration() {
        return systemConfiguration;
    }

    public void setSystemConfiguration(String systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
    
    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    @Override
	public String toString() {
	    return String.format(" corpId=%s, description=%s\n, systemConfiguration = %s\n, sut = %s, jmeter = %s\n",corpId,description,systemConfiguration,sut,jmeter);
	}
	
}
