package com.ptaas.model;

import java.util.Date;

public class JmeterResponseAction {

    private String status;
    private String responseMsg;
    private String executionID;
    private long dateStarted;
    
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getResponseMsg() {
        return responseMsg;
    }
    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
    public String getExecutionID() {
        return executionID;
    }
    public void setExecutionID(String executionID) {
        this.executionID = executionID;
    }
    
    public long getDateStarted() {
        return dateStarted;
    }
    public void setDateStarted(long dateStarted) {
        this.dateStarted = dateStarted;
    }
    
    
    @Override
    public String toString() {
        return String.format("[JmeterResponseActivate] status=%s , responseMsg=%s , executionID = %s dateStarted=%d",status,responseMsg,executionID,dateStarted);
    }
    
}
