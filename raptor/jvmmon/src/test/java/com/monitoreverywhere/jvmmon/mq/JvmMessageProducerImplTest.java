package com.monitoreverywhere.jvmmon.mq;

import org.junit.Test;

public class JvmMessageProducerImplTest {

    @Test
    public void testConfiguration() {
        JvmMessageProducer impl = JvmMessageProducerImpl.getInstance();
    }
    
    @Test
    public void testConfigurationWithUserDefinedPort() {
        System.setProperty("monitoreverywhere.broker.port", "8888");
        JvmMessageProducer impl = JvmMessageProducerImpl.getInstance();
    }
    
    @Test
    public void testConfigurationWithUserDefinedInvalidPort() {
        System.setProperty("monitoreverywhere.broker.port", "blah");
        JvmMessageProducer impl = JvmMessageProducerImpl.getInstance();
    }
/*   
    @Test
    public void testSetup() throws JvmMonSetupException {
        JvmMessageProducer impl = JvmMessageProducerImpl.getInstance();
        impl.setup();
    }

    @Test
    public void testSend() {
        fail("Not yet implemented");
    }

    @Test
    public void testDestroy() {
        fail("Not yet implemented");
    }
*/
}
