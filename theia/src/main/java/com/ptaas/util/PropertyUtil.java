/*******************************************************************************
 * Copyright (c) 2006-2015, PayPal Pvt Ltd, All rights reserved
 * Project : annotations-shared
 * Package : com.paypal.loadandperformance.util
 * Class Name : PropertyUtil.java
 * Sub Project: annotations-shared
 * Created on : Apr 24, 2015
 * Created by : gthattiyottu
 ******************************************************************************/
package com.ptaas.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// TODO: Auto-generated Javadoc
/**
 * The Class PropertyUtil.
 */
public class PropertyUtil {

	/** The Constant properties. */
	private static final Properties properties = new Properties();

	/** The property util. */
	private static PropertyUtil propertyUtil = null;

	/** The Constant loadedFiles. */
	private static final Map<String, String> loadedFiles = new HashMap<String, String>();

	/** The Constant LOAD_N_PERFORMANCE_PROPERTIES. */
	private static final String LOAD_N_PERFORMANCE_PROPERTIES = "loadnperformance.properties";
	private static final String METRICES_PROPERTIES = "metrices.properties";

	/** The loading class. */
	private static Class loadingClass = PropertyUtil.class;

	private PropertyUtil() {
		InputStream inputStream = null;
		if (!loadedFiles.containsKey(LOAD_N_PERFORMANCE_PROPERTIES)) {

			if (ClassloaderUtils.getResourceAsStream(
					LOAD_N_PERFORMANCE_PROPERTIES, loadingClass) != null) {
				inputStream = ClassloaderUtils.getResourceAsStream(
						LOAD_N_PERFORMANCE_PROPERTIES, loadingClass);
			}
			if (inputStream != null) {
				try {
					properties.load(inputStream);
					loadedFiles.put(LOAD_N_PERFORMANCE_PROPERTIES,
							LOAD_N_PERFORMANCE_PROPERTIES);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					System.out.println(" loadnperformance.properties ");
					Enumeration keys = properties.keys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						String value = (String) properties.get(key);
						System.out.println(key + ": " + value);
					}
				}
			}
		}
		if (!loadedFiles.containsKey(METRICES_PROPERTIES)) {

			if (ClassloaderUtils.getResourceAsStream(
					METRICES_PROPERTIES, loadingClass) != null) {
				inputStream = ClassloaderUtils.getResourceAsStream(
						METRICES_PROPERTIES, loadingClass);
			}
			if (inputStream != null) {
				try {
					properties.load(inputStream);
					loadedFiles.put(METRICES_PROPERTIES,
							METRICES_PROPERTIES);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					System.out.println(" loadnperformance.properties ");
					Enumeration keys = properties.keys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						String value = (String) properties.get(key);
						System.out.println(key + ": " + value);
					}
				}
			}
		}
	}

	/**
	 * Gets the single instance of PropertyUtil.
	 * 
	 * @return single instance of PropertyUtil
	 */
	public static PropertyUtil getInstance() {
		if (propertyUtil == null) {
			propertyUtil = new PropertyUtil();
		}
		return propertyUtil;
	}

	public static PropertyUtil getInstance(Class clazz) {
		if (propertyUtil == null) {
			loadingClass = clazz;
			propertyUtil = new PropertyUtil();
		}
		return propertyUtil;
	}

	public String getParameterValue(String key, String defaultValue) {
		String value = (String) properties.get(key);
		if (value == null || value.trim().length() == 0) {
			if (key != null && key.trim().length() > 0) {
				if (key.startsWith(UtilConstants.DEFAULT)) {
					value = defaultValue;
				} else {
					key = UtilConstants.DEFAULT + key;
					value = getParameterValue(key, defaultValue);
				}
			} else {
				value = defaultValue;
			}

		}
		return value;
	}

	public boolean getParameterValue(String key, boolean defaultValue) {
		String value = (String) properties.get(key);
		boolean booleanValue = false;
		if (value == null || value.trim().length() == 0) {
			if (key != null && key.trim().length() > 0) {
				if (key.startsWith(UtilConstants.DEFAULT)) {
					booleanValue = defaultValue;
				} else {
					key = UtilConstants.DEFAULT + key;
					booleanValue = getParameterValue(key, defaultValue);
				}
			} else {
				booleanValue = defaultValue;
			}
		} else {
			if (value.trim().equalsIgnoreCase("true")
					|| value.trim().equalsIgnoreCase("on")
					|| value.trim().equalsIgnoreCase("enabled")) {
				booleanValue = true;
			}
		}
		return booleanValue;
	}

	public int getParameterValue(String key, int defaultValue) {
		String value = (String) properties.get(key);
		int intValue = 0;
		if (value == null || value.trim().length() == 0) {
			if (key != null && key.trim().length() > 0) {
				if (key.startsWith(UtilConstants.DEFAULT)) {
					intValue = defaultValue;
				} else {
					key = UtilConstants.DEFAULT + key;
					intValue = getParameterValue(key, defaultValue);
				}
			} else {
				intValue = defaultValue;
			}
		} else {
			try {
				intValue = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				intValue = defaultValue;
			}
		}
		return intValue;
	}

	public long getParameterValue(String key, long defaultValue) {
		String value = (String) properties.get(key);
		long longValue = 0;
		if (value == null || value.trim().length() == 0) {
			if (key != null && key.trim().length() > 0) {
				if (key.startsWith(UtilConstants.DEFAULT)) {
					longValue = defaultValue;
				} else {
					key = UtilConstants.DEFAULT + key;
					longValue = getParameterValue(key, defaultValue);
				}
			} else {
				longValue = defaultValue;
			}
		} else {
			try {
				longValue = Long.parseLong(value);
			} catch (NumberFormatException e) {
				longValue = defaultValue;
			}
		}
		return longValue;
	}

	public double getParameterValue(String key, double defaultValue) {
		String value = (String) properties.get(key);
		double doubleValue = 0;
		if (value == null || value.trim().length() == 0) {
			if (key != null && key.trim().length() > 0) {
				if (key.startsWith(UtilConstants.DEFAULT)) {
					doubleValue = defaultValue;
				} else {
					key = UtilConstants.DEFAULT + key;
					doubleValue = getParameterValue(key, defaultValue);
				}
			} else {
				doubleValue = defaultValue;
			}
		} else {
			try {
				doubleValue = Double.parseDouble(value);
			} catch (NumberFormatException e) {
				doubleValue = defaultValue;
			}
		}
		return doubleValue;
	}

	public float getParameterValue(String key, float defaultValue) {
		String value = (String) properties.get(key);
		float floatValue = 0;
		if (value == null || value.trim().length() == 0) {
			if (key != null && key.trim().length() > 0) {
				if (key.startsWith(UtilConstants.DEFAULT)) {
					floatValue = defaultValue;
				} else {
					key = UtilConstants.DEFAULT + key;
					floatValue = getParameterValue(key, defaultValue);
				}
			} else {
				floatValue = defaultValue;
			}
		} else {
			try {
				floatValue = Float.parseFloat(value);
			} catch (NumberFormatException e) {
				floatValue = defaultValue;
			}
		}
		return floatValue;
	}
}
