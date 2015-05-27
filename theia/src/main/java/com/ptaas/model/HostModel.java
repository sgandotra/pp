package com.ptaas.model;

import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class HostModel {

	@NotNull
	private String name;
	
	@NotNull
	@Size(min=1)
	private Set<String> roles;
	
	public HostModel() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	
	
}


