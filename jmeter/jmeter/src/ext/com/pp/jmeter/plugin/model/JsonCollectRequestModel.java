package com.pp.jmeter.plugin.model;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.gson.annotations.SerializedName;

public class JsonCollectRequestModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public JsonCollectRequestModel() {}

	@Valid
	@NotNull(message = "Data Collect Request Payload cannot be empty")
	@SerializedName("DataCollectorCollectRequest")
	private DataCollectorCollectRequest dataCollectorCollectRequest;

	public static class DataCollectorCollectRequest implements Serializable {
		private static final long serialVersionUID = 83454353453409L;

		@NotNull(message = "token cannot be null")
		private String token;

		private String note;

		@NotNull(message = "timestamp cannot be null")
		private String timestamp;

		@NotNull(message = "Loc cannot be null")
		private String loc;

		@NotNull(message = "monitor name cannot be null")
		private String machinename;

		@Valid
		private MetricPayload metricPayload;
		
		public static class MetricPayload {
			@Valid
			@NotNull(message = "metrics cannot be null")
			private List<Metric> metrics;

			public List<Metric> getMetrics() {
				return metrics;
			}

			public void setMetrics(List<Metric> metrics) {
				this.metrics = metrics;
			}

			public static class Metric implements Serializable {

				private static final long serialVersionUID = 1L;

				@NotNull(message = "ID cannot be null")
				private int id;

				@NotNull(message = "metric name cannot be  null")
				private String name;

				@NotNull(message = "metric type cannot be null")
				private String type;

				@NotNull(message = "value cannot be null")
				private String value;

				public int getId() {
					return id;
				}

				public void setId(int id) {
					this.id = id;
				}

				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}

				public String getType() {
					return type;
				}

				public void setType(String type) {
					this.type = type;
				}

				public String getValue() {
					return value;
				}

				public void setValue(String value) {
					this.value = value;
				}

				@Override
				public String toString() {
					return ReflectionToStringBuilder.toString(this,
							ToStringStyle.MULTI_LINE_STYLE);
				}
			}

			@Override
			public String toString() {
				return ReflectionToStringBuilder.toString(this,
						ToStringStyle.MULTI_LINE_STYLE);

			}
		}

		public void setMetricPaylaod(MetricPayload metricPayload) {
			this.metricPayload = metricPayload;
		}
		
		public MetricPayload getMetricPayload() {
			return this.metricPayload;
		}
		
		public String getMachinename() {
			return machinename;
		}

		public void setMachinename(String machinename) {
			this.machinename = machinename;
		}

		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}

		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		public String getLoc() {
			return loc;
		}

		public void setLoc(String loc) {
			this.loc = loc;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this,
					ToStringStyle.MULTI_LINE_STYLE);
		}
	}

	public void setDataCollectorCollectRequest(DataCollectorCollectRequest dataCollectorCollectRequest) {
		this.dataCollectorCollectRequest = dataCollectorCollectRequest;
	}

	public DataCollectorCollectRequest getDataCollectorCollectRequest() {
		return dataCollectorCollectRequest;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}
}
