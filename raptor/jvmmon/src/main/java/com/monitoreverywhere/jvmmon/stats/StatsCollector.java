package com.monitoreverywhere.jvmmon.stats;

import java.io.Serializable;

public interface StatsCollector<E> extends Serializable {

    public E getStats();
}
