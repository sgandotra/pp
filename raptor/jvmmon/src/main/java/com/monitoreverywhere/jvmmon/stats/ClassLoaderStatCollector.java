package com.monitoreverywhere.jvmmon.stats;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public final class ClassLoaderStatCollector implements StatsCollector<ImmutableMap<String,Map<String,Double>>> {

    private static final long serialVersionUID = 6368274466541924266L;

    public enum ClassLoader {
        LOADED,
        TOTALLOADED,
        UNLOADED
    }
    
    public ClassLoaderStatCollector() { }
    
    
    public ImmutableMap<String,Map<String,Double>> getStats() {
    	Map<String,Double> map = new HashMap<String,Double>();
        
        ClassLoadingMXBean classMxBean = ManagementFactory.getClassLoadingMXBean();
        
        map.put(ClassLoader.LOADED.name().toLowerCase(), Double.valueOf(classMxBean.getLoadedClassCount()));
        map.put(ClassLoader.TOTALLOADED.name().toLowerCase(), Double.valueOf(classMxBean.getTotalLoadedClassCount()));
        map.put(ClassLoader.UNLOADED.name().toLowerCase(), Double.valueOf(classMxBean.getUnloadedClassCount()));
        
        
        return ImmutableMap.of("classloader", map);
    }
   
}
