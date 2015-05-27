package com.monitoreverywhere.jvmmon;


import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;




/**
 * 
 * 
 *
 */
public class JvmMonServletContainerInitializer implements ServletContainerInitializer {
    
    
    @Override
    public void onStartup(Set<Class<?>> webInitializers, ServletContext ctx)
            throws ServletException {
        System.err.println("-------------------------------------------------------------------------");
        System.err.println("-------------------------------------------------------------------------");
        System.err.println("");
        System.err.println("Registering custom listener with servlet container for monitoring");
        System.err.println("");
        System.err.println("-------------------------------------------------------------------------");
        System.err.println("-------------------------------------------------------------------------");

        ctx.addListener(JvmMonServletContextListener.class);
       
    }
}
