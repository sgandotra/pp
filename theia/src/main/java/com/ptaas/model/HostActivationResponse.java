package com.ptaas.model;

public class HostActivationResponse {
	
	
	private String configName;
	
	private Host host;
	
	private String errMsg;
	
	public HostActivationResponse() {
		super();
	}

	public static class Host {
		
		private String hostName;
		private NmonResponseActivate nmonResponseActivate;
		private NodemonResponseActivate nodeMonResponseActivate;
		private JmonResponseActivate jmonResponseActivate;
		private JmonResponseDeActivate jmonResponseDeActivate;
		
		
		public Host() {
			super();
		}


		public String getHostName() {
			return hostName;
		}


		public void setHostName(String hostName) {
			this.hostName = hostName;
		}


		public NmonResponseActivate getNmonResponseActivate() {
			return nmonResponseActivate;
		}


		public void setNmonResponseActivate(NmonResponseActivate nmonResponseActivate) {
			this.nmonResponseActivate = nmonResponseActivate;
		}


		public NodemonResponseActivate getNodeMonResponseActivate() {
			return nodeMonResponseActivate;
		}


		public void setNodeMonResponseActivate(
				NodemonResponseActivate nodemonresponse) {
			this.nodeMonResponseActivate = nodemonresponse;
		}


		public JmonResponseActivate getJmonResponseActivate() {
			return jmonResponseActivate;
		}


		public void setJmonResponseActivate(JmonResponseActivate jmonResponseActivate) {
			this.jmonResponseActivate = jmonResponseActivate;
		}


		public JmonResponseDeActivate getJmonResponseDeActivate() {
			return jmonResponseDeActivate;
		}


		public void setJmonResponseDeActivate(JmonResponseDeActivate jmonResponseDeActivate) {
			this.jmonResponseDeActivate = jmonResponseDeActivate;
		}

	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
	
}
