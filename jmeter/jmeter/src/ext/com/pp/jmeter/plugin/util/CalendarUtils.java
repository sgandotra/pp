package com.pp.jmeter.plugin.util;

import java.util.Date;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Util Class to get Date in different formats
 * @author sagandotra
 *
 */

public class CalendarUtils {
	
	private static final String ISO_FORMAT_NO_MILLIS = "yyyy-MM-dd'T'HH:mm:ss";
	private CalendarUtils() {}
	
	/**
	 * Get Current time as ISO8601 Format 
	 * 
	 * @return
	 */
	public static String getLocalDateTimeAsISO8601() {
		return new LocalDateTime().toString(ISO_FORMAT_NO_MILLIS);
	}
	
	/**
	 * Convert NmonZZZ Date Format {@link NMON_DATE_FORMAT} to java.util.Date
	 *  
	 * @param sDate
	 * @return
	 */
	public static Date parseNmonZZZAsDate(String sDate) {
		return DateTimeFormat.forPattern(ISO_FORMAT_NO_MILLIS).parseDateTime(sDate).toDate();
	}
	
	/**
	 * Convert NmonZZZ Date Format {@link NMON_DATE_FORMAT} to java.util.Date.toString()
	 * @param sDate
	 * @return
	 */
	
	public static String parseNmonZZZAsLocalDateTimeString(String sDate) {
		return new LocalDateTime(parseNmonZZZAsDate(sDate).getTime()).toString(ISO_FORMAT_NO_MILLIS);
	}
}
