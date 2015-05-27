package com.ptaas.service;

import javax.validation.constraints.NotNull;

public class GitCloneResponse {

    @NotNull
    private String response;
    
    public GitCloneResponse() { }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    
    
    
}
