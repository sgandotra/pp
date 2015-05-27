package com.ptaas.model;

import java.util.ArrayList;
import java.util.List;

public class NodemonRequestActivate {

	private List<Services> services;
	
	public NodemonRequestActivate() {
		super();
		services = new ArrayList<Services>();
	}
	
	public static class Services {
		
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		
	}

	public List<Services> getServices() {
		return services;
	}

	public void setServices(List<Services> services) {
		this.services = services;
	}
	
	
	
}
