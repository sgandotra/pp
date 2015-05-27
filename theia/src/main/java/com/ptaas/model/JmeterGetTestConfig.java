package com.ptaas.model;

public class JmeterGetTestConfig {
    
    private MonitoredHostsModel monitoredHostsModel;
    
    private JmeterConfig jmeterConfig;
    
    public static class JmeterConfig {
        private Integer vusers;
        private long startTime;
        private long duration;
        
        public JmeterConfig() {}

        public Integer getVusers() {
            return vusers;
        }

        public void setVusers(Integer vusers) {
            this.vusers = vusers;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
        
    }

    public MonitoredHostsModel getMonitoredHostsModel() {
        return monitoredHostsModel;
    }

    public void setMonitoredHostsModel(MonitoredHostsModel monitoredHostsModel) {
        this.monitoredHostsModel = monitoredHostsModel;
    }

    public JmeterConfig getJmeterConfig() {
        return jmeterConfig;
    }

    public void setJmeterConfig(JmeterConfig jmeterConfig) {
        this.jmeterConfig = jmeterConfig;
    }
    
}
