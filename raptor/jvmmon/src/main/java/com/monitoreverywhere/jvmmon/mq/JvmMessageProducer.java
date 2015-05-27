package com.monitoreverywhere.jvmmon.mq;


import java.util.concurrent.ArrayBlockingQueue;

import com.monitoreverywhere.jvmmon.model.Stats;

public interface JvmMessageProducer {

    public void setup();
    
    public void send(Stats stats);
    
    public ArrayBlockingQueue<Stats> getQueue();
    
    public void shutdown() ;

    public void run();

    public String getGraphitePort();
}
