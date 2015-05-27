package com.ptaas.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostMonStatus {

	private String hostname;
	private String type;
	private String platform;
	private String arch;
	private String uptime;
	private String[] loadavg;
	private Memory memory;
	private Cpu[] cpus;
	private NetworkInterfaces networkInterfaces;
	private long lastupdated_utc;

	public static class NetworkInterfaces {

		private List<Lo> lo = new ArrayList<Lo>();
		private List<Bond0> bond0 = new ArrayList<Bond0>();
		private List<Bond01> bond01 = new ArrayList<Bond01>();
		private List<Eth0> eth0 = new ArrayList<Eth0>();
		private List<Eth01> eth01 = new ArrayList<Eth01>();
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public class Lo {

			private String address;
			private String family;
			private Boolean internal;
			private Map<String, Object> additionalProperties = new HashMap<String, Object>();

			/**
			 * 
			 * @return The address
			 */
			public String getAddress() {
				return address;
			}

			/**
			 * 
			 * @param address
			 *            The address
			 */
			public void setAddress(String address) {
				this.address = address;
			}

			/**
			 * 
			 * @return The family
			 */
			public String getFamily() {
				return family;
			}

			/**
			 * 
			 * @param family
			 *            The family
			 */
			public void setFamily(String family) {
				this.family = family;
			}

			/**
			 * 
			 * @return The internal
			 */
			public Boolean getInternal() {
				return internal;
			}

			/**
			 * 
			 * @param internal
			 *            The internal
			 */
			public void setInternal(Boolean internal) {
				this.internal = internal;
			}

			public Map<String, Object> getAdditionalProperties() {
				return this.additionalProperties;
			}

			public void setAdditionalProperty(String name, Object value) {
				this.additionalProperties.put(name, value);
			}

		}

		public class Bond01 {

			private String address;
			private String family;
			private Boolean internal;
			private Map<String, Object> additionalProperties = new HashMap<String, Object>();

			/**
			 * 
			 * @return The address
			 */
			public String getAddress() {
				return address;
			}

			/**
			 * 
			 * @param address
			 *            The address
			 */
			public void setAddress(String address) {
				this.address = address;
			}

			/**
			 * 
			 * @return The family
			 */
			public String getFamily() {
				return family;
			}

			/**
			 * 
			 * @param family
			 *            The family
			 */
			public void setFamily(String family) {
				this.family = family;
			}

			/**
			 * 
			 * @return The internal
			 */
			public Boolean getInternal() {
				return internal;
			}

			/**
			 * 
			 * @param internal
			 *            The internal
			 */
			public void setInternal(Boolean internal) {
				this.internal = internal;
			}

			public Map<String, Object> getAdditionalProperties() {
				return this.additionalProperties;
			}

			public void setAdditionalProperty(String name, Object value) {
				this.additionalProperties.put(name, value);
			}

		}

		public static class Bond0 {

			private String address;
			private String family;
			private Boolean internal;
			private Map<String, Object> additionalProperties = new HashMap<String, Object>();

			/**
			 * 
			 * @return The address
			 */
			public String getAddress() {
				return address;
			}

			/**
			 * 
			 * @param address
			 *            The address
			 */
			public void setAddress(String address) {
				this.address = address;
			}

			/**
			 * 
			 * @return The family
			 */
			public String getFamily() {
				return family;
			}

			/**
			 * 
			 * @param family
			 *            The family
			 */
			public void setFamily(String family) {
				this.family = family;
			}

			/**
			 * 
			 * @return The internal
			 */
			public Boolean getInternal() {
				return internal;
			}

			/**
			 * 
			 * @param internal
			 *            The internal
			 */
			public void setInternal(Boolean internal) {
				this.internal = internal;
			}

			public Map<String, Object> getAdditionalProperties() {
				return this.additionalProperties;
			}

			public void setAdditionalProperty(String name, Object value) {
				this.additionalProperties.put(name, value);
			}

		}
		
		public class Eth0 {

			private String address;
			private String family;
			private Boolean internal;
			private Map<String, Object> additionalProperties = new HashMap<String, Object>();

			/**
			* 
			* @return
			* The address
			*/
			public String getAddress() {
			return address;
			}

			/**
			* 
			* @param address
			* The address
			*/
			public void setAddress(String address) {
			this.address = address;
			}

			/**
			* 
			* @return
			* The family
			*/
			public String getFamily() {
			return family;
			}

			/**
			* 
			* @param family
			* The family
			*/
			public void setFamily(String family) {
			this.family = family;
			}

			/**
			* 
			* @return
			* The internal
			*/
			public Boolean getInternal() {
			return internal;
			}

			/**
			* 
			* @param internal
			* The internal
			*/
			public void setInternal(Boolean internal) {
			this.internal = internal;
			}

			public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
			}

			public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
			}

			}
		
		public class Eth01 {

			private String address;
			private String family;
			private Boolean internal;
			private Map<String, Object> additionalProperties = new HashMap<String, Object>();

			/**
			* 
			* @return
			* The address
			*/
			public String getAddress() {
			return address;
			}

			/**
			* 
			* @param address
			* The address
			*/
			public void setAddress(String address) {
			this.address = address;
			}

			/**
			* 
			* @return
			* The family
			*/
			public String getFamily() {
			return family;
			}

			/**
			* 
			* @param family
			* The family
			*/
			public void setFamily(String family) {
			this.family = family;
			}

			/**
			* 
			* @return
			* The internal
			*/
			public Boolean getInternal() {
			return internal;
			}

			/**
			* 
			* @param internal
			* The internal
			*/
			public void setInternal(Boolean internal) {
			this.internal = internal;
			}

			public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
			}

			public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
			}

			}

		/**
		 * 
		 * @return The lo
		 */
		public List<Lo> getLo() {
			return lo;
		}

		/**
		 * 
		 * @param lo
		 *            The lo
		 */
		public void setLo(List<Lo> lo) {
			this.lo = lo;
		}

		/**
		 * 
		 * @return The bond0
		 */
		public List<Bond0> getBond0() {
			return bond0;
		}

		/**
		 * 
		 * @param bond0
		 *            The bond0
		 */
		public void setBond0(List<Bond0> bond0) {
			this.bond0 = bond0;
		}

		/**
		 * 
		 * @return The bond01
		 */
		public List<Bond01> getBond01() {
			return bond01;
		}

		/**
		 * 
		 * @param bond01
		 *            The bond0:1
		 */
		public void setBond01(List<Bond01> bond01) {
			this.bond01 = bond01;
		}

		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

		public List<Eth01> getEth01() {
			return eth01;
		}

		public void setEth01(List<Eth01> eth01) {
			this.eth01 = eth01;
		}

		public List<Eth0> getEth0() {
			return eth0;
		}

		public void setEth0(List<Eth0> eth0) {
			this.eth0 = eth0;
		}

	}

	public static class Memory {

		private String totalmem;
		private String freemem;

		public String getTotalmem() {
			return totalmem;
		}

		public void setTotalmem(String totalmem) {
			this.totalmem = totalmem;
		}

		public String getFreemem() {
			return freemem;
		}

		public void setFreemem(String freemem) {
			this.freemem = freemem;
		}
	}

	public static class Cpu {
		private String model;
		private String speed;
		private Times times;

		public static class Times {
			private String user;
			private String nice;
			private String sys;
			private String idle;
			private String irq;

			public String getUser() {
				return user;
			}

			public void setUser(String user) {
				this.user = user;
			}

			public String getNice() {
				return nice;
			}

			public void setNice(String nice) {
				this.nice = nice;
			}

			public String getSys() {
				return sys;
			}

			public void setSys(String sys) {
				this.sys = sys;
			}

			public String getIdle() {
				return idle;
			}

			public void setIdle(String idle) {
				this.idle = idle;
			}

			public String getIrq() {
				return irq;
			}

			public void setIrq(String irq) {
				this.irq = irq;
			}

		}

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public String getSpeed() {
			return speed;
		}

		public void setSpeed(String speed) {
			this.speed = speed;
		}

		public Times getTimes() {
			return times;
		}

		public void setTimes(Times times) {
			this.times = times;
		}

	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public String[] getLoadavg() {
		return loadavg;
	}

	public void setLoadavg(String[] loadavg) {
		this.loadavg = loadavg;
	}

	public Cpu[] getCpus() {
		return cpus;
	}

	public void setCpus(Cpu[] cpus) {
		this.cpus = cpus;
	}

	public NetworkInterfaces getNetworkInterfaces() {
		return networkInterfaces;
	}

	public void setNetworkInterfaces(NetworkInterfaces networkInterfaces) {
		this.networkInterfaces = networkInterfaces;
	}

	public long getLastupdated_utc() {
		return lastupdated_utc;
	}

	public void setLastupdated_utc(long lastupdated_utc) {
		this.lastupdated_utc = lastupdated_utc;
	}

	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

}
