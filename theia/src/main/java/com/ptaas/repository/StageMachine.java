package com.ptaas.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="stage_machine")
public class StageMachine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String cpus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_created")
	private Date dateCreated;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_modified")
	private Date dateModified;

	private String description;

	private boolean inuse;

	private String memory;

	private String stagename;

	private boolean status;

	//bi-directional many-to-one association to TestResult
	@OneToMany(mappedBy="stagemachine")
	@JsonBackReference
	private List<TestResult> testResults;

	public StageMachine() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCpus() {
		return this.cpus;
	}

	public void setCpus(String cpus) {
		this.cpus = cpus;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		return this.dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getInuse() {
		return this.inuse;
	}

	public void setInuse(boolean inuse) {
		this.inuse = inuse;
	}

	public String getMemory() {
		return this.memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getStagename() {
		return this.stagename;
	}

	public void setStagename(String stagename) {
		this.stagename = stagename;
	}

	public boolean getStatus() {
		return this.status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<TestResult> getTestResults() {
		return this.testResults;
	}

	public void setTestResults(List<TestResult> testResults) {
		this.testResults = testResults;
	}

	public TestResult addTestResult(TestResult TestResult) {
		getTestResults().add(TestResult);
		TestResult.setStagemachine(this);

		return TestResult;
	}

	public TestResult removeTestResult(TestResult TestResult) {
		getTestResults().remove(TestResult);
		TestResult.setStagemachine(null);

		return TestResult;
	}

}
