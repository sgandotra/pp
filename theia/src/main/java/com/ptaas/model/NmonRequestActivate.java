package com.ptaas.model;

public class NmonRequestActivate {
	
	public NmonRequestActivate() {
		super();
		this.nmon = new Nmon();
	}
	
	public static class Nmon {
		
		private String frequency;
		private String samples;
		private String graphite;
		
		public String getFrequency() {
			return frequency;
		}
		public void setFrequency(String frequency) {
			this.frequency = frequency;
		}
		public String getSamples() {
			return samples;
		}
		public void setSamples(String samples) {
			this.samples = samples;
		}
		public String getGraphite() {
			return graphite;
		}
		public void setGraphite(String graphite) {
			this.graphite = graphite;
		}
		
		public Nmon() {
			super();
		}

	}
	
	private Nmon nmon;

	public Nmon getNmon() {
		return nmon;
	}

	public void setNmon(Nmon nmon) {
		this.nmon = nmon;
	}
	
	
}
