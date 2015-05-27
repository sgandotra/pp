package com.ptaas.model;

import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RoleModel {

	@NotNull
	private String roleName;
	@NotNull
	@Size(min=1)
	private Set<String> components;
	
	
	public RoleModel() {
		super();
	}


	public String getRoleName() {
		return roleName;
	}


	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}


	public Set<String> getComponents() {
		return components;
	}


	public void setComponents(Set<String> components) {
		this.components = components;
	}
		
}
