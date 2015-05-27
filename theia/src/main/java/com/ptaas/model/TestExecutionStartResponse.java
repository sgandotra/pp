package com.ptaas.model;

import java.io.Serializable;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.ptaas.model.TestExecutionStartRequest.Jmeter.Config;

public class TestExecutionStartResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    public enum Status {
        SUCCESS,
        FAILURE, 
        STOPPED
    }
    
    @NotNull
    private int testId;
    
    @NotNull
    private String sut;
    @NotNull
    private String loadgenerator;
       
    @Valid
    private Config config;
    
    @Valid 
    private Response response;
    
    public static class Response implements Serializable {
        
        private static final long serialVersionUID = 1L;

        @NotNull
        @DateTimeFormat(iso=ISO.DATE)
        private Date timestamp;
        
        @NotNull
        private Status status;
        
        @NotNull
        private String message;
                
        public Response() {
            timestamp = new Date();
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    
    public TestExecutionStartResponse() {
        response = new Response();
    }


    public String getSut() {
        return sut;
    }


    public void setSut(String sut) {
        this.sut = sut;
    }


    public String getLoadgenerator() {
        return loadgenerator;
    }


    public void setLoadgenerator(String loadgenerator) {
        this.loadgenerator = loadgenerator;
    }


    public Config getConfig() {
        return config;
    }


    public void setConfig(Config config) {
        this.config = config;
    }


    public Response getResponse() {
        return response;
    }


    public void setResponse(Response response) {
        this.response = response;
    }


    public int getTestId() {
        return testId;
    }


    public void setTestId(int testId) {
        this.testId = testId;
    }
    
}
