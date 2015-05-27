package com.monitoreverywhere.jvmmon.stats;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MemoryPoolStatCollector implements
        StatsCollector<ImmutableMap<String, Map<String,Double>>> {

    private static final long serialVersionUID = 7529056411494213734L;

    public MemoryPoolStatCollector() {
    }

    public ImmutableMap<String, Map<String,Double>> getStats() {

        List<MemoryPoolMXBean> memoryPools = ManagementFactory.getMemoryPoolMXBeans();
        
        Builder<String, Map<String,Double>> memoryPoolUsages 
            = new ImmutableMap.Builder<String, Map<String,Double>>();

        for (MemoryPoolMXBean bean : memoryPools) {
        	if(bean.isValid()) {
	        	Map<String,Double> map = new HashMap<String,Double>();
				map.put("init",(double)bean.getUsage().getInit());
				map.put("max",(double) bean.getUsage().getMax());
				map.put("used",(double) bean.getUsage().getUsed());
				map.put("committed",(double) bean.getUsage().getCommitted());
	        	
	            memoryPoolUsages.put(bean.getName(), map);
        	}
        }

        return memoryPoolUsages.build();

    }
}
