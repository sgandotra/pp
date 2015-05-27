package com.ptaas.model;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

public class JmeterStatusResponse {

    @NotNull
    private String status;
    @NotNull
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private Date timestamp;
    
    
    @Valid
    private Data data;
    

    public static class Data {
        
        @NotNull
        private String state;
        
        @NotNull
        @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
        private Date timestamp;
        
        private Status status;
        
        public static class Status {
            
            private String threads;
            private String samples;
            private long latency;
            private long rt;
            private long errors;
            
            public Status() {}

            public String getThreads() {
                return threads;
            }

            public void setThreads(String threads) {
                this.threads = threads;
            }

            public String getSamples() {
                return samples;
            }

            public void setSamples(String samples) {
                this.samples = samples;
            }

            public long getLatency() {
                return latency;
            }

            public void setLatency(long latency) {
                this.latency = latency;
            }

            public long getRT() {
                return rt;
            }

            public void setRT(long rT) {
                rt = rT;
            }

            public long getErrors() {
                return errors;
            }

            public void setErrors(long errors) {
                this.errors = errors;
            }           
            
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
        
        
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
    
}
