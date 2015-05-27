package com.ptaas.repository;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "system_configuration",
		uniqueConstraints=
				@UniqueConstraint(columnNames = {"name", "user_id"})
		) 
 
public class SystemConfiguration {

	@Id	
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="name" , nullable= false, unique=true)
	private String name;
	
	@Column(name="is_favorite" , nullable = false)
	private boolean isFavorite;
	
	@Column(name="is_running" , nullable = false)
	private boolean isRunning;
	
	@OneToMany(mappedBy="systemConfiguration", cascade=CascadeType.PERSIST)
	@JsonManagedReference
	private Set<Host> hosts;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	@JsonIgnore
	private User user;
	
	@OneToMany(mappedBy="systemConfiguration")
	@JsonIgnore
	@JsonBackReference
	private Set<TestResult> testResults;
	
	public SystemConfiguration() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public Set<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(Set<TestResult> testResults) {
        this.testResults = testResults;
    }

    @Override
	public String toString() {
		return String.format("[SystemConfiguration ] name=%s isFavorite=%b isRunning=%b",  name,isFavorite,isRunning);
	}
	
}
