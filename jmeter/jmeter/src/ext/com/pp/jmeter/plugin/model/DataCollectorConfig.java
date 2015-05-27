package com.pp.jmeter.plugin.model;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

public class DataCollectorConfig {
	
	@NotEmpty(message="internal error, require id for internal purposes")
	@Min(value=1, message="id cannot be less than 1")
	@Max(value=3, message="You cannot combine more than 3")
	private String id;
	
	@NotEmpty(message="Location cannot be empty")
	private String location;
	
	@NotEmpty(message="Token cannot be empty")
	private String token;
	
	@NotEmpty(message="monitor cannot be empty")
	private String monitorName;
	
	@Valid
	@Size(min=1,message="metrics cannot be zero size")
	private List<MetricConfig> metricConfigs;

	public static class MetricConfig {

		@NotEmpty(message="id cannot be empty")
		@Min(value=1, message="id cannot be less than 1")
		private String id;
		
		private boolean valid;
		
		@NotEmpty(message="given name cannot be empty")
		private String givenName;
		
		@NotEmpty(message="labelNameWithMetric cannot be empty")
		private String labelNameWithMetric;
		
		@NotEmpty(message="label name cannot be empty")
		private String labelName;
		
		@NotEmpty(message="jmeter metric cannot be empty")
		private String jmeterMetricType;

		public String getJmeterMetricType() {
			return jmeterMetricType;
		}

		public void setJmeterMetricType(String jmeterMetricType) {
			this.jmeterMetricType = jmeterMetricType;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getGivenName() {
			return givenName;
		}

		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}

		public String getLabelNameWithMetric() {
			return labelNameWithMetric;
		}

		public void setLabelNameWithMetric(String labelNameWithMetric) {
			this.labelNameWithMetric = labelNameWithMetric;
		}
		
		public String getLabelName() {
			return labelName;
		}

		public void setLabelName(String labelName) {
			this.labelName = labelName;
		}
		
		public boolean isValid() {
			return valid;
		}

		public void setValid(boolean valid) {
			this.valid = valid;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((givenName == null) ? 0 : givenName.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime
					* result
					+ ((labelName == null) ? 0 : labelName.hashCode());
			result = prime
					* result
					+ ((jmeterMetricType == null) ? 0 : jmeterMetricType
							.hashCode());
			result = prime * result
					+ ((labelNameWithMetric == null) ? 0 : labelNameWithMetric.hashCode());
			result = prime * result + (valid ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MetricConfig other = (MetricConfig) obj;
			if (givenName == null) {
				if (other.givenName != null)
					return false;
			} else if (!givenName.equals(other.givenName))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (labelName == null) {
				if (other.labelName != null)
					return false;
			} else if (!labelName.equals(other.labelName))
				return false;
			if (jmeterMetricType == null) {
				if (other.jmeterMetricType != null)
					return false;
			} else if (!jmeterMetricType.equals(other.jmeterMetricType))
				return false;
			if (labelNameWithMetric == null) {
				if (other.labelNameWithMetric != null)
					return false;
			} else if (!labelNameWithMetric.equals(other.labelNameWithMetric))
				return false;
			if (valid != other.valid)
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this,ToStringStyle.MULTI_LINE_STYLE);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMonitorName() {
		return monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public List<MetricConfig> getMetricConfigs() {
		return metricConfigs;
	}

	public void setMetricConfigs(List<MetricConfig> metricConfigs) {
		this.metricConfigs = metricConfigs;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,ToStringStyle.MULTI_LINE_STYLE);
	}
	
	
}
