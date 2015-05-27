package com.theia.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptaas.model.JmonResponseActivate;
import com.ptaas.model.MonitoredHostsModel;
import com.ptaas.model.JmonResponseActivate.Services;
import com.ptaas.model.JmonResponseActivate.Services.Invalid;
import com.ptaas.model.JmonResponseActivate.Services.Valid;
import com.ptaas.model.MonitoredHostsModel.Host;
import com.ptaas.model.MonitoredHostsModel.Host.MonitoredService;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CreateHost {
	
	@Test
	public void create() {
		
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint("http://localhost:8080")
	    .setLogLevel(LogLevel.FULL)
	    .build();
		
		MonitoredHostsModel model = new MonitoredHostsModel();
		
		Map<String,Host> hosts = new HashMap<String,Host>();
		Host host = new Host();
		host.setHostName("abc");
		host.setMonitoredServices(new ArrayList<MonitoredService>());
		hosts.put(host.getHostName(),host);
		model.setHosts(hosts);
		
		PostHostDataService service = restAdapter.create(PostHostDataService.class);
		service.post(model, new Callback<Void>() {

			@Override
			public void failure(RetrofitError arg0) {
				System.err.println(arg0);
				
			}

			@Override
			public void success(Void arg0, Response arg1) {
				System.err.println(arg1);
				
			}

		});
		
	}
	
	@Test
	public void createJson() throws JsonProcessingException {
		JmonResponseActivate jmon = new JmonResponseActivate();
		
		Services services = new Services();
		
		List<Valid> valids = new ArrayList<Valid>();
		List<Invalid> invalids = new ArrayList<Invalid>();
		
		services.setValid(valids);
		services.setInvalid(invalids);
		
		jmon.setServices(services);
		jmon.setJmonresponse(new ArrayList<String>());
		
		ObjectMapper mapper = new ObjectMapper();
		
		
		System.err.println(mapper.writeValueAsString(jmon));
	}
	
	
	@Test
	public void testDate() throws ParseException {
		
		String sDate = "2015-01-29T08:00:00.000Z";
		
		SimpleDateFormat df = new  SimpleDateFormat(
			    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
		df.parse(sDate);
		
	}
	
}
