package com.ptaas.repository;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Set;


/**
 * The persistent class for the Roles database table.
 * 
 */
@Entity
@Table(name="roles" , 
		uniqueConstraints=
			@UniqueConstraint(columnNames = {"name", "user_id"})
		)
@NamedQuery(name="Role.findAll", query="SELECT r FROM Role r")
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="name",unique=true,nullable=false)
	private String name;

	//bi-directional many-to-many association to Component
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name = "roles_has_components", 
	joinColumns = { @JoinColumn(name = "roles_id", referencedColumnName="id") }, 
	inverseJoinColumns = { @JoinColumn(name = "components_id", referencedColumnName="id") })
	@NotNull
    @JsonManagedReference
	private Set<Component> components;

	// bi-directional many-to-many association to hosts
	@JsonBackReference
	@ManyToMany(mappedBy="roles")
	private Set<Host> hosts;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	public Role() {
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

	public Set<Component> getComponents() {
		return this.components;
	}

	public void setComponents(Set<Component> components) {
		this.components = components;
	}

	public Set<Host> getHosts() {
		return hosts;
	}

	public void setHosts(Set<Host> hosts) {
		this.hosts = hosts;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	

}