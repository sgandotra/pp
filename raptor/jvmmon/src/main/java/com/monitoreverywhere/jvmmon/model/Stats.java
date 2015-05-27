package com.monitoreverywhere.jvmmon.model;

import com.google.common.collect.ImmutableMap;
import com.monitoreverywhere.jvmmon.stats.RunTimeStatCollector.RunTimeStat;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;


public class Stats {
  
    private ImmutableMap<String, Map<String, Double>> classLoaderStats;
    private ImmutableMap<String, Map<String, Double>> garbageCollector;
    private ImmutableMap<String,Map<String,Double>> memoryPoolCollector;
    private ImmutableMap<String,Map<String,Double>> memoryStatCollector;
    private EnumMap<RunTimeStat, String> runTimeCollector;
    
    private long timestamp;
    
    public static class Builder {
        
        private ImmutableMap<String, Map<String, Double>> classLoaderStats;
        private ImmutableMap<String, Map<String, Double>> garbageCollector;
        private ImmutableMap<String,Map<String,Double>> memoryPoolCollector;
        private ImmutableMap<String, Map<String, Double>> memoryStatCollector;
        private EnumMap<RunTimeStat, String> runTimeCollector;
        private long timestamp;
        private String id;
       
        public Builder() {
            this.timestamp = System.currentTimeMillis() / 1000;
        }
        
        public Builder setClassLoaderStats(ImmutableMap<String, Map<String, Double>> immutableMap) {
            this.classLoaderStats = immutableMap;
            return this;
        }
        
        public Builder setGarbageCollector(
                ImmutableMap<String, Map<String, Double>> immutableMap) {
            this.garbageCollector = immutableMap;
            return this;
        }
        
        public Builder setMemoryPoolCollector(
                ImmutableMap<String, Map<String,Double>> memoryPoolCollector) {
            this.memoryPoolCollector = memoryPoolCollector;
            return this;
        }
        
        public Builder setMemoryStatCollector(
        		ImmutableMap<String,Map<String,Double>> memoryStatCollector) {
            this.memoryStatCollector = memoryStatCollector;
            return this;
        }
        
        public Builder setRunTimeCollector(EnumMap<RunTimeStat, String> enumMap) {
            this.runTimeCollector = enumMap;
            return this;
        }
        
        public Stats build() {
            Stats stats = new Stats();
            stats.setClassLoaderStats(classLoaderStats);
            stats.setGarbageCollector(garbageCollector);
            stats.setMemoryPoolCollector(memoryPoolCollector);
            stats.setMemoryStatCollector(memoryStatCollector);
            stats.setRunTimeCollector(runTimeCollector);
            stats.setTimestamp(timestamp);
            return stats;
        }
        
        
    }

    public ImmutableMap<String, Map<String, Double>> getClassLoaderStats() {
        return classLoaderStats;
    }


	public void setClassLoaderStats(ImmutableMap<String, Map<String, Double>> classLoaderStats) {
        this.classLoaderStats = classLoaderStats;
    }

    public ImmutableMap<String, Map<String, Double>> getGarbageCollector() {
        return garbageCollector;
    }

    public void setGarbageCollector(
            ImmutableMap<String, Map<String, Double>> garbageCollector) {
        this.garbageCollector = garbageCollector;
    }

    public ImmutableMap<String, Map<String, Double>> getMemoryPoolCollector() {
        return memoryPoolCollector;
    }

    public void setMemoryPoolCollector(
            ImmutableMap<String, Map<String, Double>> memoryPoolCollector) {
        this.memoryPoolCollector = memoryPoolCollector;
    }

    public ImmutableMap<String, Map<String, Double>> getMemoryStatCollector() {
        return memoryStatCollector;
    }

    public void setMemoryStatCollector(
            ImmutableMap<String, Map<String, Double>> memoryStatCollector) {
        this.memoryStatCollector = memoryStatCollector;
    }

    public EnumMap<RunTimeStat, String> getRunTimeCollector() {
        return runTimeCollector;
    }

    public void setRunTimeCollector(EnumMap<RunTimeStat, String> runTimeCollector) {
        this.runTimeCollector = runTimeCollector;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public long getTimestamp() {
    	return this.timestamp;
    }
    
}
