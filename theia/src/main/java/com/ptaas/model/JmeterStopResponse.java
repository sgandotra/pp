package com.ptaas.model;

public class JmeterStopResponse {

    public enum Status {
        SUCCESS,
        FAILURE
    }
    
    private Status response;
    
    private String msg;
    
    public JmeterStopResponse() { }

    public Status getResponse() {
        return response;
    }

    public void setResponse(Status response) {
        this.response = response;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }   
    
}
