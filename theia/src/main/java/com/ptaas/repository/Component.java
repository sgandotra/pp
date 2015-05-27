package com.ptaas.repository;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Set;

/**
 * The persistent class for the components database table.
 * 
 */
@Entity
@Table(name = "components")
@NamedQuery(name = "Component.findAll", query = "SELECT c FROM Component c")
public class Component implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@NotNull
	@Length(min=5)
	@Column(name="name",unique=true,nullable=false)
	private String name;

	
	// bi-directional many-to-many association to Role
	@ManyToMany(mappedBy="components" , fetch=FetchType.LAZY)
	@JsonBackReference
	private Set<Role> roles;

	public Component() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

}