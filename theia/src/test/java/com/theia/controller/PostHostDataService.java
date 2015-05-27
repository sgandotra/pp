package com.theia.controller;

import com.ptaas.model.HostModel;
import com.ptaas.model.MonitoredHostsModel;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface PostHostDataService {

	@POST("/monitoring")
	void post(@Body MonitoredHostsModel hostModel,Callback<Void> cb);
}
