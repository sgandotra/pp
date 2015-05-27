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
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="load_generator")
public class LoadGenerator implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private int cpus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_created")
	private Date dateCreated;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_modified")
	private Date dateModified;

	private boolean inuse;

	private String lgname;

	private String memory;

	private boolean status;

	//bi-directional many-to-one association to TestResult
	@OneToMany(mappedBy="loadgenerator")
	@JsonIgnore
	@JsonBackReference
	private List<TestResult> testResults;

	public LoadGenerator() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCpus() {
		return this.cpus;
	}

	public void setCpus(int cpus) {
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

	public boolean getInuse() {
		return this.inuse;
	}

	public void setInuse(boolean inuse) {
		this.inuse = inuse;
	}

	public String getLgname() {
		return this.lgname;
	}

	public void setLgname(String lgname) {
		this.lgname = lgname;
	}

	public String getMemory() {
		return this.memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
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

	public void setTestResults(List<TestResult> TestResults) {
		this.testResults = TestResults;
	}

	public TestResult addTestResult(TestResult testResult) {
		getTestResults().add(testResult);
		testResult.setLoadgenerator(this);

		return testResult;
	}

	public TestResult removeTestResult(TestResult testResult) {
		getTestResults().remove(testResult);
		testResult.setLoadgenerator(null);

		return testResult;
	}

}
