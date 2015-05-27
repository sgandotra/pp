package com.monitoreverywhere.jvmmon.stats;

import com.monitoreverywhere.jvmmon.stats.RunTimeStatCollector.RunTimeStat;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.EnumMap;
import java.util.Map;

public final class RunTimeStatCollector implements StatsCollector<EnumMap<RunTimeStat,String>> {

    private static final long serialVersionUID = -7613362632713395607L;

    public enum RunTimeStat {
        BOOTCLASSPATH,
        CLASSPATH,
        INPUTARGS,
        LIBRARYPATH,
        NAME,
        SPEC,
        STARTTIME,
        SYSTEMPROPERTIES,
        UPTIME,
        VMVER
    }

    public RunTimeStatCollector() {}

    public EnumMap<RunTimeStat, String> getStats() {
        RuntimeMXBean bean              = ManagementFactory.getRuntimeMXBean();
        EnumMap<RunTimeStat,String> map = new EnumMap<RunTimeStat,String>(RunTimeStat.class);
        
        map.put(RunTimeStat.BOOTCLASSPATH, bean.getBootClassPath());
        map.put(RunTimeStat.CLASSPATH, bean.getClassPath());
        map.put(RunTimeStat.INPUTARGS, bean.getInputArguments().toString());
        map.put(RunTimeStat.LIBRARYPATH, bean.getLibraryPath());
        map.put(RunTimeStat.NAME, bean.getName());
        
        map.put(RunTimeStat.SPEC,getSpec(bean));
        
        map.put(RunTimeStat.STARTTIME, String.valueOf(bean.getStartTime()));
        
        Map<String,String> systemProperties = bean.getSystemProperties();    
        map.put(RunTimeStat.SYSTEMPROPERTIES, getSystemPropertyAsString(systemProperties));
        
        map.put(RunTimeStat.UPTIME, String.valueOf(bean.getUptime()));
        map.put(RunTimeStat.VMVER, getVmVersion(bean));
        
        return map;
    }

    private String getSystemPropertyAsString(Map<String,String> map) {
        StringBuilder builder = new StringBuilder();

        for (String property : map.keySet()) {
            builder.append(property + "=" + map.get(property) + "\r\n");
        }

        return builder.toString();
    }

    private String getSpec( RuntimeMXBean bean ) {
        return bean.getSpecName() + "-" + bean.getSpecVendor() + " , " + bean.getSpecVersion();
    }

    private String getVmVersion(RuntimeMXBean bean) {
        return bean.getVmName() + " " + bean.getVmVendor() + " " + bean.getVmVersion();
    }
}
