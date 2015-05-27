package com.ptaas.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="users")
public class User  implements Serializable {


	private static final long serialVersionUID = 6197189140226367399L;

	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id
	private int id;
	
	@Column(name="user_name",nullable=false,unique=true)
	private String userName;

	@OneToMany(mappedBy="user", fetch = FetchType.LAZY)
	private Set<SystemConfiguration> systemConfigurations;
	
	@OneToMany(mappedBy="user",fetch = FetchType.LAZY)
	private Set<Role> roles;
	
	@OneToMany(mappedBy="user",fetch = FetchType.LAZY)
    @JsonBackReference
    private List<TestResult> testResults;

	@Column(name="last_updated",nullable = false)
	private long lastUpdated;
	
	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Set<SystemConfiguration> getSystemConfigurations() {
		return systemConfigurations;
	}

	public void setSystemConfigurations(Set<SystemConfiguration> systemConfigurations) {
		this.systemConfigurations = systemConfigurations;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public List<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
    }

    @Override
	public String toString() {
		return String.format(" [ User : ] userName=%s",userName);
	}
	

}
