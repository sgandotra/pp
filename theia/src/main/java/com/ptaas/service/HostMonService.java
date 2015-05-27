package com.ptaas.service;

import java.util.Collection;
import java.util.Set;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import com.ptaas.model.GitListResponse;
import com.ptaas.model.HostMonStatus;
import com.ptaas.model.JmeterRequestAction;
import com.ptaas.model.JmeterResponseAction;
import com.ptaas.model.JmeterStatusResponse;
import com.ptaas.model.JmeterStopResponse;
import com.ptaas.model.JmonRequestActivate;
import com.ptaas.model.JmonResponseActivate;
import com.ptaas.model.JmonResponseDeActivate;
import com.ptaas.model.NmonRequestActivate;
import com.ptaas.model.NmonResponseActivate;
import com.ptaas.model.NodemonRequestActivate;
import com.ptaas.model.NodemonResponseActivate;
import com.ptaas.model.MonitoredHostsModel.Host.MonitoredService;

public interface HostMonService {

	@GET("/system/details")
	HostMonStatus getStatus();
	
	@GET("/services/list")
	Set<String> getServicesList();
	
	@POST("/services/listtype")
	Collection<MonitoredService> getServicesStatus(@Body Collection<MonitoredService> monitoredServices);
	
	@POST("/nmon/activate")
	NmonResponseActivate activateNmon(@Body NmonRequestActivate nmon);
	
	@POST("/jmon/activate")
	JmonResponseActivate activateJmon(@Body JmonRequestActivate jmon);
	
	@POST("/nodemon/activate")
	NodemonResponseActivate activateNodemon(@Body NodemonRequestActivate nodemon);
	
	@GET("/nmon/deactivate")
	NmonResponseActivate deactivateNmon();
	
	@GET("/jmon/deactivate")
	JmonResponseDeActivate deactivateJMon();
	
	@POST("/nodemon/deactivate")
	NodemonResponseActivate deactivateNodemon(@Body NodemonRequestActivate nodemon);
	
	@POST("/jmeter/configure/start")
	JmeterResponseAction activateJmeter(@Body JmeterRequestAction jmeterActivateRequest);
	
	@GET("/jmeter/data/{executionID}")
	JmeterStatusResponse getStatus(@Path("executionID") String executionID);
	
	@DELETE("/jmeter/configure/{executionID}")
	JmeterStopResponse stopJmeter(@Path("executionID") int executionID);
	
	@GET("/git/clone?cloneurl=https://f8d8dc2f42e1a5def189bba455b76a27825d6e7d@github.paypal.com/performance/lnptestscripts.git")
	GitCloneResponse gitClone();
	
	@GET("/git/list")
	GitListResponse gitList();
	
}
