package com.ptaas.model;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitListResponse {

    public enum Status {
      SUCCESS,
      FAILURE
    }
    
    private String error;
    
    @NotNull
    private Status status;
    
    @NotNull
    @Valid
    private List<Response> response;
    
    public static class Response {
        
        private String dir;
        
        private Jmx jmx;
        
        public static class Jmx {
            
            private String path;
            
            @Valid
            private Config config;
            
            public static class Config {
                
                private String author;
                
                @JsonProperty("ptass-ready")
                private boolean ptassReady;
                
                private String notes;
                
                private String description;
                
                private int vusers;
                
                private long duration;
                
                private long warmup;
                
                private String[] dependencies;
                
                @JsonProperty("confluence_url")
                private String confluenceUrl;
                
                @JsonProperty("self_github")
                private String selfGithub;
                
                @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
                @JsonProperty("date_creation")
                private Date dateCreation;
                
                @JsonProperty("jmeter_version")
                private String jmeterVersion;
                
                @JsonProperty("jmeter_dependencies")
                private String[] jmeterDependencies;
                
                public Config() { }

                public String getAuthor() {
                    return author;
                }

                public void setAuthor(String author) {
                    this.author = author;
                }

                public boolean isPtassReady() {
                    return ptassReady;
                }

                public void setPtassReady(boolean ptassReady) {
                    this.ptassReady = ptassReady;
                }

                public String getNotes() {
                    return notes;
                }

                public void setNotes(String notes) {
                    this.notes = notes;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                public int getVusers() {
                    return vusers;
                }

                public void setVusers(int vusers) {
                    this.vusers = vusers;
                }

                public long getDuration() {
                    return duration;
                }

                public void setDuration(long duration) {
                    this.duration = duration;
                }

                public long getWarmup() {
                    return warmup;
                }

                public void setWarmup(long warmup) {
                    this.warmup = warmup;
                }

                public String[] getDependencies() {
                    return dependencies;
                }

                public void setDependencies(String[] dependencies) {
                    this.dependencies = dependencies;
                }

                public String getConfluenceUrl() {
                    return confluenceUrl;
                }

                public void setConfluenceUrl(String confluenceUrl) {
                    this.confluenceUrl = confluenceUrl;
                }

                public String getSelfGithub() {
                    return selfGithub;
                }

                public void setSelfGithub(String selfGithub) {
                    this.selfGithub = selfGithub;
                }

                public Date getDateCreation() {
                    return dateCreation;
                }

                public void setDateCreation(Date dateCreation) {
                    this.dateCreation = dateCreation;
                }

                public String getJmeterVersion() {
                    return jmeterVersion;
                }

                public void setJmeterVersion(String jmeterVersion) {
                    this.jmeterVersion = jmeterVersion;
                }

                public String[] getJmeterDependencies() {
                    return jmeterDependencies;
                }

                public void setJmeterDependencies(String[] jmeterDependencies) {
                    this.jmeterDependencies = jmeterDependencies;
                }
                
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public Config getConfig() {
                return config;
            }

            public void setConfig(Config config) {
                this.config = config;
            }
                        
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public Jmx getJmx() {
            return jmx;
        }

        public void setJmx(Jmx jmx) {
            this.jmx = jmx;
        }               
        
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }
        
}
