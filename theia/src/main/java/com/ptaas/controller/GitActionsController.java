package com.ptaas.controller;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ptaas.exception.GitCloneException;
import com.ptaas.model.GitListResponse;
import com.ptaas.service.GitActionsService;
import com.ptaas.service.exception.LoadGeneratorUnavailableException;

@Controller
@RequestMapping("/git")
public class GitActionsController {

    private static final Logger logger = LoggerFactory.getLogger(GitActionsController.class);
    
    @Autowired      
    private GitActionsService gitActionsService;
    
    
    @RequestMapping(value="/clone/{hostName:.+}")
    @ResponseBody
    public ResponseEntity<Void> clone(@NotNull @PathVariable String hostName) throws LoadGeneratorUnavailableException, GitCloneException {
        
        logger.info("Received request to clone on hostName : {}",hostName);
        gitActionsService.gitClone(hostName);
        
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
    
    @RequestMapping(value="/{hostName:.+}/list",produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GitListResponse> list(@NotNull @PathVariable String hostName) throws LoadGeneratorUnavailableException {
        
        logger.info("Received a request to list git repo on hostname : {}",hostName);
        GitListResponse response = gitActionsService.list(hostName);
        
        return new ResponseEntity<GitListResponse>(response,HttpStatus.OK);
        
    }
    
}
