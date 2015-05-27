package com.ptaas.repository;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Entity implementation class for Entity: Hosts
 *
 */
@Entity
@Table(name="hosts")
public class Host implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name="hostname",nullable=false,unique=true)
	private String hostName;

	@Column(name="is_active",nullable=false)
	private boolean isActive;
	
	@Column(name="platform")
	private String platform;
	
	@Column(name="up_time")
	private String uptime;
	
	@Column(name="load_avg")
	private String loadavg;
	
	@Column(name="total_mem")
	private long totalMem;
	
	@Column(name="free_mem")
	private long freeMem;
	
	@Column(name="cpu_model")
	private String cpuModel;
	
	private int cpus;
	
	@Column(name="ethaddress_1")
	private String ethAddress1;
	
	@Column(name="ethaddress_2")
	private String ethAddress2;
	
	@Column(name="nmon_start")
	private Long nmonstart;
	
	@Column(name="nmon_end")
	private Long nmonend;
	
	@Column(name="is_nmon")
	private boolean isNmon;
	
	@Column(name="last_updated")
	private long lastUpdated;
	
	

	@ManyToOne
	@JoinColumn(name="owner_id",nullable=false)
	@JsonBackReference
	private SystemConfiguration systemConfiguration;
	
	private String componentstatus;
	
	//bi-directional many-to-many association to Component
	@ManyToMany
	@JoinTable(name = "hosts_has_roles", 
	joinColumns = { @JoinColumn(name = "hosts_id", referencedColumnName="id") }, 
	inverseJoinColumns = { @JoinColumn(name = "roles_id", referencedColumnName="id") })
	@NotNull
    @JsonManagedReference
	private Set<Role> roles;
	
	public Host() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public String getLoadavg() {
		return loadavg;
	}

	public void setLoadavg(String loadavg) {
		this.loadavg = loadavg;
	}

	public long getTotalMem() {
		return totalMem;
	}

	public void setTotalMem(long totalMem) {
		this.totalMem = totalMem;
	}

	public long getFreeMem() {
		return freeMem;
	}

	public void setFreeMem(long freeMem) {
		this.freeMem = freeMem;
	}

	public String getCpuModel() {
		return cpuModel;
	}

	public void setCpuModel(String cpuModel) {
		this.cpuModel = cpuModel;
	}

	public int getCpus() {
		return cpus;
	}

	public void setCpus(int cpus) {
		this.cpus = cpus;
	}

	public String getEthAddress1() {
		return ethAddress1;
	}

	public void setEthAddress1(String ethAddress1) {
		this.ethAddress1 = ethAddress1;
	}

	public String getEthAddress2() {
		return ethAddress2;
	}

	public void setEthAddress2(String ethAddress2) {
		this.ethAddress2 = ethAddress2;
	}
	
	public boolean isNmon() {
        return isNmon;
    }

    public void setNmon(boolean isNmon) {
        this.isNmon = isNmon;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getNmonstart() {
		return nmonstart;
	}

	public void setNmonstart(Long nmonstart) {
		this.nmonstart = nmonstart;
	}

	public long getNmonend() {
		return nmonend;
	}

	public void setNmonend(Long nmonend) {
		this.nmonend = nmonend;
	}
	
	public long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	
	
	public SystemConfiguration getSystemConfiguration() {
		return systemConfiguration;
	}

	public void setSystemConfiguration(SystemConfiguration systemConfiguration) {
		this.systemConfiguration = systemConfiguration;
	}

	@Override
	public String toString() {
		return String.format(" [ Host id=%d ] hostname=%s, platform =%s, uptime=%s "
				+ " loadavg=%s, totalMem=%s, freeMem=%s, cpuModel=%s, cpus=%d, "
				+ " ethaddress1 = %s, ethaddress=%s lastupdated=%d",
				id,hostName,platform,uptime,loadavg,totalMem,freeMem,cpuModel,
				cpus,ethAddress1,ethAddress2,lastUpdated);
		
	}

	public String getComponentstatus() {
		return componentstatus;
	}

	public void setComponentstatus(String componentstatus) {
		this.componentstatus = componentstatus;
	}
	
   
}
