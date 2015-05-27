/*******************************************************************************
 * Copyright (c) 2006-2015, PayPal Pvt Ltd, All rights reserved
 * Project : annotations-shared
 * Package : com.paypal.loadandperformance.util
 * Class Name : ClassloaderUtils.java
 * Sub Project: annotations-shared
 * Created on : Apr 24, 2015
 * Created by : gthattiyottu
 ******************************************************************************/
package com.ptaas.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


// TODO: Auto-generated Javadoc
/**
 * The Class ClassloaderUtils.
 */
public class ClassloaderUtils {
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getGlobal();
    
    /** The Constant UNCHECKED. */
    private static final String UNCHECKED="unchecked";
      
      /** The Constant URL_PROTOCOL_WSJAR. */
      private static final String URL_PROTOCOL_WSJAR = "wsjar";
   
    private ClassloaderUtils() {
            super();
    }

    /**
 * Load all resources with a given name, potentially aggregating all results
 * from the searched classloaders.  If no results are found, the resource name
 * is prepended by '/' and tried again.
 *
 * This method will try to load the resources using the following methods (in order):
 * <ul>
 *  <li>From Thread.currentThread().getContextClassLoader()
 *  <li>From ClassLoaderUtil.class.getClassLoader()
 *  <li>callingClass.getClassLoader()
 * </ul>
 *
 * @param resourceName The name of the resources to load
 * @param callingClass The Class object of the calling object
 */
    @SuppressWarnings(UNCHECKED)
    public static Iterator<URL> getResources(String resourceName, Class callingClass, boolean aggregate) throws IOException {

     AggregateIterator<URL> iterator = new AggregateIterator<URL>();

     iterator.addEnumeration(Thread.currentThread().getContextClassLoader().getResources(resourceName));

     if (!iterator.hasNext() || aggregate) {
         iterator.addEnumeration(ClassloaderUtils.class.getClassLoader().getResources(resourceName));
     }

     if (!iterator.hasNext() || aggregate) {
         ClassLoader cl = callingClass.getClassLoader();

         if (cl != null) {
             iterator.addEnumeration(cl.getResources(resourceName));
         }
     }

     if (!iterator.hasNext() && (resourceName != null) && (resourceName.charAt(0) != '/')) {
         return getResources('/' + resourceName, callingClass, aggregate);
     }

     return iterator;
 }

/**
* Load a given resource.
*
* This method will try to load the resource using the following methods (in order):
* <ul>
*  <li>From Thread.currentThread().getContextClassLoader()
*  <li>From ClassLoaderUtil.class.getClassLoader()
*  <li>callingClass.getClassLoader()
* </ul>
*
* @param resourceName The name IllegalStateException("Unable to call ")of the resource to load
* @param callingClass The Class object of the calling object
*/
    @SuppressWarnings(UNCHECKED)
    public static URL getResource(String resourceName, Class callingClass) {
    URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);

    if (url == null) {
        url = ClassloaderUtils.class.getClassLoader().getResource(resourceName);
    }

    if (url == null) {
        ClassLoader cl = callingClass.getClassLoader();

        if (cl != null) {
            url = cl.getResource(resourceName);
        }
    }

    if ((url == null) && (resourceName != null) && (resourceName.charAt(0) != '/')) {
        return getResource('/' + resourceName, callingClass);
    }

    return url;
}

/**
* This is a convenience method to load a resource as a stream.
*
* The algorithm used to find the resource is given in getResource()
*
* @param resourceName The name of the resource to load
* @param callingClass The Class object of the calling object
*/
    @SuppressWarnings(UNCHECKED)
    public static InputStream getResourceAsStream(String resourceName, Class callingClass) {
    URL url = getResource(resourceName, callingClass);
    url = getProperUrlPattern(url);
    try {
        return (url != null) ? url.openStream() : null;
    } catch (IOException e) {
        return null;
    }
}


private static URL getProperUrlPattern(URL url) {
     String urlString  = url.toString();
     if(urlString.startsWith(URL_PROTOCOL_WSJAR)){
            
                     urlString =  urlString.substring(2);
                     try {
                                    return new URL(urlString);
                            } catch (MalformedURLException e) {
                                    LOG.log(Level.SEVERE,"error in jar url certaion", e);
                                    return url;
                            }
            
     }
            return url;
    }

    /**
* Load a class with a given name.
*
* It will try to load the class in the following order:
* <ul>
*  <li>From Thread.currentThread().getContextClassLoader()
*  <li>Using the basic Class.forName()
*  <li>From ClassLoaderUtil.class.getClassLoader()
*  <li>From the callingClass.getClassLoader()
* </ul>
*
* @param className The name of the class to load
* @param callingClass The Class object of the calling object
* @throws ClassNotFoundException If the class cannot be found anywhere.
*/
@SuppressWarnings(UNCHECKED)
    public static Class loadClass(String className, Class callingClass) throws ClassNotFoundException {
    try {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    } catch (ClassNotFoundException e) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            try {
                return ClassloaderUtils.class.getClassLoader().loadClass(className);
            } catch (ClassNotFoundException exc) {
                return callingClass.getClassLoader().loadClass(className);
            }
        }
    }
}

/**
 * The Class AggregateIterator.
 * 
 * @param <E>
 *            the element type
 */
protected static final class AggregateIterator<E> implements Iterator<E> {

    /** The enums. */
    private List<Enumeration<E>> enums = new LinkedList<Enumeration<E>>();
    
    /** The cur. */
    private Enumeration<E> cur = null;
    
    /** The next. */
    private E next = null;
    
    /** The loaded. */
    private Set<E> loaded = new HashSet<E>();

    /**
     * @param e
     * @return
     */
    @SuppressWarnings("unchecked")
            public AggregateIterator addEnumeration(Enumeration<E> e) {
        if (e.hasMoreElements()) {
            if (cur == null) {
                cur = e;
                next = e.nextElement();
                loaded.add(next);
            } else {
                enums.add(e);
            }
        }
        return this;
    }

    /**
     * Determines if next element is present.
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return (next != null);
    }

    /**
     * Retrieves next element.
     * @see java.util.Iterator#next()
     */
    public E next() {
        if (next != null) {
            E prev = next;
            next = loadNext();
            return prev;
        } else {
            throw new NoSuchElementException();
        }
    }

    private Enumeration<E> determineCurrentEnumeration() {
        if (cur != null && !cur.hasMoreElements()) {
            if (enums.size() > 0) {
                cur = ((LinkedList<Enumeration<E>>)enums).removeLast();
            } else {
                cur = null;
            }
        }
        return cur;
    }

    private E loadNext() {
        if (determineCurrentEnumeration() != null) {
            E tmp = cur.nextElement();
           while (loaded.contains(tmp)) {
                tmp = loadNext();
                if (tmp == null) {
                    break;
                }
            }
            if (tmp != null) {
                loaded.add(tmp);
            }
            return tmp;
        }
        return null;

    }

    /**
     * Removes element.
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

}
