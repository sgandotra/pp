package com.ptaas.repository;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name="test_results")
public class TestResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int tid;

	private String commandlineparams;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_created")
	private Date dateCreated;

	private int duration;

	private int testresultid;

	private int vusers;
	
	private String description;
		
	private String status;
	
	private boolean shared;
	
	@Column(name="executionid")
	private int executionId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="executionid", insertable=false, updatable = false)
	@JsonIgnore
	private JmeterConsoleData jmeterConsoleData;

    //bi-directional many-to-one association to Loadgenerator
	@ManyToOne
	@JsonManagedReference
	private LoadGenerator loadgenerator;

	//bi-directional many-to-one association to Stagemachine
	@ManyToOne
	@JsonManagedReference
	private StageMachine stagemachine;
	
	@ManyToOne
	@JsonManagedReference
	@JsonIgnore
	private User user;
	
	@ManyToOne
	@JoinColumn(name="systemconfigid")
	@JsonManagedReference
	private SystemConfiguration systemConfiguration;

	public TestResult() {
	}

	public int getTid() {
		return this.tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getCommandlineparams() {
		return this.commandlineparams;
	}

	public void setCommandlineparams(String commandlineparams) {
		this.commandlineparams = commandlineparams;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public int getDuration() {
		return this.duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getTestresultid() {
		return this.testresultid;
	}

	public void setTestresultid(int testresultid) {
		this.testresultid = testresultid;
	}

	public int getVusers() {
		return this.vusers;
	}

	public void setVusers(int vusers) {
		this.vusers = vusers;
	}

	public LoadGenerator getLoadgenerator() {
		return this.loadgenerator;
	}

	public void setLoadgenerator(LoadGenerator loadgenerator) {
		this.loadgenerator = loadgenerator;
	}

	public StageMachine getStagemachine() {
		return this.stagemachine;
	}

	public void setStagemachine(StageMachine stagemachine) {
		this.stagemachine = stagemachine;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
 
    public JmeterConsoleData getJmeterConsoleData() {
        return jmeterConsoleData;
    }

    public void setJmeterConsoleData(JmeterConsoleData jmeterConsoleData) {
        this.jmeterConsoleData = jmeterConsoleData;
    }

    public int getExecutionId() {
        return executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public SystemConfiguration getSystemConfiguration() {
        return systemConfiguration;
    }

    public void setSystemConfiguration(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    @Override
    public String toString() {
        return "TestResult [tid=" + tid + ", commandlineparams=" + commandlineparams
                + ", dateCreated=" + dateCreated + ", duration=" + duration + ", testresultid="
                + testresultid + ", vusers=" + vusers + ", description=" + description
                + ", status=" + status + ", shared=" + shared + ", executionId=" + executionId
                + ", jmeterConsoleData=" + jmeterConsoleData + ", loadgenerator=" + loadgenerator
                + ", stagemachine=" + stagemachine + ", user=" + user + ", systemConfiguration="
                + systemConfiguration + "]";
    }
}
