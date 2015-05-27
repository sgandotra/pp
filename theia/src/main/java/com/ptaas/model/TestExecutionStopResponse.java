package com.ptaas.model;

import java.util.Date;

import com.ptaas.model.TestExecutionStartResponse.Status;

public class TestExecutionStopResponse {

    private int testId;
    private Date timestamp;
    private Response response;
    
    public TestExecutionStopResponse() {
        response = new Response();
    }
    
    public static class Response {
        private Status status;
        private String message;
        
        public Response() {}

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

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
    
}
