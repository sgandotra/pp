package com.monitoreverywhere.jvmmon.profiler;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class SamplingProfilerTest {

    private static final int NUM_OF_HOTSPOTS = 100;

    private  ScheduledExecutorService service;

    @Before
    public void before() {
        service = Executors.newSingleThreadScheduledExecutor();
    }

    @Test
    public void testGetHotspots() throws InterruptedException {

        /*
		Thread t = new Thread() {

			@Override
			public void run() {
				int i = 0;
				while(i < 10000) {
					i++;
				}
				SamplingProfiler profiler = new SamplingProfiler(Collections.EMPTY_LIST,null);
				profiler.update();
				System.out.println("Getting hotspots..");
				List<SampledMethod> hotspots = profiler.getHotspots(NUM_OF_HOTSPOTS);

				for(SampledMethod hotspot : hotspots) {
					System.out.println(hotspot + " " + hotspot.getCount());
				}
			}

		};

		service.scheduleAtFixedRate(t, 5000L, 5000L,TimeUnit.MILLISECONDS);
		Thread.sleep(Long.MAX_VALUE); */

    }

}
