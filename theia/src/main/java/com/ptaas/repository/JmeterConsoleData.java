package com.ptaas.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "jmeter_console_data")
public class JmeterConsoleData {
    
    @OneToOne(fetch = FetchType.LAZY,mappedBy="jmeterConsoleData")
    private TestResult testResult;
    
    @Id
    private int executionid;
    
    @Column(name = "samples")
    private Integer samples;
    
    @Column(name = "totalthreads")
    private Integer totalthreads;
    
    @Column(name = "runningthreads")
    private Integer runningthreads;
    
    @Column(name = "latency")
    private Integer latency;
    
    @Column(name = "responsetime")
    private Integer responsetime;
    
    @Column(name = "errors")
    private Integer errors;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_modified", nullable = false)
    private Date dateModified;

    public JmeterConsoleData() {
    }

    public JmeterConsoleData(TestResult testResults, Date dateModified) {
        this.testResult = testResults;
        this.dateModified = dateModified;
    }

    public JmeterConsoleData( TestResult testResults, Integer samples,
            Integer totalthreads, Integer runningthreads, Integer latency,
            Integer responsetime, Integer errors, Date dateModified) {
        this.testResult = testResults;
        this.samples = samples;
        this.totalthreads = totalthreads;
        this.runningthreads = runningthreads;
        this.latency = latency;
        this.responsetime = responsetime;
        this.errors = errors;
        this.dateModified = dateModified;
    }

    public Integer getSamples() {
        return this.samples;
    }

    public void setSamples(Integer samples) {
        this.samples = samples;
    }

    public Integer getTotalthreads() {
        return this.totalthreads;
    }

    public void setTotalthreads(Integer totalthreads) {
        this.totalthreads = totalthreads;
    }

    public Integer getRunningthreads() {
        return this.runningthreads;
    }

    public void setRunningthreads(Integer runningthreads) {
        this.runningthreads = runningthreads;
    }

    public Integer getLatency() {
        return this.latency;
    }

    public void setLatency(Integer latency) {
        this.latency = latency;
    }

    public Integer getResponsetime() {
        return this.responsetime;
    }

    public void setResponsetime(Integer responsetime) {
        this.responsetime = responsetime;
    }

    public Integer getErrors() {
        return this.errors;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }


    public Date getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    public int getExecutionid() {
        return executionid;
    }

    public void setExecutionid(int executionid) {
        this.executionid = executionid;
    }

    @Override
    public String toString() {
        return "JmeterConsoleData [executionid=" + executionid
                + ", samples=" + samples + ", totalthreads=" + totalthreads + ", runningthreads="
                + runningthreads + ", latency=" + latency + ", responsetime=" + responsetime
                + ", errors=" + errors + ", dateModified=" + dateModified + "]";
    }
    
}
