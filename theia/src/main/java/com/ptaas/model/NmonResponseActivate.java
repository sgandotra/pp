package com.ptaas.model;

import java.util.Date;


public class NmonResponseActivate {

	private String status;
	
	private String timestamp;
	
	private NmonStatus nmonStatus;
	
	public NmonResponseActivate() {
		
	}
	
	public static class NmonStatus {
		private String status;
		private String type;
		private long started;
		private long ended;
		
		public NmonStatus() {
			
		}
		
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public long getStarted() {
			return started;
		}
		public void setStarted(long started) {
			this.started = started;
		}
		public long getEnded() {
			return ended;
		}
		public void setEnded(long ended) {
			this.ended = ended;
		}
		
		
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public NmonStatus getNmonStatus() {
		return nmonStatus;
	}

	public void setNmonStatus(NmonStatus nmonStatus) {
		this.nmonStatus = nmonStatus;
	}
	
	
}
