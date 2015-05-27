package com.monitoreverywhere.jvmmon.stats;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class GarbageStatCollector 
    implements StatsCollector<ImmutableMap<String,Map<String,Double>>> {

    private static final long serialVersionUID = -6449473912103387001L;

    public enum GarbageCollection {
        COUNT,
        TIME
    }
    
    
    public ImmutableMap<String, Map<String, Double>> getStats() {       
        List<GarbageCollectorMXBean> gcBeans    = ManagementFactory.getGarbageCollectorMXBeans();
        Builder<String, Map<String, Double>> maps   
            = new ImmutableMap.Builder<String, Map<String, Double>>();
        
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            Map<String,Double> map  
                = new HashMap<String,Double>();
            map.put(GarbageCollection.COUNT.name().toLowerCase(), Double.valueOf(gcBean.getCollectionCount()));
            map.put(GarbageCollection.TIME.name().toLowerCase(), Double.valueOf(gcBean.getCollectionTime()));
            maps.put(gcBean.getName(),map);
        }
        
        return maps.build();
    }
    
    
}
