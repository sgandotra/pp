package com.monitoreverywhere.jvmmon.memory;

import com.google.common.collect.ImmutableMap;
import com.monitoreverywhere.jvmmon.stats.MemoryPoolStatCollector;

import org.junit.Test;

import static org.junit.Assert.*;

import java.lang.management.MemoryUsage;
import java.util.Map;




public class MemoryStatCollectorTest {

    @Test
    public void testGetMemoryPoolUsage() {

        String[] poolNames = new String[] { "Code Cache",
                "PS Eden Space",
                "PS Survivor Space",
                "PS Old Gen",
                "PS Perm Gen"
        };

        MemoryPoolStatCollector collector = new MemoryPoolStatCollector();

        ImmutableMap<String, Map<String, Double>> memoryUsages = collector.getStats();

        assertArrayEquals(poolNames,memoryUsages.keySet().toArray());

    }

}
