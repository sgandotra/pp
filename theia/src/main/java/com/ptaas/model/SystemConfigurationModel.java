package com.ptaas.model;

import java.util.Collection;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SystemConfigurationModel {

	@NotNull
	private String name;
	
	@NotNull
	@Size(min=1)
	private Collection<String> hosts;
	
	public SystemConfigurationModel() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<String> getHosts() {
		return hosts;
	}

	public void setHosts(Collection<String> hosts) {
		this.hosts = hosts;
	}
	
	@Override
	public String toString() {
		return String.format(" [ SystemConfigModel : ] configName=%s , hosts=%s",name,hosts);
	}
	
}
