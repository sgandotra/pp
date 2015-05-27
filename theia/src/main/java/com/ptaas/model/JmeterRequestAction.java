package com.ptaas.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class JmeterRequestAction {

    @NotNull
    private String script;
    
    @Valid
    private Config config;

    public static class Config {
        
        @NotNull
        private String vusers;
        @NotNull
        private String duration;
        @NotNull
        private String stageName;
        @NotNull
        private String params;
        
        public String getVusers() {
            return vusers;
        }
        public void setVusers(String vusers) {
            this.vusers = vusers;
        }
        public String getDuration() {
            return duration;
        }
        public void setDuration(String duration) {
            this.duration = duration;
        }
        public String getStageName() {
            return stageName;
        }
        public void setStageName(String stageName) {
            this.stageName = stageName;
        }
        public String getParams() {
            return params;
        }
        public void setParams(String params) {
            this.params = params;
        }
        
        @Override
        public String toString() {
            return String.format("[Config ] vuser=%s\n duration=%s\n stageName=%s\n  params=%s\n",vusers,duration,stageName,params);
        }
        
        
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
    
    @Override
    public String toString() {
        return String.format("JmeterRequestAction script =%s , config = %s",script,config);
    }
}
