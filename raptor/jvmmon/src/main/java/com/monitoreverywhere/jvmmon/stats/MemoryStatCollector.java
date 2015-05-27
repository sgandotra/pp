package com.monitoreverywhere.jvmmon.stats;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;


/**
 * Gather Memory Statistics
 * 
 * @author sagandotra
 *
 */
public final class MemoryStatCollector 
    implements StatsCollector<ImmutableMap<String, Map<String,Double>>> {
  
    private static final long serialVersionUID = 2619397283452253116L;

    public enum MemoryType {
        HEAP,
        NONHEAP
    }
    
    
    public MemoryStatCollector() {}    
    
    
    public ImmutableMap<String, Map<String,Double>> getStats() {
    	
    	 Builder<String, Map<String,Double>> memoryUsages 
         	= new ImmutableMap.Builder<String, Map<String,Double>>();
    	 
        
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        
        Map<String,Double> heap = new HashMap<String,Double>();
        Map<String,Double> nonHeap = new HashMap<String,Double>();
        
        heap.put("init", (double)memory.getHeapMemoryUsage().getInit());
        heap.put("max", (double)memory.getHeapMemoryUsage().getMax());
        heap.put("committed", (double)memory.getHeapMemoryUsage().getCommitted());
        heap.put("used", (double) memory.getHeapMemoryUsage().getUsed());
        
        
        nonHeap.put("init", (double)memory.getNonHeapMemoryUsage().getInit());
        nonHeap.put("max", (double)memory.getNonHeapMemoryUsage().getMax());
        nonHeap.put("committed", (double)memory.getNonHeapMemoryUsage().getCommitted());
        nonHeap.put("used", (double) memory.getNonHeapMemoryUsage().getUsed());
              
        memoryUsages.put(MemoryType.HEAP.name().toLowerCase(),heap );
        memoryUsages.put(MemoryType.NONHEAP.name().toLowerCase(),nonHeap);
        
        
        return memoryUsages.build();
    }

   
    
}
