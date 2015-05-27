package com.ptaas.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptaas.model.HostActivationResponse;
import com.ptaas.model.JmonResponseActivate;
import com.ptaas.model.JmonResponseDeActivate;
import com.ptaas.model.MonitoredHostsModel;
import com.ptaas.model.NodemonResponseActivate;
import com.ptaas.model.MonitoredHostsModel.Host;
import com.ptaas.model.MonitoredHostsModel.Host.MonitoredService;
import com.ptaas.model.MonitoredHostsModel.Host.MonitoredService.Type;
import com.ptaas.repository.SystemConfiguration;
import com.ptaas.service.exception.SystemConfigNotFoundException;

public abstract class MonitoringSvc {
	
	public static final ExecutorService executors = Executors.newFixedThreadPool(10);
	
	@Value("${hostmon.port}")
	public String HOSTMON_PORT;
	    
	@Value("${hostmon.timeout}")
	public long HOSTMON_TIMEOUT;
	
	public enum Action {
	    ACTIVATE,
	    DEACTIVATE
	}

	public HostMonService createClient(String hostName) {
		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint("http://"+ hostName + ":" +HOSTMON_PORT)
		.setLogLevel(LogLevel.FULL)
		.setClient(new MyUrlConnectionClient())
		.build();
	
		return restAdapter.create(HostMonService.class);
	}    

	public static class MyUrlConnectionClient extends UrlConnectionClient {
		  @Override protected HttpURLConnection openConnection(Request request) throws IOException {
		    HttpURLConnection connection = super.openConnection(request);
		    connection.setConnectTimeout(15 * 1000);
		    connection.setReadTimeout(300 * 1000);
		    return connection;
		  }
	}
	
}
